package com.interestsnearby.fragments.interestsMap

import android.Manifest
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.interestsnearby.BuildConfig
import com.interestsnearby.activities.mainInterestNearbyActivity.MainInterestNearbyActivity
import com.interestsnearby.R
import com.interestsnearby.databinding.FragmentInterestsMapBinding
import com.microsoft.maps.*
import kotlinx.android.synthetic.main.fragment_interests_map.*
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.interestsnearby.activities.mainInterestNearbyActivity.MainInterestNearbyActivityViewModel
import com.interestsnearby.adapters.ListPlacesAdapter
import com.interestsnearby.dataModel.Place
import com.interestsnearby.dataModel.WayPoint

class InterestsMapFragment : Fragment(), IInterestsMapContract.IFragment,
    ListPlacesAdapter.ListPlacesCallBack {
    private var currentLocation: Location? = null
    private lateinit var mViewModel: InterestsMapFragmentViewModel
    private lateinit var mHost: IInterestsMapContract.IHost
    private var mMapView: MapView? = null
    private var mCurrentPositionPinLayer = MapElementLayer()
    private var mPlacesPinLayer = MapElementLayer()
    private var mIndicarionGeofence = MapElementLayer()
    private var locationBitmap: Bitmap? = null
    private var geofenceBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewModel()
        setHost()
        createBitmapForPlaces()
    }

    override fun onStart() {
        super.onStart()
        requestLocation()
    }

    private fun setHost() {
        mHost = activity as MainInterestNearbyActivity
        mHost.setFragmentCallBack(this)
    }

    private fun requestLocation() {
        if (mHost.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionGranted()
            return
        }
        mHost.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setViewModel() {
        mViewModel = ViewModelProvider(this).get(InterestsMapFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentInterestsMapBinding>(
            LayoutInflater.from(container?.context),
            R.layout.fragment_interests_map,
            container,
            false
        )
        setDataBinding(binding)
        setObservers()
        return binding.root
    }

    private fun setObservers() {
        mViewModel.getListPlacesMLD().observe(viewLifecycleOwner, Observer {
            setRecyclerViewPlaces(it)
            addPlacesToMap(it)
            mHost.addGeofence(it)
        })
    }

    override fun onResume() {
        super.onResume()
        mHost.setFragmentCallBack(this)
    }

    private fun setRecyclerViewPlaces(places: List<Place>) {
        rvListPlaces?.adapter = ListPlacesAdapter(places, this)
    }

    private fun addPlacesToMap(places: List<Place>) {
        mPlacesPinLayer.elements.clear()
        mMapView?.layers?.add(mPlacesPinLayer)
        for (place in places) {
            mPlacesPinLayer.elements.add(
                getMapIcon(
                    place.name,
                    Geopoint(place.geometry.location.latitude, place.geometry.location.longitude),
                    locationBitmap
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMap()
    }

    private fun setMap() {
        mMapView = MapView(this.context, MapRenderMode.VECTOR)
        mMapView?.setCredentialsKey(BuildConfig.CREDENTIALS_BING_MAP_KEY)
        mMapView?.language = Locale.ENGLISH.language
        bingMapView?.addView(mMapView)
    }

    private fun setDataBinding(binding: FragmentInterestsMapBinding) {
        binding.interestsMapFragmentViewModel = mViewModel
        binding.lifecycleOwner = this
    }

    override fun permissionGranted() {
        mHost.hideSnackbar()
        observeToLocation()
    }

    private fun observeToLocation() {
        mViewModel.observeToLocation()
        mViewModel.getLocationChangeMLD()
            .observe(this, Observer { zoomToLocation(it.latitude, it.longitude) })
        mViewModel.getCurrentLocationChangeMLD().observe(this, Observer {
            changeCurrentPin(it)
            currentLocation = it
        })
    }

    private fun changeCurrentPin(location: Location) {
        mCurrentPositionPinLayer.elements.clear()
        mMapView?.layers?.add(mCurrentPositionPinLayer)
        mCurrentPositionPinLayer.elements.add(
            getMapIcon(
                getString(R.string.my_location_title),
                Geopoint(location.latitude, location.longitude)
            )
        )
    }

    private fun getMapIcon(pinTitle: String, geopoint: Geopoint, icon: Bitmap? = null): MapIcon {
        val pushPin = MapIcon()
        return pushPin.apply {
            location = geopoint
            title = pinTitle
            icon?.let { image = MapImage(it) }
        }
    }

    private fun zoomToLocation(lat: Double, lng: Double, zoomLevel: Double = 14.0) {
        val currentLocation = Geopoint(lat, lng)
        mMapView?.setScene(
            MapScene.createFromLocationAndZoomLevel(currentLocation, zoomLevel),
            MapAnimationKind.LINEAR
        )
    }

    private fun createBitmapForPlaces() {
        locationBitmap = createBitmap(R.drawable.ic_location_on_36dp)
        geofenceBitmap = createBitmap(R.drawable.ic_check_circle_red)
    }

    private fun createBitmap(iconRes: Int): Bitmap? {
        return context?.let {
            AppCompatResources.getDrawable(it, iconRes)?.toBitmap()
        }
    }

    override fun onStop() {
        super.onStop()
        mViewModel.onStop()
        mHost.setFragmentCallBack(null)
    }

    override fun onClickItem(location: WayPoint) {
        zoomToLocation(location.latitude, location.longitude, 20.0)
    }

    override fun onTriggerLocationOccurred(location: Location) {
        //Add indication on map when user enter to place
        mIndicarionGeofence.elements.clear()
        mMapView?.layers?.add(mIndicarionGeofence)
        currentLocation?.let {
            mIndicarionGeofence.elements.add(
                getMapIcon("", Geopoint(it.latitude, it.longitude), geofenceBitmap)
            )
        }


    }
}