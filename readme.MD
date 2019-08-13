# Installation:
  - Clone to your machine
  - Open the project in Android Studio
  - Go to Manifest.xml, insert your own Google Maps key as a value. Othervise the map will not be rendered.
  - Run app on device or emulator.

# Permissions required: 
- You will be asked only for location permission. If not given, the app will still run, you will be unable to see you current location on the map and to calculate the route between your location and selected landmark.

# Features:
Call the API to get more then 400 landmarks of Calgary, Canada. You will see them clustered/unclustered on Google Map, for more details, click on the Marker -> expand InfoWindow. There is a screen with a data for each point, from this screen you can go directly to Google Maps App to calculate the route from the current location to this landmark. Also here you can add the landmark to you personal list (all the places from the lists can be viewed from the Map screen.) The app is saving the map state (zoom, target coordinates, opened Info Window) during configuration changes and when user is moving between screens.

# Technologies:
MVVM, Kotlin, Retrofit, Picasso, Dagger2, Google Maps (including clustering).

# Reconfiguring for your own url: 
- You can request the geodata from another server. The following requirements have to be met: 1) your are sending "GET" request 2) you are getting JSON array or object as a response 3) each geo object in the response has at least three mandatory fields: name, latitude and longitude. All the configurable parameters can be found in ConfigValues.kt class. You can declare: base URL, relative URL; also you can add all the request parameters (variable name "apiCallParams") and headers. If you are expecting to get a JSON object instead of JSON Array, it is mandatory to set the value of "isJsonObject" to "true" and to declare the name of the property containing the list with geodata (variable name "parameterNameOfJsonObject"). 
To parse the data from the response, you have to map the fields of the JSON object containing a piece of geodata to the fields of the App data class named "Place". The variable for the map is "fieldsToRename"; provide the names of the fields of the server object as values for this map.

# Usage:
MIT
