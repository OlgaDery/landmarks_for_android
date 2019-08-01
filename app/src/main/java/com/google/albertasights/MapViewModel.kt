package com.google.albertasights


import android.annotation.TargetApi
import android.app.Application
import android.os.Build
import androidx.lifecycle.*
import com.google.albertasights.di.AppComponent
import com.google.albertasights.di.AppModule
import com.google.albertasights.di.DaggerAppComponent
import com.google.albertasights.models.Place
import com.google.albertasights.services.Preferences
import com.google.albertasights.services.RetrofitCalls
import com.google.albertasights.ui.UiUtils
import javax.inject.Inject


class MapViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val SELECTED = "selected"
    }

    private var component: AppComponent = DaggerAppComponent.builder()
            .appModule(AppModule(application.baseContext))
            .build()
    @Inject
    lateinit var preferences: Preferences

    init {
        component.inject(this)
    }

    private val retrofitService = RetrofitCalls()

    val loved = mutableListOf<Place>()
    val receivedPoints = MutableLiveData<Pair<Int, MutableList<Place>?>>()
    val pointToSee = MutableLiveData<Pair<Int, Place>>()
    var observableID = 0
    var pointObservableID = 0

    var showLoved = false
    var pointRemoved = false
    var markerIds: MutableSet<String> = mutableSetOf()
    var saveInfoWindow = false
    var selectedMarkerID: String? = ""
    var deviceType = UiUtils.PHONE
    var orientation = UiUtils.PORTRAIT
    var wight = 0
    var hight = 0
    val selected = mutableSetOf<String>()

    val locationAccessPermitted = MutableLiveData<Boolean>()
    val gpsEnabled = MutableLiveData<Boolean>()

    fun generateID (place: Place): String {
        return place.id.plus("_").plus(place.name)
    }

    fun setLoved() {
        val ids: MutableSet<String>? = preferences.getSelectedPlaces(SELECTED)
        if (!ids.isNullOrEmpty()) {
            selected.addAll(ids)
        }
        loved.addAll(receivedPoints.value!!.second!!.filter { selected.contains(generateID(it))
        }.toMutableList())
    }

    @TargetApi(24)
    fun updateLoved(place: Place, remove: Boolean): Boolean {
        if (remove) {
            if (Build.VERSION.SDK_INT >= 24) {
                loved.removeIf{ it == place }
                selected.removeIf{it == (generateID(place))}
            } else {
                val iteratorForPoints = loved.iterator()
                while (iteratorForPoints.hasNext()) {
                    if (place ==(iteratorForPoints.next())) {
                        iteratorForPoints.remove()
                    }
                }
                val iteratorForKeys = selected.iterator()
                while (iteratorForKeys.hasNext()) {
                    if (generateID(place) ==(iteratorForKeys.next())) {
                        iteratorForKeys.remove()
                    }
                }
            }

            pointRemoved = true
        } else {
            loved.add(place)
            selected.add(generateID(place))
        }
        return preferences.setSelectedPoints(SELECTED, selected)

    }

    fun setPoint(newPoint: Place) {
        val pair = Pair(pointObservableID+1, newPoint)
        pointToSee.value = pair
    }

    fun setLocationAccessPermitted(isPermitted: Boolean?) {
        locationAccessPermitted.value = isPermitted
    }

    fun requestPoints() {
        retrofitService.getAllTerritoryPoints("Calgary") {
            if (it != null) {
                val pair = Pair(observableID+1, it.toMutableList())
                receivedPoints.value = pair
                setLoved()
            } else {
                receivedPoints.value = Pair(observableID+1, null)

            }

        }
    }

    fun setGpsEnabled(isEnabled: Boolean?) {
        gpsEnabled.value = isEnabled
    }

}
