package com.dscvit.policewatch.models


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("is_supervisor")
    val isSupervisor: Boolean,
    @SerializedName("last_login")
    val lastLogin: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("ph_no")
    val phoneNo: String
)