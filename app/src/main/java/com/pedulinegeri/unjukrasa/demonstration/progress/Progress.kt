package com.pedulinegeri.unjukrasa.demonstration.progress

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Progress(
    val description: String = "",
    var youtubeVideo: String = "",
    @ServerTimestamp var creationDate: Date = Calendar.getInstance().time
)
