package com.google.albertasights

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
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.albertasights.utils.*


class MapsActivity : AppCompatActivity() {

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

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
        viewModel!!.hight = this.getHightInches()
        viewModel!!.wight = this.getWidthInches()
        viewModel!!.deviceType = this.findScreenSize()
        viewModel!!.orientation = this.getOrientation()

        viewModel!!.receivedPoints.observe(this, Observer {
            if (viewModel!!.observableID != it.first && it.second != null) {
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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

            }
            viewModel!!.requestPoints()
        }
    }

    override fun finish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask()
        } else {
            super.finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel!!.setLocationAccessPermitted(true)

                } else {
                    this.showToast(getString(R.string.request_acceptance_recommended))
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
                    this.showToast(getString(R.string.gps_on))
                }
                Activity.RESULT_CANCELED -> {
                    viewModel!!.setGpsEnabled(false)
                }
            }
        }
    }

}