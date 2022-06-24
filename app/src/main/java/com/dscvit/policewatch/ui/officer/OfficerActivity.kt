package com.dscvit.policewatch.ui.officer

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dscvit.policewatch.R
import com.dscvit.policewatch.databinding.ActivityOfficerBinding
import com.dscvit.policewatch.ui.auth.PhoneNumberActivity
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
}