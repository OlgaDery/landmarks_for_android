package com.google.albertasights.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat

import android.util.Log
import android.view.*
import com.google.albertasights.MapViewModel

import com.google.albertasights.R
import com.google.albertasights.models.Place
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MapFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {

    private var zoomIfRestarted = 0.0f
    private var longIfRestarted = 0.0
    private var latIfRestarted = 0.0
    private var created: Boolean? = null

    private var westCoord = 0.0
    private var eastCoord = 0.0
    private var southCoord = 0.0
    private var northCoord = 0.0
    private var mMap: GoogleMap? = null
    private var mCameraPosition: CameraPosition? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    private val mDefaultCoord = LatLng(51.0533674, -114.072997)
    private val defaultZoom = 9.0f
    private var currentZoom = 0.0f
    private var orientation: String? = null
    private var deviceType: String? = null
    private var count: Int = 0
    private var mClusterManager: ClusterManager<MyClusterItem>? = null
    private var adaptor: MyInfoWindowAdaptor? = null
    private var viewModel: MapViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        created = true
        if (savedInstanceState != null) {
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            currentZoom = mCameraPosition!!.zoom
        }
        try {
            mGoogleApiClient = GoogleApiClient.Builder(activity!!)
                    .enableAutoManage(activity!!, this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build()
            mGoogleApiClient!!.connect()
        } catch (e: IllegalStateException) {

        }

        viewModel = ViewModelProviders.of(activity!!).get(MapViewModel::class.java)

        // observer to change the permittions to access the geo data
        val locationPermissionsObserver = Observer<Boolean> { isPermittionsGranted ->
            if (isPermittionsGranted == true) {
                // check if GPS is enabled
                val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    UiUtils.displayLocationSettingsRequest(activity!!, viewModel!!)
                } else {
                    if (mMap != null) {
                        try {
                            mMap!!.isMyLocationEnabled = true
                            mMap!!.uiSettings.isMyLocationButtonEnabled = true

                        } catch (e: SecurityException) {
                            //TODO
                        }
                    }
                }

            } else {
                try {
                    mMap!!.isMyLocationEnabled = false
                    mMap!!.uiSettings.isMyLocationButtonEnabled = false
                } catch (e: SecurityException) {

                }
            }
        }
        viewModel!!.locationAccessPermitted.observe(this,
                locationPermissionsObserver)

        // observer to change the permittions to access the geo data
        val gpsAccessObserver = Observer<Boolean> { isGpsEnabled ->
            if (isGpsEnabled == false) {
                try {
                    if (mMap != null) {
                        mMap!!.isMyLocationEnabled = false
                        mMap!!.uiSettings.isMyLocationButtonEnabled = false
                    }

                } catch (e: SecurityException) {
                    //TODO
                    Log.e(TAG, getString(R.string.permissions_not_granted))
                }

            } else {
                try {
                    if (mMap != null) {
                        mMap!!.isMyLocationEnabled = true
                        mMap!!.uiSettings.isMyLocationButtonEnabled = true
                    }
                } catch (e: SecurityException) {
                    //TODO
                    Log.e(TAG, getString(R.string.permissions_not_granted))
                }
            }
        }
        viewModel!!.gpsEnabled.observe(this,
                gpsAccessObserver)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap!!.cameraPosition)
        } else {
            outState.putParcelable(KEY_CAMERA_POSITION, mCameraPosition)
        }
    }

    override fun onConnected(bundle: Bundle?) {
    }

    override fun onConnectionSuspended(i: Int) {
        UiUtils.showToast(activity!!, getString(R.string.google_maps_stopping))
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        UiUtils.showToast(activity!!, getString(R.string.error_connecting_google_maps))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)
        val mMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mMapFragment!!.getMapAsync(this)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        orientation = UiUtils.getOrientation(activity!!)
        deviceType = UiUtils.findScreenSize(activity!!)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val itemForSelected = menu.add(0, 0, 0, "Liked")
        val itemForAll = menu.add(0, 0, 0, "All")
        itemForSelected.icon = context!!.getDrawable(R.drawable.like)
        itemForSelected.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        itemForSelected.setOnMenuItemClickListener {
            viewModel!!.showLoved = true
            showClusters()
            true
        }
        itemForAll.icon = context!!.getDrawable(R.drawable.show_sorted)
        itemForAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        itemForAll.setOnMenuItemClickListener {
            if (viewModel!!.showLoved) {
                viewModel!!.showLoved = false
                showClusters()
            }
            true
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        if (ContextCompat.checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // check if GPS is enabled
            val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                UiUtils.displayLocationSettingsRequest(activity!!, viewModel!!)
            } else {
                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
            }
        } else {
            if (viewModel!!.receivedPoints.value != null) {
                //TODO check if activity gets recreated
                ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
        }

        if (viewModel!!.receivedPoints.value != null) {
            if (adaptor == null) {
                adaptor = MyInfoWindowAdaptor(viewModel!!)
                mMap!!.setInfoWindowAdapter(adaptor)
            }
        }

        if (created != null && created!!) {
            //that means that the activity is created the first time or recreated
            mClusterManager = ClusterManager(context!!, mMap)
            mClusterManager!!.renderer = MyClassRenderer(context!!, mMap!!, mClusterManager!!)
            mMap!!.setOnCameraIdleListener(mClusterManager)
            mMap!!.setOnInfoWindowClickListener(this)

            mMap!!.setOnMarkerClickListener { m ->

                //do nothing if a cluster is being clicked
                if (m.title == null) {
                    true
                } else {
                    m.showInfoWindow()
                    true
                }
            }
            val myLatLng: LatLng
            if (mCameraPosition != null) {
                myLatLng = LatLng(mCameraPosition!!.target.latitude, mCameraPosition!!.target.longitude)
            } else {
                myLatLng = mDefaultCoord
                currentZoom = defaultZoom
            }
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                    currentZoom))

            if (viewModel!!.receivedPoints.value != null) {
                showClusters()
            }

        } else {
            try {
                mCameraPosition = CameraPosition.Builder().target(LatLng(latIfRestarted, longIfRestarted))
                        .build()
                currentZoom = zoomIfRestarted
            } catch (e: Exception) {
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultCoord,
                        currentZoom))
            }
            if (viewModel!!.pointRemoved) {
                showClusters()
                viewModel!!.pointRemoved = false
            }
        }
    }

    private fun showClusters() {

        val pointsToShow = mutableListOf<Place>()
        mClusterManager!!.clearItems()
        mClusterManager!!.clusterMarkerCollection.clear()
        mClusterManager!!.markerCollection.clear()
        viewModel!!.markerIds.clear()
        mClusterManager!!.cluster()
        southCoord = 0.0
        northCoord = 0.0
        eastCoord = 0.0
        westCoord = 0.0
        count = 0
        if (viewModel!!.showLoved) {
            pointsToShow.addAll(viewModel!!.loved)
        } else {
            pointsToShow.addAll(viewModel!!.receivedPoints.value!!.second)
        }
        for (p in pointsToShow) {
            if (southCoord == 0.0) {
                southCoord = p.lat
            } else {
                if (p.lat > southCoord) {
                    southCoord = p.lat
                }
            }
            if (northCoord == 0.0) {
                northCoord = p.lat
            } else {
                if (p.lat < northCoord) {
                    northCoord = p.lat
                }
            }
            if (southCoord == 0.0) {
                southCoord = p.lat
            } else {
                if (p.lat > southCoord) {
                    southCoord = p.lat
                }
            }
            if (westCoord == 0.0) {
                westCoord = p.lng
            } else {
                if (p.lng < westCoord) {
                    westCoord = p.lng
                }
            }
            if (eastCoord == 0.0) {
                eastCoord = p.lng
            } else {
                if (p.lng > eastCoord) {
                    eastCoord = p.lng
                }
            }
            val offsetItem = MyClusterItem(p.lat, p.lng, p.name, p.category!!)
            mClusterManager!!.addItem(offsetItem)
            viewModel!!.markerIds.add(p.name)

        }
        stabilizeViewWithZoom()

    }

    private fun stabilizeViewWithZoom() {
        //changing the zoom level helps all the markers and clusters to set up
        if (created != null && !created!!) {
            if (westCoord != 0.0) {
                val currentBoundaries = LatLngBounds(LatLng(northCoord, westCoord), LatLng(southCoord, eastCoord))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(currentBoundaries, 50))
            }
        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap!!.cameraPosition.target, mMap!!.cameraPosition.zoom + 0.01f))
        created = false
    }

    override fun onInfoWindowClick(marker: Marker) {
        //method to call when user clicks on InfoWindow of the marker
        for (p in viewModel!!.receivedPoints.value!!.second) {
            if (p.name == marker.title) {
                viewModel!!.setPoint(p)
                break
            }
        }
    }

    inner class MyClassRenderer(context: Context, map: GoogleMap, clusterManager: ClusterManager<MyClusterItem>) : DefaultClusterRenderer<MyClusterItem>(context, map, clusterManager) {

        override fun onClusterItemRendered(item: MyClusterItem?, marker: Marker?) {
            super.onClusterItemRendered(item, marker)
            if (viewModel!!.saveInfoWindow) {

                if (viewModel!!.selectedMarkerID == marker!!.title) {
                    marker.showInfoWindow()
                    viewModel!!.selectedMarkerID = ""
                    viewModel!!.saveInfoWindow = false
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        created = false
        latIfRestarted = mMap!!.cameraPosition.target.latitude
        longIfRestarted = mMap!!.cameraPosition.target.longitude
        zoomIfRestarted = mMap!!.cameraPosition.zoom

        for (m in mClusterManager!!.markerCollection.markers) {
            if (m.isInfoWindowShown) {
                viewModel!!.selectedMarkerID = m.title
                viewModel!!.saveInfoWindow = true
                break
            }
        }
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.stopAutoManage(activity!!)
            mGoogleApiClient!!.disconnect()
        }
    }

    companion object {

        private val TAG = "MapFragment"
        private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        val KEY_CAMERA_POSITION = "camera_position"
    }
}
