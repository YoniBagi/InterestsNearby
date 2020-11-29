package com.interestsnearby.dataModel

import com.google.gson.annotations.SerializedName

data class InterestsPlaces(
    @SerializedName("results")
    val places: List<Place>,
    val status: String
)