package com.dscvit.policewatch.ui.supervisor

import android.content.Intent
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

        map.setMinZoomPreference(10f)

        val home = LatLng(12.888593, 77.545432)
        map.addMarker(
            MarkerOptions()
                .position(home)
                .title("Marker at Home")
        )
        val cameraPosition = CameraPosition.Builder()
            .target(home)
            .zoom(13f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}