package com.interestsnearby.managers

import android.app.Activity
import com.interestsnearby.fragments.interestsMap.IInterestsMapContract
import com.interestsnearby.fragments.interestsMap.InterestsMapFragmentModel
import com.interestsnearby.managers.locationManager.ILocationManager
import com.interestsnearby.managers.locationManager.LocationManagerObserver
import com.interestsnearby.managers.permissions.IPermissionManagerContract
import com.interestsnearby.managers.permissions.PermissionsManager

object InjectionCreator {

    fun getLocationManager(): ILocationManager.IManager = LocationManagerObserver.getInstance()
    fun getPermissionManager(activity: Activity): IPermissionManagerContract.IPermissionManager =
        PermissionsManager.getInstance(activity)

    fun getInterestsFragmentModel(): IInterestsMapContract.IModel = InterestsMapFragmentModel()

}