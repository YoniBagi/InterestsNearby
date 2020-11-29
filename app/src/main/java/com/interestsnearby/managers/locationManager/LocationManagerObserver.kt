package com.interestsnearby.managers.locationManager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.interestsnearby.InterestsNearbyApplication
import com.interestsnearby.dataModel.WayPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

const val TAG = "LocationManagerObserver"

const val AVERAGE_RADIUS_OF_EARTH_KM = 6371.0
const val KM_IN_METER = 1000

class LocationManagerObserver private constructor() : LocationListener, ILocationManager.IManager {
    private var mObserver: ILocationManager.IObserver? = null
    private lateinit var locationManager: LocationManager

    companion object {
        private var instance: LocationManagerObserver? = null

        fun getInstance(): LocationManagerObserver {
            if (instance == null) {
                instance = LocationManagerObserver()
            }
            return instance!!
        }
    }

    override fun onLocationChanged(location: Location?) {
        location?.let { mObserver?.onLocationChange(it) }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d(TAG, "onStatusChanged")
    }

    override fun onProviderEnabled(provider: String?) {
        Log.d(TAG, "onProviderEnabled  provider: $provider")
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d(TAG, "onProviderDisabled  provider: $provider")
    }

    override fun startObserveLocation(observer: ILocationManager.IObserver) {
        mObserver = observer
        val appCtx = InterestsNearbyApplication.getAppContext()
        locationManager = appCtx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                appCtx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
    }

    override fun calculateDistanceBetweenPointsInMeter(
        locationA: WayPoint,
        locationB: WayPoint
    ): Double {
        val latDistance = Math.toRadians(locationA.latitude - locationB.latitude)
        val lngDistance = Math.toRadians(locationA.longitude - locationB.longitude)

        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(locationA.latitude)) * cos(Math.toRadians(locationB.latitude))
                * sin(lngDistance / 2) * sin(lngDistance / 2)))

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return AVERAGE_RADIUS_OF_EARTH_KM * c * KM_IN_METER
    }

    override fun removeObserver() {
        locationManager.removeUpdates(this)
    }
}