package com.interestsnearby.fragments.interestsMap

import android.location.Location
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interestsnearby.BuildConfig
import com.interestsnearby.dataModel.Place
import com.interestsnearby.dataModel.WayPoint
import com.interestsnearby.managers.Constant
import com.interestsnearby.managers.InjectionCreator
import com.interestsnearby.managers.locationManager.ILocationManager
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

const val DISTANCE_CONSIDERED_CHANGE_METER = 1000.0
const val DISTANCE_CONSIDERED_CURRENT_CHANGE_METER = 5.0
const val RADIUS_TO_COLLECT_PLACES_IN_METER = 1500
const val NUMBER_OF_ITEMS_TO_PRESENT = 5

class InterestsMapFragmentViewModel : ViewModel(), ILocationManager.IObserver,
    IInterestsMapContract.IViewModel {
    private var locationManager: ILocationManager.IManager? = null
    private var mLastLocationInterestPlace = WayPoint(0.0, 0.0)
    private var mLastCurrentLocation = WayPoint(0.0, 0.0)
    private val locationChangeMLD = MutableLiveData<Location>()
    private val currentLocationChangeMLD = MutableLiveData<Location>()
    private val fragmentModel = InjectionCreator.getInterestsFragmentModel()
    private val listPlacesMLD = MutableLiveData<List<Place>>()
    private val dataFetchingProgressMLD = MutableLiveData(true)
    private val errorMLD = MutableLiveData(false)

    fun getErrorMLD(): LiveData<Boolean> = errorMLD
    fun getListPlacesMLD(): LiveData<List<Place>> = listPlacesMLD
    fun getLocationChangeMLD(): LiveData<Location> = locationChangeMLD
    fun getDataFetchingProgressMLD(): LiveData<Boolean> = dataFetchingProgressMLD
    fun getCurrentLocationChangeMLD(): LiveData<Location> = currentLocationChangeMLD

    fun observeToLocation() {
        locationManager = InjectionCreator.getLocationManager()
        locationManager?.startObserveLocation(this)
    }

    override fun onLocationChange(location: Location) {
        val currentWayPoint = WayPoint(location.latitude, location.longitude)
        val interestPlaceDistance =
            locationManager?.calculateDistanceBetweenPointsInMeter(
                currentWayPoint,
                mLastLocationInterestPlace
            )
        val currentPositionDistance =
            locationManager?.calculateDistanceBetweenPointsInMeter(
                currentWayPoint,
                mLastCurrentLocation
            )
        if (interestPlaceDistance != null && interestPlaceDistance > DISTANCE_CONSIDERED_CHANGE_METER) {
            locationChangeMLD.postValue(location)
            mLastLocationInterestPlace = WayPoint(location.latitude, location.longitude)
            fetchInterestsPlaces("${currentWayPoint.latitude},${currentWayPoint.longitude}")
        }
        if (currentPositionDistance != null && currentPositionDistance > DISTANCE_CONSIDERED_CURRENT_CHANGE_METER) {
            currentLocationChangeMLD.postValue(location)
            mLastCurrentLocation = WayPoint(location.latitude, location.longitude)
        }

    }

    private fun fetchInterestsPlaces(latLong: String) {
        viewModelScope.launch {
            fragmentModel.fetchInterestsPlaces(
                latLong,
                RADIUS_TO_COLLECT_PLACES_IN_METER,
                BuildConfig.CREDENTIALS_GOOGLE_PLACES_KEY
            )
                .onStart { dataFetchingProgressMLD.postValue(true) }
                .onCompletion { dataFetchingProgressMLD.postValue(false) }
                .catch { errorMLD.postValue(true) }
                .collect { item ->
                    if (TextUtils.equals(item.status, Constant.STATUS_RESPONSE_OK)) {
                        listPlacesMLD.postValue(item.places.take(NUMBER_OF_ITEMS_TO_PRESENT))
                    } else {
                        errorMLD.postValue(true)
                    }
                }
        }
    }

    fun onStop() {
        locationManager?.removeObserver()
    }
}