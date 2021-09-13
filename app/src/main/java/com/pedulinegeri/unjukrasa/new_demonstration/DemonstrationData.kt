package com.pedulinegeri.unjukrasa.new_demonstration

import com.google.firebase.firestore.ServerTimestamp
import com.google.type.DateTime
import com.pedulinegeri.unjukrasa.demonstration.person.Person
import com.pedulinegeri.unjukrasa.demonstration.progress.Progress
import java.util.*
import kotlin.collections.ArrayList

data class DemonstrationData(
    val initiatorUid: String = "",
    val title: String = "",
    val to: String = "",
    val description: String = "",
    var youtube_video: String = "",
    var road_protests: Boolean = false,
    var datetime: String = "",
    var location: String = "",
    @ServerTimestamp var creationDate: Date = Calendar.getInstance().time
)
