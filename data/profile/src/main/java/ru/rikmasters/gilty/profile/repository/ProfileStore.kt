package ru.rikmasters.gilty.profile.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.profile.models.ProfileCategories
import ru.rikmasters.gilty.profile.models.ProfileMeets
import ru.rikmasters.gilty.profile.paging.*
import ru.rikmasters.gilty.shared.model.DataStateTest
import ru.rikmasters.gilty.shared.model.DataStateTest.Success
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.models.Avatar
import ru.rikmasters.gilty.shared.models.AvatarAmount
import ru.rikmasters.gilty.shared.models.Profile
import java.io.File

class ProfileStore(
    override val webSource: ProfileWebSource,
    override val primarySource: DbSource,
): OfflineFirstRepository<KtorSource, DbSource>(
    webSource, primarySource
) {
    
    suspend fun deleteProfile() {
        primarySource.deleteAll<Profile>()
        primarySource.deleteAll<ProfileCategories>()
        primarySource.deleteAll<ProfileMeets>()
        primarySource.deleteAll<AvatarAmount>()
        primarySource.deleteAll<Avatar>()
    }
    
    suspend fun checkCompletable() =
        getProfile(true).isCompleted
    
    private suspend fun uploadProfile(
        forceWeb: Boolean,
    ): Profile {
        if(!forceWeb)
            primarySource.find<Profile>()
                ?.let { return it }
        
        return webSource.getUserData().on(
            success = { it },
            loading = { Profile() },
            error = { Profile() }
        ).also {
            primarySource.deleteAll<Profile>()
            primarySource.save(it)
        }
    }
    
    suspend fun storageProfile() =
        primarySource.find<Profile>()
    
    suspend fun getProfile(forceWeb: Boolean) =
        uploadProfile(forceWeb).map()
    
    suspend fun deleteHidden(imageId: String) =
        primarySource.deleteById<Avatar>(imageId).let {
            webSource.deleteHidden(
                albumId = uploadProfile(false)
                    .albumPrivate?.id ?: "",
                imageId = imageId
            )
        }

    suspend fun changeAlbumPosition(imageId: String, position:Int) = webSource.changeAlbumPosition(uploadProfile(false)
        .albumPrivate?.id ?: "", imageId, position)
    suspend fun addHidden(files: List<File>) =

        webSource.addHidden(
            albumId = uploadProfile(false)
                .albumPrivate?.id ?: "",
            files = files
        ).let { list ->
            primarySource.saveAll(
                list.on(
                    success = { it },
                    loading = { emptyList() },
                    error = { emptyList() }
                )
            )
            list
        }
    
    fun getUserHiddenPaging(
            albumId:String? = null,
        ) = Pager(
        config = PagingConfig(
            pageSize = 15,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            HiddenPhotosPagingSource(
                webSource = webSource,
                localSource = primarySource,
                albumId = albumId,
            )
        }
    ).flow
    
    suspend fun getUserHidden(
        forceWeb: Boolean,
    ): DataStateTest<List<AvatarModel>> {
        val profile = uploadProfile(false)
        
        if(!forceWeb) primarySource
            .findAll<Avatar>()
            .filter {
                it.albumId == profile.albumPrivate?.id &&
                        it.ownerId == profile.id
            }.let {
                if(it.isNotEmpty()) {
                    return Success(
                        it.map { a ->
                            a.map()
                        }
                    )
                }
            }
        
        logD("UPDATE IMAGES")
        primarySource.deleteAll<Avatar>()
        primarySource.deleteAll<AvatarAmount>()
        
        val request = webSource
            .getFiles(profile.albumPrivate?.id ?: "")
        
        val list = request.on(
            success = { it },
            loading = { emptyList<Avatar>() to 0 },
            error = { emptyList<Avatar>() to 0 }
        )
        
        primarySource.saveAll(list.first)
        primarySource.save(AvatarAmount(list.second))
        
        return Success(list.first.map { it.map() })
    }
    
    suspend fun updateProfile(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
        orientation: OrientationModel? = null,
    ): DataStateTest<Unit> {
        primarySource.find<Profile>()?.let {
            primarySource.save(
                it.copy(
                    username = username ?: it.username,
                    aboutMe = aboutMe ?: it.aboutMe,
                    age = age ?: it.age,
                    gender = gender?.name ?: it.gender,
                    orientation = orientation ?: it.orientation
                )
            )
        } ?: getProfile(true)
        return webSource.setUserData(
            username, aboutMe, age,
            gender, orientation?.id
        )
    }
    
    fun getObservers(
        query: String,
        type: ObserversType,
    ): Flow<PagingData<UserModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProfileObserversPagingSource(
                    webSource = webSource,
                    type = type,
                    query = query
                )
            }
        ).flow
    }
    
    fun getFiles(
        albumId: String,
    ): Flow<PagingData<AvatarModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AlbumPagingSource(
                    webSource = webSource,
                    albumId = albumId,
                )
            }
        ).flow
    }
    
    fun getMeetResponds(
        meetId: String,
    ): Flow<PagingData<RespondModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProfileRespondsPagingSource(
                    webSource = webSource,
                    meetId = meetId
                )
            }
        ).flow
    }
    
    fun getResponds(
        type: RespondType,
    ): Flow<PagingData<MeetWithRespondsModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProfileMeetRespondsPagingSource(
                    webSource = webSource,
                    type = type
                )
            }
        ).flow
    }
    
    fun getUserMeets(
        type: MeetingsType,
    ): Flow<PagingData<MeetingModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProfileMeetsPagingSource(
                    webSource = webSource,
                    type = type
                )
            }
        ).flow
    }
    
    suspend fun updateUserCategories(): DataStateTest<List<CategoryModel>> {
        primarySource.deleteAll<ProfileCategories>()
        
        val request =
            webSource.getUserCategories()
        
        val list = request.on(
            success = { it },
            loading = { emptyList() },
            error = { emptyList() }
        )
        
        primarySource.save(ProfileCategories(list))
        
        return Success(list.map { it.map() })
    }
    
    suspend fun getUserCategories(
        forceWeb: Boolean,
    ): DataStateTest<List<CategoryModel>> {
        if(!forceWeb) primarySource
            .find<ProfileCategories>()
            ?.let {
                return Success(
                    it.list.map { c ->
                        c.map()
                    }
                )
            }
        return updateUserCategories()
    }
    
    suspend fun getHiddenPhotosAmount() =
        primarySource.find<AvatarAmount>()?.amount ?: 0

    suspend fun deleteHiddenPhotosAmount() =
        primarySource.deleteAll<AvatarAmount>()
}
