package com.interestsnearby.fragments.interestsMap

import android.location.Location
import com.interestsnearby.dataModel.InterestsPlaces
import com.interestsnearby.dataModel.Place
import kotlinx.coroutines.flow.Flow

interface IInterestsMapContract {
    interface IHost{
        fun requestPermission(permission: String)
        fun setFragmentCallBack(fragment: IFragment?)
        fun hasPermission(permission: String): Boolean
        fun hideSnackbar()
        fun addGeofence(places: List<Place>)
    }
    interface IFragment{
        fun permissionGranted()
        fun onTriggerLocationOccurred(location: Location)
    }
    interface IModel{
        fun fetchInterestsPlaces(
            latLong: String,
            radiusToCollectPlacesInMeter: Int,
            credentialsGooglePlacesKey: String
        ): Flow<InterestsPlaces>
    }
    interface IViewModel
}