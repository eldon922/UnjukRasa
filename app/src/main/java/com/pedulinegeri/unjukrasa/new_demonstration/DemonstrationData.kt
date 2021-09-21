package com.pedulinegeri.unjukrasa.new_demonstration

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class DemonstrationData(
    val initiatorUid: String = "",
    val title: String = "",
    val to: String = "",
    val description: String = "",
    var youtube_video: String = "",
    var road_protests: Boolean = false,
    var datetime: Date = Calendar.getInstance().time,
    var location: String = "",
    @ServerTimestamp var creationDate: Date = Calendar.getInstance().time
)
