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
import com.google.android.gms.location.*

class LocationService : Service() {

    companion object {
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

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        IS_RUNNING = true

        createChannel()
        getNotificationManager()
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
    }

    private fun startLocationService() {
        buildNotificationAndStartForeground()
        requestLocationUpdates()
    }

    private fun stopLocationService() {
        stopForeground(true)
        stopSelf()
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 4000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val client = LocationServices.getFusedLocationProviderClient(this)
        if (checkPermissions()) {
            client.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    val location = locationResult.lastLocation
                    if (location != null) {
                        Toast.makeText(
                            this@LocationService,
                            "Lat: ${location.latitude} Lon: ${location.longitude}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }, null)
        }
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