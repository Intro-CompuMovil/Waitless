package com.example.waitless_p1

import android.Manifest
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.waitless_p1.Datos.Companion.MY_PERMISSION_REQUEST_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.util.Date
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.LocationManager
import android.view.KeyEvent
import com.wdullaer.materialdatetimepicker.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.roundToInt


class RutaActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private  var latitud : Double = 0.0
    private  var longitud : Double = 0.0
    private  var altitud : Double = 0.0
    private var lastLocation: GeoPoint = GeoPoint(latitud, longitud)
    private lateinit var lastLocation1 : MyLocationNewOverlay
    var check : Boolean = false
    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var sensorManager : SensorManager
    private var lightSensor : Sensor? = null
    private var lightSensorListener : SensorEventListener? = null
    private var lastMarker: GeoPoint = GeoPoint(latitud, longitud)
    lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta)

        var search = findViewById<EditText>(R.id.editText)

        roadManager = OSRMRoadManager(this, "ANDROID")
        SearchService(search)
        Configuration.getInstance().setUserAgentValue(org.osmdroid.library.BuildConfig.BUILD_TYPE)
        map = findViewById(R.id.osmMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        setUpSensorStuff()
        map.overlays.add(createOverlayEvents())

    }

    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        GlobalScope.launch(Dispatchers.IO) {
            val road = roadManager.getRoad(routePoints)
            withContext(Dispatchers.Main) {
                Log.i("OSM_acticity", "Route length: ${road.mLength} klm")
                Log.i("OSM_acticity", "Duration: ${road.mDuration / 60} min")
                if (map != null) {
                    roadOverlay?.let { map.overlays.remove(it) }
                    roadOverlay = RoadManager.buildRoadOverlay(road)
                    roadOverlay?.outlinePaint?.color = Color.RED
                    roadOverlay?.outlinePaint?.strokeWidth = 10f
                    map.overlays.add(roadOverlay)
                    val distance = start.distanceToAsDouble(finish)
                    Toast.makeText(this@RutaActivity, "La distancia entre los puntos son : $distance metros", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun createOverlayEvents(): MapEventsOverlay {
        val overlayEventos = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return false
            }
            override fun longPressHelper(p: GeoPoint): Boolean {
                longPressOnMap(p)
                return true
            }
        })
        return overlayEventos
    }
    private fun longPressOnMap(p: GeoPoint) {
        val coder = Geocoder(this)
        val addresses: List<Address>?
        addresses = coder.getFromLocation(p.latitude, p.longitude, 1)
        if (addresses!!.isNotEmpty()) {
            val address = addresses[0]
            val mapController = map.controller
            mapController.setCenter(p)
            mapController.setZoom(15.0)
            val marker = Marker(map)
            marker.position = p
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = address.getAddressLine(0)
            map.overlays.add(marker)
            lastMarker = p
            if (lastLocation != null) {
                drawRoute(lastLocation, lastMarker)
            }
        }
    }
    private fun SearchService(search: EditText){
        search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                event?.action == KeyEvent.ACTION_DOWN &&
                event.keyCode == KeyEvent.KEYCODE_ENTER) {
                // Manejar el evento Enter aquí
                // Por ejemplo, puedes cerrar el teclado:
                val text = search.text.toString()
                if(text.isEmpty()){
                }
                val location = getLocationFromText(text)
                if (location != null) {
                    val mapController = map.controller
                    mapController.setCenter(location)
                    mapController.setZoom(15.0)
                    Toast.makeText(applicationContext, "Ubicación encontrada $location", Toast.LENGTH_LONG).show()
                    val marker = Marker(map)
                    marker.position = location
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    map.overlays.add(marker)
                    val location1 = GeoPoint(location.latitude, location.longitude)
                    lastMarker = location1
                    if (lastLocation != null) {
                        drawRoute(lastLocation, lastMarker)
                    }
                }
                // Aquí puedes agregar lo que deseas hacer cuando se presione Enter, como realizar una búsqueda
                true // Devolver 'true' para indicar que has manejado el evento
            } else {
                false // Devolver 'false' para dejar que el sistema maneje el evento
            }
        }
    }
    private fun getLocationFromText(text: String): GeoPoint? {
        val coder = Geocoder(this)
        val addressList: List<Address>?
        try {
            addressList = coder.getFromLocationName(text, 1)
            if (addressList.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "No se encontraron resultados", Toast.LENGTH_LONG).show()
                return null
            }
            val location = addressList[0]
            return GeoPoint(location.latitude, location.longitude)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    private fun setupLocationOverlay(mapController: IMapController) {
        val locationProvider = GpsMyLocationProvider(this)
        locationOverlay = MyLocationNewOverlay(locationProvider, map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        locationOverlay.runOnFirstFix { runOnUiThread {

            if (locationOverlay.myLocation != null) {
                if (!check) {
                    check = true
                    val currentLocation = locationOverlay.myLocation
                    val latitude = currentLocation.latitude
                    val longitude = currentLocation.longitude
                    lastLocation = GeoPoint(latitude, longitude)
                    lastLocation1 = locationOverlay
                    map.overlays.add(locationOverlay)
                    mapController.setZoom(15.0)
                    mapController.setCenter(locationOverlay.myLocation)
                    // Now you can use latitude and longitude
                }
                else{
                    val currentLocation = locationOverlay.myLocation
                    val latitude = currentLocation.latitude
                    val longitude = currentLocation.longitude
                    if (calculateDistance(lastLocation.latitude, lastLocation.longitude, latitude, longitude) > 30) {
                        lastLocation = GeoPoint(latitude, longitude)
                        lastLocation1 = locationOverlay
                        map.overlays.add(locationOverlay)
                        mapController.setCenter(locationOverlay.myLocation)
                        val currentDate = Date()
                        val myLocation = MyLocation(currentDate, latitude, longitude)
                        writeJSONObject(myLocation)
                    }
                    else{
                        Toast.makeText(applicationContext, "Movimiento menor a  30m ", Toast.LENGTH_LONG).show()
                        map.overlays.add(lastLocation1)
                    }
                }
            }
        }}
    }
    private fun calculateDistance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val RADIUS_OF_EARTH_KM = 6371.0
        val latDistance = Math.toRadians(lat1 - lat2)
        val lngDistance = Math.toRadians(long1 - long2)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val result = RADIUS_OF_EARTH_KM * c *1000
        return (result * 100.0).roundToInt() / 100.0
    }
    class MyLocation(var fecha: Date, var latitud: Double, var longitud: Double) {
        fun toJSON(): JSONObject {
            val obj = JSONObject()
            try {
                obj.put("latitud", latitud)
                obj.put("longitud", longitud)
                obj.put("date", fecha)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return obj
        }
    }

    private fun readJSONArrayFromFile(fileName: String): JSONArray {
        val file = File(baseContext.getExternalFilesDir(null), fileName)
        if (!file.exists()) {
            return JSONArray()
        }
        val jsonString = file.readText()
        return JSONArray(jsonString)
    }

    private fun writeJSONObject(location: MyLocation) {
        val localizaciones = readJSONArrayFromFile("locations.json")
        localizaciones.put(location.toJSON())
        var output: Writer?
        val filename = "locations.json"
        try {
            val file = File(baseContext.getExternalFilesDir(null), filename)
            output = BufferedWriter(FileWriter(file))
            output.write(localizaciones.toString())
            output.close()
            Toast.makeText(applicationContext, "Location saved", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }
    private fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            if (event.values[0] < 10.0) {
                map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
            } else {
                map.overlayManager.tilesOverlay.setColorFilter(null)
            }
        }
    }
}