package com.dscvit.policewatch.models

import com.google.gson.annotations.SerializedName

data class Officer(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("patroller_id")
    val patrollerID: Int,
    @SerializedName("location")
    val coordinates: Coordinates,
    @SerializedName("label")
    val label: String
)