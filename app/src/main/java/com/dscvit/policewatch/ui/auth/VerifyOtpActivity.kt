package com.dscvit.policewatch.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dscvit.policewatch.R
import com.dscvit.policewatch.databinding.ActivityVerifyOtpBinding

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}