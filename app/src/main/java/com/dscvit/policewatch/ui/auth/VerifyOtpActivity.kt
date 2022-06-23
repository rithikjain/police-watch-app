package com.dscvit.policewatch.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dscvit.policewatch.databinding.ActivityVerifyOtpBinding
import com.dscvit.policewatch.repository.UserRepository
import com.dscvit.policewatch.ui.home.HomeActivity
import com.dscvit.policewatch.ui.utils.LoadingDialog
import com.dscvit.policewatch.ui.utils.showErrorSnackBar
import com.dscvit.policewatch.utils.Resource
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VerifyOtpActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "VerifyOtpActivity"
    }

    private lateinit var binding: ActivityVerifyOtpBinding
    private val loadingDialog by lazy { LoadingDialog(this) }
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
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

    private fun setupObservers() {
        viewModel.getUserLiveData.observe(this) {
            loadingDialog.stop()
            when (it) {
                is Resource.Success -> {
                    Log.d(TAG, "Status Code: ${it.statusCode.toString()}")
                    viewModel.setUserSignedIn()
                    navigateToHomeActivity()
                }
                is Resource.Error -> {
                    if (it.statusCode == 401) {
                        binding.root.showErrorSnackBar("Phone number does not exist in database!")
                    } else {
                        binding.root.showErrorSnackBar("Network Error")
                    }
                    Log.d(TAG, "Status Code: ${it.statusCode.toString()}, Message: ${it.message}")
                }
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
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user

                    user?.getIdToken(false)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            userRepository.saveUserToken(it.result.token ?: "")
                            loadingDialog.start("Checking if phone number exists in database...")
                            viewModel.getUser(it.result.token ?: "")
                        }
                    }
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