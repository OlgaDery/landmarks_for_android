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

# Usage:
MIT
