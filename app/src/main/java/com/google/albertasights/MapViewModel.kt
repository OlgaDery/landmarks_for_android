package com.google.albertasights


import android.app.Application
import androidx.lifecycle.*
import com.google.albertasights.di.AppComponent
import com.google.albertasights.di.AppModule
import com.google.albertasights.di.DaggerAppComponent
import com.google.albertasights.models.Place
import com.google.albertasights.services.Preferences
import com.google.albertasights.services.RetrofitCalls
import javax.inject.Inject

/**
 * Created by olga on 4/9/18.
 */

class MapViewModel(application: Application) : AndroidViewModel(application) {

    var component: AppComponent = DaggerAppComponent.builder()
            .appModule(AppModule(application.baseContext))
            .build()
    @Inject
    lateinit var preferences: Preferences

    init {
        component.inject(this)
    }

    private val retrofitService = RetrofitCalls()

    val loved = mutableListOf<Place>()
    val receivedPoints = MutableLiveData<Pair<Int, MutableList<Place>>>()
    val pointToSee = MutableLiveData<Pair<Int, Place>>()
    var observableID = 0
    var pointObservableID = 0

    var showLoved = false
    var pointRemoved = false

    var markerIds: MutableSet<String> = mutableSetOf()
    var saveInfoWindow = false
    var selectedMarkerID: String? = ""

    val deviceType = MutableLiveData<String>()
    val orientation = MutableLiveData<String>()
    val wight = MutableLiveData<Int>()
    val hight = MutableLiveData<Int>()
    val selected = mutableSetOf<String>()

    val dataReceived = MutableLiveData<Boolean>()
    val locationAccessPermitted = MutableLiveData<Boolean>()
    val gpsEnabled = MutableLiveData<Boolean>()

    val orienr: LiveData<String>
        get() = orientation

    fun generateID (place: Place): String {
        return place.id.plus("_").plus(place.name)
    }


    fun setLoved() {
        val ids: MutableSet<String>? = preferences.getSelectedPlaces("selected")
        if (!ids.isNullOrEmpty()) {
            selected.addAll(ids)
        }
        loved.addAll(receivedPoints.value!!.second.filter { selected.contains(generateID(it))
        }.toMutableList())
    }

    fun updateLoved(place: Place, remove: Boolean): Boolean {
        if (remove) {

            //TODO replace with lambda
            loved.remove(place)
            selected.remove(generateID(place))
            pointRemoved = true
        } else {
            loved.add(place)
            selected.add(generateID(place))
        }
        return preferences.setSelectedPoints("selected", selected)

    }

    fun setPoint(newPoint: Place) {
        val pair = Pair(pointObservableID+1, newPoint)
        pointToSee.value = pair
    }

    fun updateWight(wight1: Int?) {
        wight.value = wight1
    }

    fun updateHights(hight1: Int?) {
        hight.value = hight1
    }

    fun updateOrientation(orient: String) {
        orientation.value = orient
    }

    fun updateDeviceType(device: String) {
        deviceType.value = device
    }

    fun setLocationAccessPermitted(isPermitted: Boolean?) {
        locationAccessPermitted.value = isPermitted
    }

    fun requestPoints() {
        retrofitService.getAllTerritoryPoints("Calgary") {
            val pair = Pair(observableID+1, it.toMutableList())
            receivedPoints.value = pair
            setLoved()
        }
    }

    fun setGpsEnabled(isEnabled: Boolean?) {
        gpsEnabled.value = isEnabled
    }


}
