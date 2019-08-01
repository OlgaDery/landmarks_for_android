package com.google.albertasights.ui
import android.annotation.SuppressLint
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

    companion object {
        private const val URL = "url"
        private const val PHOTO = "photo"
        private const val NO_PHOTO = "no"
    }

    private var toLoad: String? = null
    private var count = 0

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker): View? {
        val view: View
        val name: TextView?
        val descr: TextView?
        val photo: ImageView?
        try {
            view = if (viewModel.orientation == UiUtils.PORTRAIT) {
                LayoutInflater.from(viewModel.getApplication()).inflate(R.layout.info_window_layout, null)

            } else {
                LayoutInflater.from(viewModel.getApplication()).inflate(R.layout.info_window_layout1, null)
            }
            photo = view.findViewById(R.id.img_picture)
            descr = view.findViewById(R.id.descript)
            name = view.findViewById(R.id.name)

            val button = view.findViewById<ImageButton>(R.id.more)
            button.setImageResource(R.drawable.more_horizontal)
            if (viewModel.orientation == UiUtils.PORTRAIT) {

                if (viewModel.deviceType == UiUtils.TABLET) {
                    // big vertical
                    val params = LinearLayout.LayoutParams(viewModel.hight/2, ViewGroup.LayoutParams.WRAP_CONTENT)
                    view.layoutParams = params
                    val photoParams = RelativeLayout.LayoutParams(viewModel.wight/2 - 20, viewModel.hight/3 - 50)
                    photo!!.layoutParams = photoParams
                } else {
                    // small vertical
                    val params = LinearLayout.LayoutParams(viewModel.wight/100 * 70, ViewGroup.LayoutParams.WRAP_CONTENT)
                    view.layoutParams = params
                    val photoParams = RelativeLayout.LayoutParams(viewModel.wight/ 100 * 70 - 20, viewModel.hight/3)
                    photo!!.layoutParams = photoParams

                }
            } else {
                if (viewModel.deviceType == UiUtils.TABLET) {
                    // big horizontal
                    val params = LinearLayout.LayoutParams(viewModel.wight/2, ViewGroup.LayoutParams.WRAP_CONTENT)
                    view.layoutParams = params
                    val photoParams = RelativeLayout.LayoutParams(viewModel.wight/4, viewModel.wight/4)
                    photo!!.layoutParams = photoParams

                } else {
                    // small horizontal
                    val params = LinearLayout.LayoutParams(viewModel.wight/100 * 60, ViewGroup.LayoutParams.WRAP_CONTENT)//screenHight/100*70
                    view.layoutParams = params
                    val photoParams = RelativeLayout.LayoutParams(viewModel.wight/4, viewModel.hight/2 - 20)
                    photo!!.layoutParams = photoParams
                }
            }

            // setting up the content
            for (p in viewModel.receivedPoints.value!!.second!!) {
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
                                    .resize(photo.layoutParams.width, photo.layoutParams.height)
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
                            if (UiUtils.parseUrl(p.photolink) != NO_PHOTO) {
                                toLoad = URL

                            } else {
                                toLoad = PHOTO
                            }
                        }
                        loadPicasso(UiUtils.parseUrl(p.photolink), marker, photo)
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
        if (this.toLoad == URL) {
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
        //this class is to call recursively showInfoWindow() if the data is not being loaded the first time.
        override fun onSuccess() {
            viewModel.markerIds.remove(markerToRefresh.title)
            if (count > 2) {
                count = 0
                return
            }
            markerToRefresh.showInfoWindow()
        }

        override fun onError(e: Exception) {
            toLoad = PHOTO
            if (count > 2) {
                count = 0
                return
            }
            markerToRefresh.showInfoWindow()
        }
    }

}
