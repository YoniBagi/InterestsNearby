package com.interestsnearby.managers.locationManager

import android.location.Location
import com.interestsnearby.dataModel.WayPoint

interface ILocationManager {
    interface IManager{
        fun startObserveLocation(observer: IObserver)
        fun calculateDistanceBetweenPointsInMeter(locationA: WayPoint, locationB: WayPoint): Double
        fun removeObserver()
    }
    interface IObserver{
        fun onLocationChange(location: Location)
    }
}