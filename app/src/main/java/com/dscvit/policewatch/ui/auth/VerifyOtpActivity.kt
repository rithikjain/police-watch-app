package com.dscvit.policewatch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dscvit.policewatch.databinding.ActivityVerifyOtpBinding
import com.dscvit.policewatch.ui.home.HomeActivity
import com.dscvit.policewatch.ui.utils.LoadingDialog
import com.dscvit.policewatch.ui.utils.showErrorSnackBar
import com.dscvit.policewatch.ui.utils.showSuccessSnackBar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerifyOtpActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "VerifyOtpActivity"
    }

    private lateinit var binding: ActivityVerifyOtpBinding
    private val loadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.verifyButton.setOnClickListener {
            if (binding.otpTextField.otp?.length != 6) {
                binding.root.showErrorSnackBar("Please enter a valid OTP")
            } else {
                loadingDialog.start("Verifying OTP...")
                verifyOTP(binding.otpTextField.otp ?: "")
            }
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun verifyOTP(code: String) {
        val credential =
            PhoneAuthProvider.getCredential(intent.getStringExtra("verificationID") ?: "", code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                loadingDialog.stop()

                if (task.isSuccessful) {
                    binding.root.showSuccessSnackBar("Signed In!")
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user

                    navigateToHomeActivity()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.root.showErrorSnackBar("Wrong OTP")
                    } else {
                        binding.root.showErrorSnackBar("${task.exception?.message}")
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
            }
    }
}