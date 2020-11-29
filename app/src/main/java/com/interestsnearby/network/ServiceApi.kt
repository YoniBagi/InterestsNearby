package com.interestsnearby.network

import com.interestsnearby.dataModel.InterestsPlaces
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceApi {
    @GET("nearbysearch/json")
    suspend fun fetchInterestsPlaces(
        @Query("location") latLong: String,
        @Query("radius") radius: Int,
        @Query("key") keyGooglePlaces: String
    ) : InterestsPlaces
}