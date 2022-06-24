package com.dscvit.policewatch.ui.officer

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dscvit.policewatch.R
import com.dscvit.policewatch.databinding.ActivityOfficerBinding
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
                // TODO: Handle stop location sharing
            } else {
                binding.toggleLocationButton.text = "Stop Sharing Location"
                viewModel.isSharingLocation = true
                // TODO: Handle start location sharing
            }
        }
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
                    } else {
                        Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
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