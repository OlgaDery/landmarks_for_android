package com.google.albertasights
import android.content.Context
import android.os.Build
import androidx.lifecycle.*
import com.google.albertasights.models.Place
import com.google.albertasights.services.DatabaseActions
import com.google.albertasights.services.PreferenceActions
import com.google.albertasights.services.GetGeoData
import com.google.albertasights.utils.UiUtils
import com.google.albertasights.utils.checkNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class MapViewModel : ViewModel() {

    companion object {
        private const val SELECTED = "selected"
        private const val NO_CONNECTION_MESSAGE = 1
        private const val SERVER_ERROR_MESSAGE = 2
    }

    @Inject
    lateinit var preferences: PreferenceActions

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var database: DatabaseActions

    @Inject
    lateinit var retrofitService: GetGeoData

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
    val error = MutableLiveData<Int>()

    private fun setLoved(places: List<Place>) {
        val ids: MutableSet<String>? = preferences.getCollection(generateSelectedPointsCollectionID(ConfigValues.BASE_URL))
        if (!ids.isNullOrEmpty()) {
            selected.addAll(ids)
        }
        loved.addAll(places.filter { selected.contains(generatePlaceID(it))
        }.toMutableList())
    }

    fun updateLoved(place: Place, remove: Boolean): Boolean {
        pointRemoved = updateLovedList(place, remove, loved, selected)
        return preferences.saveCollection(generateSelectedPointsCollectionID(ConfigValues.BASE_URL), selected)
    }

    fun setPoint(newPoint: Place) {
        val pair = Pair(pointObservableID+1, newPoint)
        pointToSee.value = pair
    }

    fun setLocationAccessPermitted(isPermitted: Boolean?) {
        locationAccessPermitted.value = isPermitted
    }

    fun getPointsFromDB() {
        viewModelScope.launch(Dispatchers.IO){
            val list: List<Place>? = database.getDataFromDatabase(ConfigValues.BASE_URL, Place::class.java)
            if (list != null) {
                val pair = Pair(observableID + 1, list.toMutableList())
                receivedPoints.postValue(pair)
                setLoved(list)
            } else {
                if (context.checkNetwork() == 0) {
                    error.postValue(NO_CONNECTION_MESSAGE)
                }
            }
        }
        if (context.checkNetwork() != 0) {
            requestPoints()
        } else {
            error.postValue(NO_CONNECTION_MESSAGE)
        }
    }

     fun requestPoints() {
         retrofitService.getAllTerritoryPoints {
             if (it != null) {
                 val pair = Pair(observableID + 1, it.toMutableList())
                 receivedPoints.value = pair
                 updateDatabase(pair.second)

             } else {
                 error.postValue(SERVER_ERROR_MESSAGE)
             }
         }
     }

    fun updateDatabase(mutableList: MutableList<Place>) {
        viewModelScope.launch(Dispatchers.IO) {
            database.submitDataToDatabase(Place::class.java, mutableList, List::class.java, ConfigValues.BASE_URL)
        }
    }

    fun setGpsEnabled(isEnabled: Boolean?) {
        gpsEnabled.value = isEnabled
    }

    //testable methods
    fun generatePlaceID (place: Place): String {
        return place.id.plus("_").plus(place.name)
    }


    fun generateSelectedPointsCollectionID (url: String): String {
        return url.plus("_").plus(SELECTED)
    }

    fun updateLovedList(place: Place, remove: Boolean, lovedList: MutableList<Place>, selectedIds: MutableSet<String>): Boolean {
        if (remove) {
            if (Build.VERSION.SDK_INT >= 24) {
                lovedList.removeIf{ it == place }
                selectedIds.removeIf{it == (generatePlaceID(place))}
            } else {
                val iteratorForPoints = lovedList.iterator()
                while (iteratorForPoints.hasNext()) {
                    if (place ==(iteratorForPoints.next())) {
                        iteratorForPoints.remove()
                    }
                }
                val iteratorForKeys = selectedIds.iterator()
                while (iteratorForKeys.hasNext()) {
                    if (generatePlaceID(place) ==(iteratorForKeys.next())) {
                        iteratorForKeys.remove()
                    }
                }
            }
            return true
        } else {
            lovedList.add(place)
            selectedIds.add(generatePlaceID(place))
            return false
        }
    }

}
