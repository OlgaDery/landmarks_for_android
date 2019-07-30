package com.google.albertasights.ui

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.google.albertasights.MapViewModel
import com.google.albertasights.R

class MapsActivity : AppCompatActivity() {

    private var viewModel: MapViewModel? = null
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_maps)
            navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
        } catch (e: Exception) {
            System.out.println(e.printStackTrace())
        }

        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        viewModel!!.updateHights(UiUtils.getHightInches(applicationContext))
        viewModel!!.updateWight(UiUtils.getWidthInches(applicationContext))
        viewModel!!.updateDeviceType(UiUtils.findScreenSize(applicationContext))
        viewModel!!.updateOrientation(UiUtils.getOrientation(applicationContext))

        viewModel!!.receivedPoints.observe(this, Observer {
            if (viewModel!!.observableID != it.first) {

                //Going to the map fragment. Preventing Loader fragment from being included to backstack
                navController.navigate(R.id.loader_fr_to_map_fr, null, NavOptions.Builder()
                        .setPopUpTo(R.id.loader_fr, true).build())
                viewModel!!.observableID = viewModel!!.observableID+1
            }
        })

        viewModel!!.pointToSee.observe(this, Observer {
            if (viewModel!!.pointObservableID != it.first) {
                navController.navigate(R.id.action_map_fr_to_point)
                viewModel!!.pointObservableID = viewModel!!.pointObservableID+1
            }
        })

        if (viewModel!!.receivedPoints.value == null) {
            System.out.println("should make api call!!!!!!!!!!!!!!!!")
            viewModel!!.requestPoints()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        android.os.Process.killProcess(android.os.Process.myPid())
        //System.out.println("$$$$$$$$$$$$$$$$$$$$$44")
    }
    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel!!.setLocationAccessPermitted(true)

                } else {
                    UiUtils.showToast(this, getString(R.string.request_acceptance_recommended))
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UiUtils.REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    // All required changes were successfully made
                    viewModel!!.setGpsEnabled(true)
                    UiUtils.showToast(this, getString(R.string.gps_on))
                }
                Activity.RESULT_CANCELED -> {
                    viewModel!!.setGpsEnabled(false)
                }
            }
        }
    }

}