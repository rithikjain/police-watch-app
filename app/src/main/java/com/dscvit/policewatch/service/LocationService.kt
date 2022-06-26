package com.dscvit.policewatch.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dscvit.policewatch.R
import com.dscvit.policewatch.models.Coordinates
import com.dscvit.policewatch.models.Location
import com.dscvit.policewatch.repository.UserRepository
import com.dscvit.policewatch.utils.Constants
import com.google.android.gms.location.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject
import javax.net.ssl.SSLSocketFactory

@AndroidEntryPoint
class LocationService : Service() {

    companion object {
        const val TAG = "LocationService"
        const val CHANNEL_ID = "Police_Watch_Notifications"

        // Service Actions
        const val START = "START"
        const val STOP = "STOP"

        // Intent Extras
        const val SERVICE_ACTION = "SERVICE_ACTION"

        // Service Running Status
        var IS_RUNNING = false
    }

    // Getting access to the NotificationManager
    private lateinit var notificationManager: NotificationManager

    // Web socket
    private var webSocketClient: WebSocketClient? = null

    @Inject
    lateinit var userRepository: UserRepository

    lateinit var locationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        IS_RUNNING = true

        createChannel()
        getNotificationManager()
        connectToWebsocket()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        getNotificationManager()

        val action = intent?.getStringExtra(SERVICE_ACTION) ?: ""

        Log.d("LocationService", "onStartCommand Action: $action")

        when (action) {
            START -> startLocationService()
            STOP -> stopLocationService()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        IS_RUNNING = false
        if (locationCallback != null) {
            locationClient.removeLocationUpdates(locationCallback!!)
        }
        webSocketClient?.close()
    }

    private fun startLocationService() {
        buildNotificationAndStartForeground()
        requestLocationUpdates()
    }

    private fun stopLocationService() {
        stopForeground(true)
        webSocketClient?.close()
        webSocketClient = null
        stopSelf()
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest.create().apply {
            interval = 6000
            fastestInterval = 6000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val gson = Gson()

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkPermissions()) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    val location = locationResult.lastLocation
                    if (location != null) {
                        if (webSocketClient != null && webSocketClient?.isOpen == true) {
                            Toast.makeText(
                                this@LocationService,
                                "Lat: ${location.latitude} Lon: ${location.longitude}",
                                Toast.LENGTH_SHORT
                            ).show()

                            val locationModel =
                                Location(Coordinates(location.latitude, location.longitude))
                            webSocketClient?.send(gson.toJson(locationModel))
                        } else if (webSocketClient != null && webSocketClient?.isClosed == true) {
                            webSocketClient?.reconnect()
                        }
                    }
                }
            }
            locationClient.requestLocationUpdates(
                request,
                locationCallback as LocationCallback, null
            )
        }
    }

    private fun connectToWebsocket() {
        val uri = URI("wss://${Constants.BASE_URL}/ws/patroller/location")
        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        val headers = HashMap<String, String>()
        headers["Authorization"] = userRepository.getSavedUserToken()

        webSocketClient = object : WebSocketClient(uri, headers) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "WebSocket onOpen")
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "WebSocket onMessage: $message")
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "WebSocket onClose: $reason")
            }

            override fun onError(ex: Exception?) {
                Log.d(TAG, "WebSocket onError: ${ex?.message}")
            }
        }

        webSocketClient?.setSocketFactory(socketFactory)
        webSocketClient?.connect()
    }

    private fun checkPermissions(): Boolean {
        val finePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (finePermission != PackageManager.PERMISSION_GRANTED) return false
        val backgroundPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (backgroundPermission != PackageManager.PERMISSION_GRANTED) return false
        return true
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun buildNotificationAndStartForeground() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle("Location Tracker")
            .setContentText("Police Watch is tracking your location")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        startForeground(101, notification)
    }

}