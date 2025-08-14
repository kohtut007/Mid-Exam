package com.homeworks.midexam.auth.register

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.homeworks.midexam.auth.login.LoginActivity
import com.homeworks.midexam.auth.utils.GoogleSignInHelper
import com.homeworks.midexam.auth.utils.launchActivity
import com.homeworks.midexam.auth.utils.showToast
import com.homeworks.midexam.database.DatabaseHelper
import com.homeworks.midexam.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var googleSignInHelper: GoogleSignInHelper

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("RegisterActivity>>", "Google Sign-In result: ${result.resultCode}")
        Log.d("RegisterActivity>>", "Data: ${result.data}")
        when (result.resultCode) {
            RESULT_OK -> {
                handleGoogleSignInResult(result.data)
            }
            RESULT_CANCELED -> {
                // User cancelled the sign-in process
                showToast("Google Sign-In was cancelled by user")
            }
            else -> {
                // Handle other result codes
                handleGoogleSignInError(result.resultCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseHelper = DatabaseHelper(this)
        googleSignInHelper = GoogleSignInHelper(this)
        catchEvent()
        setupRegisterForm()
    }

    private fun catchEvent() {
        binding.llPasswordStrength.visibility = View.GONE
        binding.apply {
            llSignUp.setOnClickListener {
                launchActivity<LoginActivity>()
            }
        }
    }

    private fun setupRegisterForm() = binding.apply {
        etPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        tilPassword.setEndIconTintList(ColorStateList.valueOf(Color.BLACK))
        etConfirmPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        tilConfirmPassword.setEndIconTintList(ColorStateList.valueOf(Color.BLACK))

        etUsername.doAfterTextChanged {
            tvUsernameWarning.showWarning(validateUsername(etUsername.text.toString()))
        }
        etPassword.doAfterTextChanged {
            val password = etPassword.text.toString()
            tvPasswordWarning.showWarning(validatePassword(password))
            updatePasswordStrength(password)
            tvConfirmPasswordWarning.showWarning(validateConfirmPassword(
                password,
                etConfirmPassword.text.toString()
            ))
        }
        etConfirmPassword.doAfterTextChanged {
            tvConfirmPasswordWarning.showWarning(validateConfirmPassword(
                etPassword.text.toString(),
                etConfirmPassword.text.toString()
            ))
        }
        tilPassword.setEndIconOnClickListener {
            togglePasswordVisibility(etPassword)
        }
        tilConfirmPassword.setEndIconOnClickListener {
            togglePasswordVisibility(etConfirmPassword)
        }
        btnRegister.setOnClickListener {
            val userValid = validateUsername(etUsername.text.toString())
            val passValid = validatePassword(etPassword.text.toString())
            val confirmValid = validateConfirmPassword(
                etPassword.text.toString(),
                etConfirmPassword.text.toString()
            )
            tvUsernameWarning.showWarning(userValid)
            tvPasswordWarning.showWarning(passValid)
            tvConfirmPasswordWarning.showWarning(confirmValid)
            
            // Check terms and conditions
            if (!cbTermsAndConditions.isChecked) {
                showToast("Please accept the Terms and Conditions to continue.")
                return@setOnClickListener
            }
            
            if (userValid == null && passValid == null && confirmValid == null) {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()
                
                // Check if user already exists
                if (databaseHelper.checkUserExists(username)) {
                    showToast("Username already exists! Please choose a different username.")
                    return@setOnClickListener
                }
                
                // Add user to database
                val userId = databaseHelper.addUser(username, password)
                if (userId != -1L) {
                    showToast("Registration successful! Please login.")
                    launchActivity<LoginActivity>()
                    finish()
                } else {
                    showToast("Registration failed! Please try again.")
                }
            }
        }
        
        btnSignInWithGoogle.setOnClickListener {
            startGoogleSignIn()
        }
    }

    private fun validateUsername(username: String): String? =
        if (username.isBlank()) "Username cannot be empty" else null

    private fun validatePassword(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { !it.isLetterOrDigit() } -> "Password must contain at least one special character"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirm: String): String? =
        when {
            confirm.isBlank() -> "Please confirm your password"
            password != confirm -> "Passwords do not match"
            else -> null
        }

    private fun TextView.showWarning(msg: String?) {
        text = msg ?: ""
        visibility = if (msg == null) TextView.GONE else TextView.VISIBLE
    }

    class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: android.view.View): CharSequence {
            return PasswordCharSequence(source)
        }
        private class PasswordCharSequence(private val source: CharSequence) : CharSequence {
            override val length: Int get() = source.length
            override fun get(index: Int): Char = '*'
            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
                PasswordCharSequence(source.subSequence(startIndex, endIndex))
        }
    }

    private fun togglePasswordVisibility(etPassword: TextView) {
        if (etPassword.transformationMethod == null) {
            etPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        } else {
            etPassword.transformationMethod = null
        }
        (etPassword as? EditText)?.setSelection(etPassword.text?.length ?: 0)
    }

    private fun updatePasswordStrength(password: String) {
        val strength = when {
            password.isEmpty() -> {
                binding.llPasswordStrength.visibility = View.GONE
                return
            }
            password.length < 8 -> "Very Weak"
            !password.any { it.isUpperCase() } || !password.any { it.isLowerCase() } -> "Weak"
            !password.any { it.isDigit() } -> "Fair"
            !password.any { !it.isLetterOrDigit() } -> "Good"
            else -> "Strong"
        }

        val color = when (strength) {
            "Very Weak" -> Color.RED
            "Weak" -> Color.parseColor("#FF8C00") // Orange
            "Fair" -> Color.parseColor("#FFD700") // Gold
            "Good" -> Color.parseColor("#32CD32") // Lime Green
            "Strong" -> Color.GREEN
            else -> Color.RED
        }

        binding.llPasswordStrength.visibility = View.VISIBLE
        binding.tvPasswordStrength.text = strength
        binding.tvPasswordStrength.setTextColor(color)
    }

    private fun startGoogleSignIn() {
        try {
            val signInIntent = googleSignInHelper.getSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        } catch (e: IllegalStateException) {
            if (e.message?.contains("internet") == true) {
                showToast("No internet connection. Please check your network and try again.")
            } else {
                showToast("Google Sign-In is not available. Please try again.")
            }
        } catch (e: Exception) {
            showToast("Failed to start Google Sign-In: ${e.message}")
        }
    }

    private fun handleGoogleSignInResult(data: Intent?) {
        try {
            val result = googleSignInHelper.handleSignInResult(data)
            when (result) {
                is GoogleSignInHelper.GoogleSignInResult.Success -> {
                    val account = result.account
                    handleSuccessfulGoogleSignIn(account)
                }
                is GoogleSignInHelper.GoogleSignInResult.Error -> {
                    handleGoogleSignInError(result.statusCode, result.message)
                }
            }
        } catch (e: Exception) {
            showToast("Google Sign-In failed: ${e.message ?: "Unknown error"}")
        }
    }

    private fun handleSuccessfulGoogleSignIn(account: GoogleSignInAccount) {
        val email = account.email ?: ""
        val displayName = account.displayName ?: account.email?.split("@")?.first() ?: "User"
        
        // Check if user exists in database
        if (databaseHelper.checkUserExists(email)) {
            showToast("Account already exists! Please login instead.")
            launchActivity<LoginActivity>()
            finish()
        } else {
            // Create new user account
            val userId = databaseHelper.addUser(email, "google_auth")
            if (userId != -1L) {
                showToast("Registration successful with Google! Welcome, $displayName!")
                launchActivity<LoginActivity>()
                finish()
            } else {
                showToast("Failed to create account")
            }
        }
    }

    private fun handleGoogleSignInError(resultCode: Int) {
        when (resultCode) {
            GoogleSignInHelper.RC_SIGN_IN -> {
                showToast("Google Sign-In failed: Please try again")
            }
            else -> {
                showToast("Google Sign-In failed: Error code $resultCode")
            }
        }
    }

    private fun handleGoogleSignInError(statusCode: Int, message: String) {
        val errorMessage = when (statusCode) {
            com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR -> 
                "Network error. Please check your internet connection."
            com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT -> 
                "Request timed out. Please try again."
            com.google.android.gms.common.api.CommonStatusCodes.CANCELED -> 
                "Sign-in was cancelled."
            com.google.android.gms.common.api.CommonStatusCodes.INVALID_ACCOUNT -> 
                "Invalid account. Please try with a different account."
            com.google.android.gms.common.api.CommonStatusCodes.SIGN_IN_REQUIRED -> 
                "Sign-in required. Please try again."
            com.google.android.gms.common.api.CommonStatusCodes.INTERNAL_ERROR -> 
                "Internal error. Please try again later."
            else -> "Google Sign-In failed: $message"
        }
        showToast(errorMessage)
    }
}