package com.dscvit.policewatch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dscvit.policewatch.databinding.ActivityPhoneNumberBinding
import com.dscvit.policewatch.repository.UserRepository
import com.dscvit.policewatch.ui.home.HomeActivity
import com.dscvit.policewatch.ui.utils.LoadingDialog
import com.dscvit.policewatch.ui.utils.showErrorSnackBar
import com.dscvit.policewatch.ui.utils.showSuccessSnackBar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class PhoneNumberActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PhoneNumberActivity"
    }

    private lateinit var binding: ActivityPhoneNumberBinding
    private val loadingDialog by lazy { LoadingDialog(this) }

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToHomeIfUserSignedIn()
        setupListeners()
    }

    private fun setupListeners() {
        binding.nextButton.setOnClickListener {
            if (binding.mobileNumberEditText.text.length != 10) {
                binding.root.showErrorSnackBar("Please enter a valid phone number")
            } else {
                verifyPhone("+91${binding.mobileNumberEditText.text}")
                loadingDialog.start("Sending an OTP...")
            }
        }
    }

    private fun navigateToHomeIfUserSignedIn() {
        if (Firebase.auth.currentUser != null) {
            navigateToHomeActivity()
        }
    }

    private fun navigateToVerifyOtpActivity(verificationID: String) {
        val intent = Intent(this, VerifyOtpActivity::class.java)
        intent.putExtra("verificationID", verificationID)
        startActivity(intent)
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val phoneAuthCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
                loadingDialog.stop()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                loadingDialog.stop()
                binding.root.showErrorSnackBar("onVerificationFailed: $e")
                Log.w(TAG, "onVerificationFailed", e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                loadingDialog.stop()
                Log.d(TAG, "onCodeSent:$verificationId")
                navigateToVerifyOtpActivity(verificationId)
            }
        }

    private fun verifyPhone(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(90L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(phoneAuthCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.root.showSuccessSnackBar("Signed In!")
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user

                    user?.getIdToken(false)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            userRepository.saveUserToken(it.result.token ?: "")
                        }
                    }

                    navigateToHomeActivity()
                } else {
                    binding.root.showErrorSnackBar("signInWithCredential:failure - ${task.exception}")
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
}