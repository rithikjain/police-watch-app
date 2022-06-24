package com.dscvit.policewatch.ui.officer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dscvit.policewatch.R
import com.dscvit.policewatch.databinding.ActivityOfficerBinding
import com.dscvit.policewatch.service.LocationService
import com.dscvit.policewatch.ui.auth.PhoneNumberActivity
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.liveData
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OfficerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfficerBinding
    private val viewModel: OfficerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOfficerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLocationPermissions()
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        binding.nameTextView.text = "Hi, ${viewModel.getSavedUser()?.firstName}"
        if (LocationService.IS_RUNNING) {
            binding.toggleLocationButton.text = "Stop Sharing Location"
            viewModel.isSharingLocation = true
        } else {
            binding.toggleLocationButton.text = "Start Sharing Location"
            viewModel.isSharingLocation = false
        }
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

        binding.toggleLocationButton.setOnClickListener {
            if (viewModel.isSharingLocation) {
                binding.toggleLocationButton.text = "Start Sharing Location"
                viewModel.isSharingLocation = false
                // Stop sharing location
                stopLocationService()
            } else {
                // Start sharing location
                if (checkIfAllPermissionsAreGranted()) {
                    startLocationService()
                    binding.toggleLocationButton.text = "Stop Sharing Location"
                    viewModel.isSharingLocation = true
                } else {
                    showPermissionRequestDialog()
                }
            }
        }
    }

    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        intent.putExtra(LocationService.SERVICE_ACTION, LocationService.START)
        startService(intent)
    }

    private fun stopLocationService() {
        val intent = Intent(this, LocationService::class.java)
        intent.putExtra(LocationService.SERVICE_ACTION, LocationService.STOP)
        stopService(intent)
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

    private fun checkIfAllPermissionsAreGranted(): Boolean {
        val permissions = listOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun getLocationPermissions() {
        permissionsBuilder(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).build().send { result ->
            if (!result.allGranted()) {
                showPermissionRequestDialog()
            } else {
                permissionsBuilder(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ).build().send { res ->
                    if (!res.allGranted()) {
                        showPermissionRequestDialog()
                    }
                }
            }
        }
    }

    private fun showPermissionRequestDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Location Permission Required")
            .setMessage("Location permission is needed to use this app, kindly select the Allow all time option.")
            .setCancelable(false)
            .setPositiveButton("Okay") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                dialog.dismiss()
            }
            .show()
    }
}