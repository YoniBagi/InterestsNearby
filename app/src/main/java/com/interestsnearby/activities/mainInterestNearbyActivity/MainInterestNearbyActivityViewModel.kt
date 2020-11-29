package com.interestsnearby.activities.mainInterestNearbyActivity

import androidx.lifecycle.ViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.interestsnearby.dataModel.Place
import com.interestsnearby.managers.Constant

class MainInterestNearbyActivityViewModel: ViewModel() {
    private val geofenceList = mutableListOf<Geofence>()

    fun getGeofencingRequest(places: List<Place>): GeofencingRequest =
        GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(getGeofenceList(places))
        }.build()

    private fun getGeofenceList(places: List<Place>): MutableList<Geofence> {
        geofenceList.clear()
        //val myPlace = ArrayList(places)
        //myPlace.add(Place("890890", Geometry(WayPoint(31.800435, 34.824930)), "", "home", "home", 15, 2f, mutableListOf()))
        for (place in places){
            geofenceList.add(
                Geofence.Builder()
                    .setRequestId(place.place_id)
                    .setCircularRegion(
                        place.geometry.location.latitude,
                        place.geometry.location.longitude,
                        Constant.GEOFENCE_RADIUS_IN_METERS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build()
            )
        }
        return geofenceList
    }
}