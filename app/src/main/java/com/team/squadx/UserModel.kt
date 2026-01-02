package com.team.squadx

import com.google.firebase.firestore.PropertyName

data class UserModel(
    val uid: String = "",
    // These annotations ensure it reads correctly even if Firestore fields are Capitalized
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("department") @set:PropertyName("department") var department: String = "",
    @get:PropertyName("section") @set:PropertyName("section") var section: String = "",
    @get:PropertyName("rollNumber") @set:PropertyName("rollNumber") var rollNumber: String = "",
    @get:PropertyName("phone") @set:PropertyName("phone") var phone: String = ""
)