package com.interestsnearby.activities.mainInterestNearbyActivity

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.interestsnearby.R
import com.interestsnearby.dataModel.Place
import com.interestsnearby.fragments.interestsMap.IInterestsMapContract
import com.interestsnearby.managers.Constant
import com.interestsnearby.managers.InjectionCreator
import com.interestsnearby.managers.permissions.IPermissionManagerContract
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "MainInterestActivity"

class MainInterestNearbyActivity : AppCompatActivity(), IInterestsMapContract.IHost {
    private var mPermissionManager: IPermissionManagerContract.IPermissionManager? = null
    private var mInterestsMapFragment: IInterestsMapContract.IFragment? = null
    private lateinit var mViewModel: MainInterestNearbyActivityViewModel
    private var mSnackbar: Snackbar? = null
    private lateinit var geofencingClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, MainInterestNearbyActivity::class.java)
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPermissionManager = InjectionCreator.getPermissionManager(this)
        mViewModel = ViewModelProvider(this).get(MainInterestNearbyActivityViewModel::class.java)
        setGeofence()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(com.interestsnearby.broadcastRecivers.TAG, errorMessage)
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            mInterestsMapFragment?.onTriggerLocationOccurred(geofencingEvent.triggeringLocation)
        }
    }

    private fun setGeofence() {
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mInterestsMapFragment?.permissionGranted()
        } else {
            showSnackBar()
        }
    }

    private fun showSnackBar() {
        mSnackbar = Snackbar
            .make(
                clContainer,
                R.string.rationale_location_permission, Snackbar.LENGTH_INDEFINITE
            )
            .setAction(R.string.setting_button) {
                val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                settingIntent.data = uri
                startActivityForResult(
                    settingIntent,
                    Constant.SETTING_LOCATION_PERMISSIONS_REQUEST_CODE
                )
            }
        mSnackbar?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (requestCode == Constant.SETTING_LOCATION_PERMISSIONS_REQUEST_CODE) {
            mPermissionManager?.let {
                if (!shouldShowRequestPermissionRationale(permission) && !it.hasPermission(
                        permission
                    )
                ) {
                    showSnackBar()
                }
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            mInterestsMapFragment?.permissionGranted()
        }
    }

    override fun requestPermission(permission: String) {
        if (mPermissionManager == null) {
            mPermissionManager = InjectionCreator.getPermissionManager(this)
        }
        mPermissionManager?.requestPermission(permission)
    }

    override fun setFragmentCallBack(fragment: IInterestsMapContract.IFragment?) {
        mInterestsMapFragment = fragment
    }

    override fun hasPermission(permission: String): Boolean =
        mPermissionManager?.hasPermission(permission) ?: false

    override fun hideSnackbar() {
        mSnackbar?.dismiss()
    }

    override fun addGeofence(places: List<Place>) {
        geofencingClient.removeGeofences(geofencePendingIntent)
        geofencingClient.addGeofences(
            mViewModel.getGeofencingRequest(places),
            geofencePendingIntent
        )?.run {
            addOnSuccessListener {
                Log.d(TAG, "Geofences added")
            }
            addOnFailureListener {
                Log.d(TAG, "Failed to add geofences: ${it.message}")
            }
        }
    }

}
