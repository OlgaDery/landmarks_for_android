package com.google.albertasights.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.albertasights.*

import com.google.albertasights.models.Place
import com.google.albertasights.utils.UiUtils
import com.google.albertasights.utils.parseUrl
import com.google.albertasights.utils.resetLayoutParameters
import com.google.albertasights.utils.showToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_point.*

import java.util.Locale

class PointFragment : Fragment() {

    companion object {
        private const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"
        private const val GOOGLE_MAPS_URL = "http://maps.google.com/maps?daddr=%f,%f (%s)"
    }

    private var point: Place? = null
    private var viewModel: MapViewModel? = null
    private var directionsRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MapViewModel::class.java)
        point = viewModel!!.pointToSee.value!!.second

        // observer to change the permittions to access the geo data
        val locationPermissionsObserver = Observer<Boolean> { isPermittionsGranted ->
            if (isPermittionsGranted == true) {

                // check if GPS is enabled
                val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    UiUtils.displayLocationSettingsRequest(activity!!, viewModel!!)
                } else {
                    if (directionsRequested) {
                        showNavigation()
                        directionsRequested = false
                    }
                }

            } else {
                context!!.showToast(getString(R.string.can_not_show_directions))
            }
        }
        viewModel!!.locationAccessPermitted.observe(this,
                locationPermissionsObserver)

        // observer to change the permittions to access the geo data
        val gpsAccessObserver = Observer<Boolean> { isGpsEnabled ->
            if (isGpsEnabled == false) {
                //
            } else {
                if (directionsRequested) {
                    directionsRequested = false
                    showNavigation()
                }
            }
        }
        viewModel!!.gpsEnabled.observe(this,
                gpsAccessObserver)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_point, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_all.setImageResource(R.drawable.directions)
        button_like.setImageResource(R.drawable.like)

        button_all.setOnClickListener {
            directionsRequested = true
            val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ContextCompat.checkSelfPermission(context!!,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // check if GPS enable
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    UiUtils.displayLocationSettingsRequest(activity!!, viewModel!!)
                } else {
                    showNavigation()
                }

            } else {
                ActivityCompat.requestPermissions(activity!!,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        1)
            }
        }

        if (viewModel!!.loved.contains(point)) {
            button_like.setColorFilter(ContextCompat.getColor(context!!, R.color.red), PorterDuff.Mode.SRC_IN)
        }

        button_like.setOnClickListener {
            // hook this method to the listener to make it visible for the activity
            if (!viewModel!!.loved.contains(point)) {
                if (viewModel!!.updateLoved(point!!, false)) {
                    button_like.setColorFilter(ContextCompat.getColor(context!!, R.color.red), PorterDuff.Mode.SRC_IN)
                    context!!.showToast(getString(R.string.added))
                } else {
                    context!!.showToast(getString(R.string.error))
                }

            } else {
                //call the method to remove from selected
                if (viewModel!!.updateLoved(point!!, true)) {
                    button_like.setColorFilter(null)
                    context!!.showToast(getString(R.string.removed))
                } else {
                    context!!.showToast(getString(R.string.error))
                }
            }
        }

        name.text = getString(R.string.name).plus(" ").plus(point!!.name)
        descript.text = getString(R.string.descr).plus(" ").plus(point!!.description ?: getString(R.string.no))
        link_text_view.text = getString(R.string.link).plus(" ").plus(point!!.weblink)

        val listener = View.OnClickListener {
            try {
                showWebPage()
            } catch (e: Exception) {
                context!!.showToast(getString(R.string.browser_not_found))
            }
        }
        if (point!!.weblink.length > 2) {
            link_text_view.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
            link_text_view.setOnClickListener(listener)
        }

        if (point!!.photolink.length > 5) {
            if (viewModel!!.orientation == UiUtils.LANDSCAPE) {
               //  portrait screen, set the width of the parental element
                img_picture.resetLayoutParameters(viewModel!!.hight / 2 + 60, viewModel!!.wight - 60)

            }
            Picasso.get()
                    .load(point!!.photolink.parseUrl())
                    .into(img_picture)

        } else {
            img_picture.resetLayoutParameters(viewModel!!.hight / 3 - 60, viewModel!!.wight / 2 - 60)
            Picasso.get()
                    .load(R.drawable.no_ph)
                    .into(img_picture)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(UiUtils.POINT, point)

    }

    private fun showNavigation () {
        val uri = String.format(Locale.ENGLISH, GOOGLE_MAPS_URL, point!!.lat, point!!.lng, "Going there")
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        mapIntent.setPackage(GOOGLE_MAPS_PACKAGE)
        startActivity(mapIntent)
    }

    private fun showWebPage() {
        val myWebLink = Intent(Intent.ACTION_VIEW)
        myWebLink.data = Uri.parse(point!!.weblink)
        context!!.startActivity(myWebLink)
    }
}