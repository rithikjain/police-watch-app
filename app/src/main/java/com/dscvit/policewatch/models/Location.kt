package com.dscvit.policewatch.models


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("location")
    val coordinates: Coordinates
)