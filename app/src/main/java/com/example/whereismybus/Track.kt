package com.example.whereismybus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusAlert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.whereismybus.ui.theme.Linen
import com.example.whereismybus.ui.theme.Spruce
import com.example.whereismybus.ui.theme.WhereIsMyBusTheme
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Track : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhereIsMyBusTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                ) {
                    Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
                    Map()
                }
            }
        }
    }
    @Composable
    fun Map() {
        var loading by remember {
            mutableStateOf(true)
        }
        var loaded by remember {
            mutableStateOf(true)
        }
        AndroidView(factory = { context ->
            MapView(context).apply {
                setBuiltInZoomControls(false)
                setMultiTouchControls(true)
                controller.setZoom(20)
                controller.setCenter(GeoPoint(9.97743,76.57996))
                val routePoints = listOf(
                    GeoPoint(9.97711,76.57979),
                    GeoPoint(10.10781,76.48146),
                    GeoPoint(10.10875,76.48425),
                    GeoPoint(10.11106,76.48270),
                    GeoPoint(10.11145,76.47926),
                    GeoPoint(10.11370,76.47830),
                    GeoPoint(10.11243,76.41865),
                    GeoPoint(10.10712,76.36079),
                    GeoPoint(10.10446,76.35886),
                    GeoPoint(10.10759,76.35720),
                    GeoPoint(10.10671,76.35611)
                )
                fetchRoute(this,routePoints,fun() { loading = false }, fun() { loaded = true }, fun() { loaded = false} )
            }
        })
        if (loading) Loading()
        else {
            if (!loaded) {
                ErrorPage(fun() { loading = true })
            }
        }
    }
    @Composable
    fun Loading() {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
                .background(Linen)
        ) {
            CircularProgressIndicator(
                color = Spruce
            )
            Column (modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = "LOADING",
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
                    fontSize = 23.sp,
                    color = Spruce
                )
                Text(
                    text = "Please Wait",
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontSize = 15.sp,
                    color = Spruce
                )
            }
        }
    }

    @Composable
    fun ErrorPage(function: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Linen)
        ) {
            Icon(
                imageVector = Icons.Filled.BusAlert,
                contentDescription = "",
                tint = Color.Red,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .size(50.dp)
            )
            Text(
                text = "TRACKING FAILED",
                fontFamily = FontFamily(Font(R.font.inter_black)),
                fontSize = 20.sp,
                color = Spruce
            )
            Text(
                text = "Error: Network Error",
                fontFamily = FontFamily(Font(R.font.inter_bold)),
                fontSize = 15.sp,
                color = Spruce
            )
            IconButton(
                onClick = { function() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Sync,
                    contentDescription = "",
                    tint = Spruce,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }

    private fun fetchRoute(
        mapView: MapView,
        route: List<GeoPoint>,
        function: () -> Unit,
        function1: () -> Unit,
        function2: () -> Unit
    ) {
        Thread {
            val viaPoints = route.joinToString(";") {
                "${it.longitude},${it.latitude}"
            }
            val routerUrl = "https://router.project-osrm.org/route/v1/driving/$viaPoints?overview=full&geometries=geojson"
            val connection = URL(routerUrl).openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val responseBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        responseBuilder.append(line)
                    }
                    val result = responseBuilder.toString()
                    val jsonResponse = JSONObject(result)
                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val route = routes.getJSONObject(0)
                        val geometry = route.getJSONObject("geometry")
                        val coordinates = geometry.getJSONArray("coordinates")

                        val geoPoints = mutableListOf<GeoPoint>()
                        for (i in 0 until coordinates.length()) {
                            val cord = coordinates.getJSONArray(i)
                            geoPoints.add(GeoPoint(cord.getDouble(1), cord.getDouble(0)))
                        }
                        if(geoPoints.isEmpty()) {
                            function()
                            function2()
                        } else {
                            function()
                            function1()
                            mapView.overlayManager.add(Polyline().apply {
                                setPoints(geoPoints)
                                outlinePaint.color = android.graphics.Color.DKGRAY
                            })
                            mapView.invalidate()
                        }
                    }
                } else {
                    function()
                    function2()
                }
            } catch (error: Exception) {
                function()
                function2()
            }
        }.start()
    }
}


