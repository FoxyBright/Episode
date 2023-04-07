package ru.rikmasters.gilty.profile.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.profile.models.ProfileCategories
import ru.rikmasters.gilty.profile.models.ProfileMeets
import ru.rikmasters.gilty.profile.paging.*
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
import ru.rikmasters.gilty.shared.models.Profile
import ru.rikmasters.gilty.shared.models.meets.Category
import java.io.File

class ProfileStore(

    override val webSource: ProfileWebSource,

    override val primarySource: DbSource
) : OfflineFirstRepository<KtorSource, DbSource>(
    webSource,
    primarySource
) {

    suspend fun deleteProfile() {
        primarySource.deleteAll<Profile>()
        primarySource.deleteAll<ProfileCategories>()
        primarySource.deleteAll<ProfileMeets>()
        primarySource.deleteAll<Avatar>()
    }

    suspend fun checkCompletable() =
        getProfile(true).isCompleted

    private suspend fun uploadProfile(
        forceWeb: Boolean
    ): Profile {
        if (!forceWeb) primarySource.find<Profile>()

        val profile = webSource.getUserData()
        primarySource.deleteAll<Profile>()
        primarySource.save(profile)

        return profile
    }

    suspend fun checkProfileStore() =
        primarySource
            .find<Profile>()
            ?.map() != null

    suspend fun getProfile(forceWeb: Boolean) =
        uploadProfile(forceWeb).map()

    fun hiddenFlow() = primarySource
        .listenAll(Avatar::class)
        .map { list ->
            list.map { it.map() }
        }

    suspend fun deleteHidden(imageId: String) {
        webSource.deleteHidden(
            uploadProfile(false)
                .albumPrivate?.id ?: "",
            imageId
        )
        primarySource.deleteById<Avatar>(imageId)
    }

    suspend fun addHidden(files: List<File>) {
        primarySource.saveAll(
            webSource.addHidden(
                uploadProfile(false)
                    .albumPrivate?.id ?: "",
                files
            )
        )
    }

    suspend fun getUserHidden(
        forceWeb: Boolean
    ): List<AvatarModel> {
        val profile = uploadProfile(false)

        fun List<Avatar>.map() = this.map { it.map() }

        if (!forceWeb) primarySource
            .findAll<Avatar>()
            .filter {
                it.albumId == profile.albumPrivate?.id &&
                    it.ownerId == profile.id
            }.let {
                if (it.isNotEmpty()) {
                    return it.map()
                }
            }

        primarySource.deleteAll<Avatar>()
        val list = webSource.getFiles(
            profile.albumPrivate?.id ?: ""
        )
        primarySource.saveAll(list)

        return list.map()
    }

    suspend fun updateProfile(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
        orientation: OrientationModel? = null
    ) {
        webSource.setUserData(
            username,
            aboutMe,
            age,
            gender,
            orientation?.id
        )
    }

    private fun List<Category>.map() = this.map { it.map() }

    fun getObservers(
        query: String,
        type: ObserversType
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
        albumId: String
    ): Flow<PagingData<AvatarModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AlbumPagingSource(
                    webSource = webSource,
                    albumId = albumId
                )
            }
        ).flow
    }

    fun getMeetResponds(
        meetId: String
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
        type: RespondType
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
        type: MeetingsType
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

    suspend fun updateUserCategories(): List<CategoryModel> {
        primarySource.deleteAll<ProfileCategories>()
        val list = webSource.getUserCategories()
        primarySource.save(ProfileCategories(list))
        return list.map()
    }

    suspend fun getUserCategories(
        forceWeb: Boolean
    ): List<CategoryModel> {
        if (!forceWeb) primarySource
            .find<ProfileCategories>()
            ?.let { return it.list.map() }

        return updateUserCategories()
    }
}
