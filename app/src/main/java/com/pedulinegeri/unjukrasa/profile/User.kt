package com.pedulinegeri.unjukrasa.profile

data class User(
    var id: String = "",
    val name: String = "",
    val participate: ArrayList<String> = arrayListOf(),
    val upvote: ArrayList<String> = arrayListOf(),
    val downvote: ArrayList<String> = arrayListOf(),
    val share: ArrayList<String> = arrayListOf(),
    val involve: ArrayList<String> = arrayListOf(),
    val demonstrations: ArrayList<DemonstrationTitle> = arrayListOf()
)
