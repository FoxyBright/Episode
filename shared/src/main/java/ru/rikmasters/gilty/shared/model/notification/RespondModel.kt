package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
//
//{
//    "id": "730c5ff1-ecfa-4cf7-87f5-88a6d5bfdabf",
//    "tags": [
//    {
//        "id": "b32de38c-1078-4177-9a56-5089f09341b1",
//        "title": "Museum"
//    }
//    ],
//    "is_online": true,
//    "organizer": {
//    "id": "af4bc6a5-4c41-4679-a73d-479010043dbe",
//    "gender": "MALE",
//    "username": "roman.kurortnii",
//    "emoji_type": "HEART_EYES",
//    "avatar": {
//    "id": "63dbb3f21de4be00ef05b582",
//    "album_id": "63636ae25062656703034e43",
//    "owner_id": "af4bc6a5-4c41-4679-a73d-479010043dbe",
//    "type": "PHOTO",
//    "thumbnail": {
//    "id": "63dbb3f21de4be00ef05b583",
//    "url": "http://gilty.rikmasters.ru/api/v1/thumbnails/63dbb3f21de4be00ef05b583?policy=eyJjbGllbnRfaXAiOiI4NC4yNTIuMTQzLjk3IiwiYmx1ciI6ZmFsc2UsImV4cGlyZXMiOjE2NzU2OTMyNzcsInBhdGgiOiJcL2FwaVwvdjFcL3RodW1ibmFpbHNcLzYzZGJiM2YyMWRlNGJlMDBlZjA1YjU4MyJ9&hash=wprArcxGDWhPPrclGeUXdw",
//    "width": 410,
//    "height": 640,
//    "filesize": 44294,
//    "mimetype": "image/jpeg"
//},
//    "mimetype": "image/jpeg",
//    "filesize": 243282,
//    "url": "http://gilty.rikmasters.ru/api/v1/albums/63636ae25062656703034e43/files/63dbb3f21de4be00ef05b582?policy=eyJjbGllbnRfaXAiOiI4NC4yNTIuMTQzLjk3IiwiYmx1ciI6ZmFsc2UsImV4cGlyZXMiOjE2NzU2OTMyNzcsInBhdGgiOiJcL2FwaVwvdjFcL2FsYnVtc1wvNjM2MzZhZTI1MDYyNjU2NzAzMDM0ZTQzXC9maWxlc1wvNjNkYmIzZjIxZGU0YmUwMGVmMDViNTgyIn0&hash=XfihV4sVCH4dve2hhMu2Sg",
//    "width": 960,
//    "height": 1280,
//    "has_access": true
//},
//    "thumbnail": {
//    "id": "63dbb3f21de4be00ef05b583",
//    "url": "http://gilty.rikmasters.ru/api/v1/thumbnails/63dbb3f21de4be00ef05b583?policy=eyJjbGllbnRfaXAiOiI4NC4yNTIuMTQzLjk3IiwiYmx1ciI6ZmFsc2UsImV4cGlyZXMiOjE2NzU2OTMyNzcsInBhdGgiOiJcL2FwaVwvdjFcL3RodW1ibmFpbHNcLzYzZGJiM2YyMWRlNGJlMDBlZjA1YjU4MyJ9&hash=wprArcxGDWhPPrclGeUXdw",
//    "width": 410,
//    "height": 640,
//    "filesize": 44294,
//    "mimetype": "image/jpeg"
//},
//    "age": 30,
//    "is_anonymous": false,
//    "is_online": true
//},
//    "responds_count": 1,
//    "responds": [
//    {
//        "id": "2ea2d2c3-af58-4ab1-9764-c4840e8936ab",
//        "author": {
//        "id": "dfa76b92-60c7-49a0-84b2-b54e5415811b",
//        "gender": "MALE",
//        "username": "etatov",
//        "emoji_type": "HEART_EYES",
//        "avatar": {
//        "id": "63aab01ae66f921242081114",
//        "album_id": "63aaafcae66f921242081113",
//        "owner_id": "dfa76b92-60c7-49a0-84b2-b54e5415811b",
//        "type": "PHOTO",
//        "thumbnail": {
//        "id": "63bdc999dbf719d4be056dac",
//        "url": "http://gilty.rikmasters.ru/api/v1/thumbnails/63bdc999dbf719d4be056dac?policy=eyJjbGllbnRfaXAiOiI4NC4yNTIuMTQzLjk3IiwiYmx1ciI6ZmFsc2UsImV4cGlyZXMiOjE2NzU2OTMyNzcsInBhdGgiOiJcL2FwaVwvdjFcL3RodW1ibmFpbHNcLzYzYmRjOTk5ZGJmNzE5ZDRiZTA1NmRhYyJ9&hash=f7zGNPAtubWr82ToQeGMmQ",
//        "width": 410,
//        "height": 640,
//        "filesize": 58632,
//        "mimetype": "image/jpeg"
//    },
//        "mimetype": "image/jpeg",
//        "filesize": 26230,
//        "url": "http://gilty.rikmasters.ru/api/v1/albums/63aaafcae66f921242081113/files/63aab01ae66f921242081114?policy=eyJjbGllbnRfaXAiOiI4NC4yNTIuMTQzLjk3IiwiYmx1ciI6ZmFsc2UsImV4cGlyZXMiOjE2NzU2OTMyNzcsInBhdGgiOiJcL2FwaVwvdjFcL2FsYnVtc1wvNjNhYWFmY2FlNjZmOTIxMjQyMDgxMTEzXC9maWxlc1wvNjNhYWIwMWFlNjZmOTIxMjQyMDgxMTE0In0&hash=xKqisCYiWyibHXUDcV40Qw",
//        "width": 320,
//        "height": 314,
//        "has_access": true
//    },
//        "thumbnail": {
//        "id": "63bdc999dbf719d4be056dac",
//        "url": "http://gilty.rikmasters.ru/api/v1/thumbnails/63bdc999dbf719d4be056dac?policy=eyJjbGllbnRfaXAiOiI4NC4yNTIuMTQzLjk3IiwiYmx1ciI6ZmFsc2UsImV4cGlyZXMiOjE2NzU2OTMyNzcsInBhdGgiOiJcL2FwaVwvdjFcL3RodW1ibmFpbHNcLzYzYmRjOTk5ZGJmNzE5ZDRiZTA1NmRhYyJ9&hash=f7zGNPAtubWr82ToQeGMmQ",
//        "width": 410,
//        "height": 640,
//        "filesize": 58632,
//        "mimetype": "image/jpeg"
//    },
//        "age": 20,
//        "is_anonymous": false,
//        "is_online": true
//    },
//        "comment": "",
//        "photo_access": false
//    }
//    ]
//}


data class RespondModel(
    val id: Int,
    val meet: MeetingModel,
    val comment: String?,
    val sender: OrganizerModel,
    val type: RespondType,
    val hiddenPhoto: List<Pair<AvatarModel, Boolean>>?
)

val DemoSendRespondsModel = RespondModel(
    1,
    DemoMeetingModel,
    null,
    DemoOrganizerModel,
    RespondType.SEND,
    null
)

val DemoReceivedRespondsModel = RespondModel(
    1, DemoMeetingModel,
    "Классно выглядишь, пойдем? Я вроде адекватный))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    listOf(
        Pair(DemoAvatarModel, true),
        Pair(DemoAvatarModel, true),
        Pair(DemoAvatarModel, false),
        Pair(DemoAvatarModel, false)
    )
)

val DemoReceivedRespondModelWithoutPhoto = RespondModel(
    1,
    DemoMeetingModel,
    "Покажи свои фото))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    null
)