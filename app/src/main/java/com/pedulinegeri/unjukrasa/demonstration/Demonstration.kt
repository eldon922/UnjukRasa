package com.pedulinegeri.unjukrasa.demonstration

import com.google.type.DateTime
import com.pedulinegeri.unjukrasa.demonstration.person.Person
import com.pedulinegeri.unjukrasa.demonstration.progress.Progress
import java.util.*
import kotlin.collections.ArrayList

data class Demonstration(
    val initiatorUid: String = "",
    val title: String = "",
    val to: String = "",
    val description: String = "",
    val youtube_video: String = "",
    val road_protests: Boolean = false,
    val datetime: Date = Calendar.getInstance().time,
    val location: String = "",
    val participation: Long = 0,
    val upvote: Long = 0,
    val downvote: Long = 0,
    val share: Long = 0,
    val persons: ArrayList<Person> = arrayListOf(),
    val progress: ArrayList<Progress> = arrayListOf(),
    val creationDate: Date = Calendar.getInstance().time
)
