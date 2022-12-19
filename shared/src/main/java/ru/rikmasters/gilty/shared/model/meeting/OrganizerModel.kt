package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel

data class OrganizerModel(
    
    val id: String,
    
    val username: String,
    
    val emoji: EmojiModel,
    
    val avatar: AvatarModel,
    
    val age: Int
) {
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        
        other as OrganizerModel
        
        if(id != other.id) return false
        if(username != other.username) return false
        if(emoji != other.emoji) return false
        if(avatar != other.avatar) return false
        if(age != other.age) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + emoji.hashCode()
        result = 31 * result + avatar.hashCode()
        result = 31 * result + age
        return result
    }
}

val DemoOrganizerModel = OrganizerModel(
    "https://placekitten.com/400/800",
    "alina.loon",
    DemoEmojiModel,
    DemoAvatarModel,
    20
)
