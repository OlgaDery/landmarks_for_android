package com.google.albertasights.ui
import com.google.albertasights.R
import com.google.android.gms.maps.GoogleMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.albertasights.MapViewModel

import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class MyInfoWindowAdaptor(private val viewModel: MapViewModel) : GoogleMap.InfoWindowAdapter {
    private var name: TextView? = null
    private var descr: TextView? = null
    private var photo: ImageView? = null
    private var toLoad: String? = null
    internal var count = 0

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View? {
        val view: View
        try {
            if (viewModel.orientation.value == UiUtils.PORTRAIT) {
                view = LayoutInflater.from(viewModel.getApplication()).inflate(R.layout.custom_info_contents, null)

            } else {
                view = LayoutInflater.from(viewModel.getApplication()).inflate(R.layout.custom_info_contents1, null)
            }
            photo = view.findViewById(R.id.img_picture)
            descr = view.findViewById(R.id.descript)
            name = view.findViewById(R.id.name)

            val button = view.findViewById<ImageButton>(R.id.more)
            button.setImageResource(R.drawable.more_horizontal)
            if (viewModel.orientation.value == UiUtils.PORTRAIT) {

                if (viewModel.deviceType.value == UiUtils.TABLET) {
                    // big vertical
                    val params = LinearLayout.LayoutParams(viewModel.hight.value!!/2, ViewGroup.LayoutParams.WRAP_CONTENT)
                    view.layoutParams = params
                    val photoParams = RelativeLayout.LayoutParams(viewModel.wight.value!!/2 - 20, viewModel.hight.value!!/3 - 50)
                    photo!!.layoutParams = photoParams
                } else {
                    // small vertical
                    val params = LinearLayout.LayoutParams(viewModel.wight.value!!/100 * 70, ViewGroup.LayoutParams.WRAP_CONTENT)
                    view.layoutParams = params
                    val photoParams = RelativeLayout.LayoutParams(viewModel.wight.value!!/ 100 * 70 - 20, viewModel.hight.value!!/3)
                    photo!!.layoutParams = photoParams

                }
            } else {
                if (viewModel.deviceType.value == UiUtils.TABLET) {
                    // big horizontal
                    val params = LinearLayout.LayoutParams(viewModel.wight.value!!/2, ViewGroup.LayoutParams.WRAP_CONTENT)
                    view.layoutParams = params
                    val photoParams = RelativeLayout.LayoutParams(viewModel.wight.value!!/4, viewModel.wight.value!!/4)
                    photo!!.layoutParams = photoParams

                } else {
                    // small horizontal
                    val params = LinearLayout.LayoutParams(viewModel.wight.value!!/100 * 60, ViewGroup.LayoutParams.WRAP_CONTENT)//screenHight/100*70
                    view.layoutParams = params
                    val photoParams = RelativeLayout.LayoutParams(viewModel.wight.value!!/4, viewModel.hight.value!!/2 - 20)
                    photo!!.layoutParams = photoParams
                }
            }

            // setting up the content
            for (p in viewModel.receivedPoints.value!!.second) {
                if (p.name == marker.title) {
                    name!!.text = p.name
                    if (p.description != null && p.description!!.length > 100) {
                        descr!!.text = p.description!!.substring(0, 100)
                    } else {
                        descr!!.text = p.description
                    }
                    if (!viewModel.markerIds.contains(marker.title)) {
                        if (p.photolink.length > 5) {
                            Picasso.get()
                                    .load(UiUtils.parseUrl(p.photolink))
                                    .resize(photo!!.layoutParams.width, photo!!.layoutParams.height)
                                    .centerCrop()
                                    .into(photo)

                        } else {
                            Picasso.get()
                                    .load(R.drawable.no_ph)
                                    .into(photo)
                        }

                        toLoad = null
                        count = 0
                    } else {
                        count++
                        if (toLoad == null) {
                            if (UiUtils.parseUrl(p.photolink) != "no") {
                                toLoad = "url"

                            } else {
                                toLoad = "photo"
                            }
                        }
                        loadPicasso(UiUtils.parseUrl(p.photolink), marker, photo!!)
                        break
                    }
                }
            }
            return view
        } catch (e: Exception) {
            e.printStackTrace()
            toLoad = null
            return null
        }
    }

    private fun loadPicasso(url: String, marker: Marker, photo: ImageView) {
        if (this.toLoad == "url") {
            Picasso.get()
                    .load(url)
                    .resize(photo.layoutParams.width, photo.layoutParams.height)
                    .centerCrop()
                    .into(photo, InfoWindowRefresher(marker))

        } else {
            Picasso.get()
                    .load(R.drawable.no_ph)
                    .into(photo, InfoWindowRefresher(marker))

        }
    }

    private inner class InfoWindowRefresher constructor(private val markerToRefresh: Marker) : Callback {
        override fun onSuccess() {
            viewModel.markerIds.remove(markerToRefresh.title)
            if (count > 2) {
                count = 0
                return
            }
            markerToRefresh.showInfoWindow()
        }

        override fun onError(e: Exception) {
            toLoad = "photo"
            if (count > 2) {
                count = 0
                return
            }
            markerToRefresh.showInfoWindow()
        }
    }

}
