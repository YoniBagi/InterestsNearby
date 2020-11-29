package com.interestsnearby.broadcastRecivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

const val TAG = "GeofenceBR"

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private var mListener: GeofenceBroadcastCallback? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            mListener?.onTriggerLocationOccurred(geofencingEvent.triggeringLocation)
            Log.d("reciveTrigger", geofencingEvent.triggeringLocation.toString())
        }
    }

    fun setGeofenceBroadcastListener(listener: GeofenceBroadcastCallback){
        mListener = listener
    }

    interface GeofenceBroadcastCallback {
        fun onTriggerLocationOccurred(location: Location)
    }
}