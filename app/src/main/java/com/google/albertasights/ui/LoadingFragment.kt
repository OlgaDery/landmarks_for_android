package com.google.albertasights.ui
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(MapViewModel::class.java)

        viewModel!!.receivedPoints.observe(this, Observer {
            if (it.second == null) {
                showButton()
                progress_bar.visibility = View.GONE
                retry.setOnClickListener {
                    viewModel!!.requestPoints()
                    progress_bar.visibility = View.VISIBLE
                    retry.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load("https://dl.dropboxusercontent.com/s/rjci9l5r6vajv49/20170806_173947.jpg").into(img_picture)
        retry.visibility = View.GONE
    }

    private fun showButton() {
        retry.visibility = View.VISIBLE
    }

}
