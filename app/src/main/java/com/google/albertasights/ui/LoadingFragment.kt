package com.google.albertasights.ui
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.albertasights.MapViewModel
import com.google.albertasights.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_loading.*

class LoadingFragment : Fragment() {

    private var viewModel: MapViewModel? = null
    private var screenH: Int? = null
    private var screenW: Int? = null
    private var orientation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        val loadingDataObserver = Observer<Boolean> { data ->
            if (data == false) {
                showButton()
                retry.setOnClickListener {
                   // onButtonPressed("RETRY")
                    viewModel!!.requestPoints()

                }
            }
        }
        viewModel!!.dataReceived.observe(activity!!, loadingDataObserver)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        screenW = viewModel!!.wight.value
//        screenH = viewModel!!.hight.value
//        orientation = viewModel!!.orienr.value
//
//        val params = img.layoutParams
//        params.height = screenH!! / 100 * 80
//        params.width = screenW!! / 100 * 80
        Picasso.get()
                .load("https://dl.dropboxusercontent.com/s/rjci9l5r6vajv49/20170806_173947.jpg")//R.raw.albertasights_horizontal
                //.resize(screenW!! / 100 * 80, screenH!! / 100 * 80)
                //    .onlyScaleDown()
                // .centerInside()
                .into(img_picture)

        retry.alpha = 0.0f
    }

    private fun showButton() {
        retry.alpha = 1.0f
    }

}
