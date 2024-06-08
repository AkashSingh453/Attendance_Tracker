package com.example.attendance_tracker.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName


data class MTracker(
    @Exclude var docid: String? = null,

    val id : String? = null,
    var subject: String? = null,
    var number: Int? = null,

    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String? = null,

)

data class M_User(

    @get:PropertyName("display_name")
    @set:PropertyName("display_name")
    var displayname: String? = null,

    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userid: String? = null,

    )