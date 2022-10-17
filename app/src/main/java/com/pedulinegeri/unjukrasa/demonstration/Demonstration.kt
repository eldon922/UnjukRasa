package com.pedulinegeri.unjukrasa.demonstration

import com.pedulinegeri.unjukrasa.demonstration.person.Person
import com.pedulinegeri.unjukrasa.demonstration.progress.Progress
import java.util.*

data class Demonstration(
    var id: String = "",
    val initiatorUid: String = "",
    val title: String = "",
    val to: String = "",
    val description: String = "",
    val youtubeVideo: String = "",
    val roadProtest: Boolean = false,
    val datetime: Date = Calendar.getInstance().time,
    val location: String = "",
    val participate: Long = 0,
    val upvote: Long = 0,
    val downvote: Long = 0,
    val share: Long = 0,
    val persons: ArrayList<Person> = arrayListOf(),
    val progress: ArrayList<Progress> = arrayListOf(),
    val creationDate: Date = Calendar.getInstance().time
)
