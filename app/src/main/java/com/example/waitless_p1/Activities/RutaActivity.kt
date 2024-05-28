package com.example.waitless_p1.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.example.waitless_p1.Data.Datos.Companion.MY_PERMISSION_REQUEST_LOCATION
import com.example.waitless_p1.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

    private lateinit var map: MapView
    private var mGeocoder: Geocoder? = null
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null
    //Location permissions
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var myLocation: Location? = null

    // Coordenadas de origen
    private val originLat = 4.62894444
    private val originLon = -74.06485

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta)

        // Inicializar el cliente de ubicación fusionada
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Configurar mapa
        Configuration.getInstance().setUserAgentValue(org.osmdroid.library.BuildConfig.BUILD_TYPE)
        map = findViewById(R.id.osmMap)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        // Revisar permisos
        checkLocationPermission()

        roadManager = OSRMRoadManager(this, "ANDROID")

        setUpSensorStuff()

        // Obtener el parque desde el intent
        val intentRecibir = intent
        val buscarParque = intentRecibir.getStringExtra("parque")
        if (buscarParque != null) {
            drawPointsAndRouteToPark(buscarParque)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tienes permisos, solicitarlos al usuario
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            //El usuario ya ha rechazado los permisos
            Toast.makeText(this, "Permisos denegados :(", Toast.LENGTH_SHORT).show()
        }else{
            //Pedir permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQUEST_LOCATION)
        }
    }

    private fun drawRoute(start: GeoPoint, finish: GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        GlobalScope.launch(Dispatchers.IO) {
            val road = roadManager.getRoad(routePoints)
            withContext(Dispatchers.Main) {
                Log.i("OSM_acticity", "Route length: ${road.mLength} km")
                Log.i("OSM_acticity", "Duration: ${road.mDuration / 60} min")
                if (map != null) {
                    roadOverlay?.let { map.overlays.remove(it) }
                    roadOverlay = RoadManager.buildRoadOverlay(road)
                    roadOverlay?.outlinePaint?.color = Color.RED
                    roadOverlay?.outlinePaint?.strokeWidth = 10f
                    map.overlays.add(roadOverlay)
                    val distance = String.format("%.2f", start.distanceToAsDouble(finish) / 1000)
                    Toast.makeText(
                        this@RutaActivity,
                        "La distancia hasta el parque es: $distance km",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun drawPointsAndRouteToPark(parque: String) {
        val location = getLocationFromText(parque)
        if (location != null) {
            val marker = Marker(map)
            marker.position = GeoPoint(location.latitude, location.longitude)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker)

            val mylocation = GeoPoint(originLat, originLon)
            val mapController: IMapController = map.controller
            mapController.setCenter(mylocation)
            mapController.setZoom(14.0)
            val myLocationMarker = Marker(map)
            myLocationMarker.position = mylocation
            myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(myLocationMarker)

            drawRoute(mylocation, location)
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()

        mGeocoder = Geocoder(baseContext)

        val mapController: IMapController = map.controller
        mapController.setZoom(18.0)

        //Cambiar de OSCURO -> CLARO o CLARO -> OSCURO
        val uiManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if(uiManager.nightMode == UiModeManager.MODE_NIGHT_YES)
            map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
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

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }
}