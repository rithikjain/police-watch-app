package com.dscvit.policewatch.models

import com.google.gson.annotations.SerializedName

data class Officer(
    @SerializedName("patroller_id")
    val patrollerID: Int,
    @SerializedName("location")
    val coordinates: Coordinates
)