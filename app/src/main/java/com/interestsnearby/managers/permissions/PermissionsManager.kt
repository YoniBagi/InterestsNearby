package com.interestsnearby.managers.permissions

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.interestsnearby.InterestsNearbyApplication
import com.interestsnearby.managers.Constant


class PermissionsManager private constructor(): IPermissionManagerContract.IPermissionManager {
    companion object{
        private var instance: PermissionsManager? = null
        private lateinit var mActivity: Activity

        fun getInstance(activity: Activity) : PermissionsManager{
            mActivity = activity

            if (instance == null){
                instance = PermissionsManager()
            }
            return instance!!
        }
    }


    override fun hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
        InterestsNearbyApplication.getAppContext(),
        permission
    ) == PermissionChecker.PERMISSION_GRANTED

    override fun requestPermission(permission: String){
        if (!hasPermission(permission)){
            ActivityCompat.requestPermissions(mActivity, arrayOf(permission), Constant.PERMISSION_REQUEST_CODE)
        }
    }
}