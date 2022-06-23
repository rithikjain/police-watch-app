package com.dscvit.policewatch.ui.supervisor

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dscvit.policewatch.R
import com.dscvit.policewatch.databinding.ActivitySupervisorBinding
import com.dscvit.policewatch.ui.auth.PhoneNumberActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SupervisorActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "SupervisorActivity"
    }

    private lateinit var binding: ActivitySupervisorBinding
    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySupervisorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
        setupMap()
    }

    private fun setupListeners() {
        binding.moreOptionsImageView.setOnClickListener {
            val popUpMenu = PopupMenu(this, binding.moreOptionsImageView)
            popUpMenu.menuInflater.inflate(R.menu.pop_up_menu, popUpMenu.menu)

            popUpMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.sign_out) {
                    signOut()
                }
                return@setOnMenuItemClickListener true
            }

            popUpMenu.show()
        }
    }

    private fun setupObservers() {
        viewModel.idToken.observe(this) {
            // DO something with the ID Token here
            Log.d(TAG, it ?: "")
        }
    }

    private fun setupMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun signOut() {
        Firebase.auth.signOut()
        viewModel.resetSavedUserToken()
        viewModel.setUserSignedOut()
        navigateToPhoneNumberActivity()
    }

    private fun navigateToPhoneNumberActivity() {
        val intent = Intent(this, PhoneNumberActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        try {
            val success =
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        map.setMinZoomPreference(10f)

        val home = LatLng(12.888593, 77.545432)

        val iconGenerator = IconGenerator(this)
        iconGenerator.setStyle(IconGenerator.STYLE_BLUE)

        map.addMarker(
            MarkerOptions()
                .position(home)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("Home")))
                .anchor(iconGenerator.anchorU, iconGenerator.anchorV)
        )

        map.addCircle(
            CircleOptions()
                .center(home)
                .radius(100.0)
                .strokeColor(Color.parseColor("#7087CEEB"))
                .fillColor(Color.parseColor("#6087CEEB"))
        )

        val cameraPosition = CameraPosition.Builder()
            .target(home)
            .zoom(13f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}