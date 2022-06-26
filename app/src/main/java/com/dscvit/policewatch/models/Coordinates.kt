package com.dscvit.policewatch.models


import com.google.gson.annotations.SerializedName

data class Coordinates(
    @SerializedName("x")
    val x: Double,
    @SerializedName("y")
    val y: Double
)