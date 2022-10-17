package com.pedulinegeri.unjukrasa.newdemonstration

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class DemonstrationData(
    val initiatorUid: String = "",
    val title: String = "",
    val to: String = "",
    val description: String = "",
    var youtubeVideo: String = "",
    var roadProtest: Boolean = false,
    var datetime: Date = Calendar.getInstance().time,
    var location: String = "",
    @ServerTimestamp var creationDate: Date = Calendar.getInstance().time
)
