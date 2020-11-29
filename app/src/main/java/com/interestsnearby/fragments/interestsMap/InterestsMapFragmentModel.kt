package com.interestsnearby.fragments.interestsMap

import com.interestsnearby.dataModel.InterestsPlaces
import com.interestsnearby.network.NetworkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InterestsMapFragmentModel : IInterestsMapContract.IModel {

    override fun fetchInterestsPlaces(
        latLong: String,
        radiusToCollectPlacesInMeter: Int,
        credentialsGooglePlacesKey: String
    ): Flow<InterestsPlaces> = flow {
        emit(
            NetworkManager.instanceServiceAdi.fetchInterestsPlaces(
                latLong,
                radiusToCollectPlacesInMeter,
                credentialsGooglePlacesKey
            )
        )
    }
}