package com.interestsnearby.dataModel

data class Place(
    val place_id: String,
    val geometry: Geometry,
    val icon: String,
    val name: String,
    val vicinity: String,
    val user_ratings_total: Int,
    val rating: Float,
    val photos: List<Photo>
)