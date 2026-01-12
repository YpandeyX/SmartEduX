package com.team.squadx

data class Faculty(
    var id: String = "",
    val name: String = "",
    val department: String = "",
    val designation: String = "",
    val subjects: List<String> = emptyList(),
    val phone: String = "",
    val email: String = "",
    val photoUrl: String = ""
)
