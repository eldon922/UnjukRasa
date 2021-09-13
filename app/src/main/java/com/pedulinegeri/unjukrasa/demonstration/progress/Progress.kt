package com.pedulinegeri.unjukrasa.demonstration.progress

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Progress(
    val description: String = "",
    var youtube_video: String = "",
    @ServerTimestamp var creationDate: Date = Calendar.getInstance().time
)
