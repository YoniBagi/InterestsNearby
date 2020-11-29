package com.interestsnearby.dataModel

import com.google.gson.annotations.SerializedName

data class WayPoint(
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lng")
    val longitude: Double)