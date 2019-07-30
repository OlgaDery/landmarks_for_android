package com.google.albertasights.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.albertasights.MapViewModel

import com.google.albertasights.R
import com.google.albertasights.models.Place
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_point.*

import java.util.Locale

class PointFragment : Fragment() {

    private var point: Place? = null
    private val orientation: String? = null
    private val deviceType: String? = null
    private var viewModel: MapViewModel? = null
    var directionsRequested = false


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
                        val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                                point!!.lat, point!!.lng, point!!.name)
                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                        directionsRequested = false
                    }
                }

            } else {
                UiUtils.showToast(activity!!, "Sorry, you can not get directions without this permission.")
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
                    val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                            point!!.lat, point!!.lng, point!!.name)
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                    directionsRequested = false
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

        fab.setImageResource(R.drawable.directions)

        fab.setOnClickListener {
            directionsRequested = true
            val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ContextCompat.checkSelfPermission(context!!,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // check if GPS enable
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                    UiUtils.displayLocationSettingsRequest(activity!!, viewModel!!)
                } else {
                    val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                            point!!.lat, point!!.lng, "Going there")
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }

            } else {
                ActivityCompat.requestPermissions(activity!!,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        1)
            }
        }


        if (viewModel!!.loved.contains(point)) {
            like.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        }

        like.setOnClickListener { view ->
            // hook this method to the listener to make it visible for the activity
            val id = view.tag.toString()
            if (!viewModel!!.loved.contains(point)) {
                if (viewModel!!.updateLoved(point!!, false)) {
                    like.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                    UiUtils.showToast(context!!, "Added to loved")
                } else {
                    UiUtils.showToast(context!!, "Error occured")
                }

            } else {
                //call the method to remove from selected
                if (viewModel!!.updateLoved(point!!, true)) {
                    like.colorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
                    UiUtils.showToast(context!!, "Removed!")
                } else {
                    UiUtils.showToast(context!!, "Error occured!")
                }
            }
        }

        like.setImageResource(R.drawable.like)
        like.tag = point!!.name
        name.text = point!!.name
        descript.text = point!!.description ?: ""
        link.text = point!!.weblink

        val listener = View.OnClickListener {
            try {
                val myWebLink = Intent(Intent.ACTION_VIEW)
                myWebLink.data = Uri.parse(point!!.weblink)
                context!!.startActivity(myWebLink)
            } catch (e: Exception) {
                UiUtils.showToast(context!!, "Error, maybe no browsers have been installed")
            }
        }
        if (point!!.weblink.length > 2) {
            link.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent));
            link.setOnClickListener(listener);
        }

        when {
            point!!.rating in 4..5 -> ratingImg.setImageDrawable(context!!.resources.getDrawable(R.drawable.great))
            point!!.rating == 3 -> ratingImg.setImageDrawable(context!!.resources.getDrawable(R.drawable.good))
            else -> ratingImg.setImageDrawable(context!!.resources.getDrawable(R.drawable.not_bad))
        }

        if (point!!.photolink.length > 5) {
            if (orientation == UiUtils.PORTRAIT) {
                // portrait screen, set the width of the parental element

            } else {
                // landscape screen
            }
            Picasso.get()
                    .load(UiUtils.parseUrl(point!!.photolink))
//                    .resize(img_picture.layoutParams.width, img_picture.layoutParams.height)
 //                   .centerCrop()
                    .into(img_picture)

        } else {
          //  img_picture.layoutParams.height = screenH / 3 - 60
          //  img_picture.layoutParams.width = screenW / 2 - 60
            Picasso.get()
                    .load(R.drawable.no_ph)
                    .into(img_picture)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)
        outState.putSerializable(UiUtils.POINT, point)

    }

    companion object {
        private val TAG = PointFragment::class.java.simpleName
    }
}