package com.pedulinegeri.unjukrasa.new_demonstration

import com.pedulinegeri.unjukrasa.demonstration.person.Person

data class Demonstration(
    val title: String = "",
    val to: String = "",
    val description: String = "",
    val youtube_video: String = "",
    val road_protests: Boolean = false,
    val datetime: String = "",
    val location: String = "",
    val persons: ArrayList<Person> = arrayListOf()
)
