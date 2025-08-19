package com.homeworks.midexam.auth.login

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.homeworks.midexam.R
import com.homeworks.midexam.auth.HomeActivity
import com.homeworks.midexam.auth.register.RegisterActivity
import com.homeworks.midexam.auth.utils.GoogleSignInHelper
import com.homeworks.midexam.auth.utils.launchActivity
import com.homeworks.midexam.auth.utils.showToast
import com.homeworks.midexam.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import androidx.lifecycle.ViewModelProvider
import com.homeworks.midexam.auth.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private lateinit var userViewModel: UserViewModel

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
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
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        googleSignInHelper = GoogleSignInHelper(this)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        catchEvent()
        setupLoginForm()
        checkExistingGoogleSignIn()
        checkKeepSignedInPreference()
    }

    private fun catchEvent() {
        binding.apply {
            llSignUp.setOnClickListener {
                launchActivity<RegisterActivity>()
            }
        }
    }

    private fun setupLoginForm() = binding.apply {
        etPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        tilPassword.setEndIconTintList(ColorStateList.valueOf(Color.BLACK))

        etUsername.doAfterTextChanged {
            tvUsernameWarning.showWarning(validateUsername(etUsername.text.toString()))
        }
        etPassword.doAfterTextChanged {
            tvPasswordWarning.showWarning(validatePassword(etPassword.text.toString()))
        }
        tilPassword.setEndIconOnClickListener {
            togglePasswordVisibility()
        }
        btnLogin.setOnClickListener {
            val userValid = validateUsername(etUsername.text.toString())
            val passValid = validatePassword(etPassword.text.toString())
            tvUsernameWarning.showWarning(userValid)
            tvPasswordWarning.showWarning(passValid)
            if (userValid == null && passValid == null) {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()
                userViewModel.login(username, password) { user ->
                    if (user != null) {
                        if (cbKeepSignedIn.isChecked) {
                            saveKeepSignedInPreference(username)
                        }
                        launchActivity<HomeActivity> {
                            putExtra("user_id", user.id)
                            putExtra("username", user.username)
                            finish()
                        }
                        finish()
                    } else {
                        showToast("Invalid username or password!")
                    }
                }
            }
        }
        tvSignUpHere.setOnClickListener {
            launchActivity<RegisterActivity>()
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

    private fun TextView.showWarning(msg: String?) {
        text = msg ?: ""
        visibility = if (msg == null) TextView.GONE else TextView.VISIBLE
    }

    class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
        override fun getTransformation(
            source: CharSequence,
            view: View,
        ): CharSequence {
            return PasswordCharSequence(source)
        }

        private class PasswordCharSequence(private val source: CharSequence) : CharSequence {
            override val length: Int get() = source.length
            override fun get(index: Int): Char = '*'
            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
                PasswordCharSequence(source.subSequence(startIndex, endIndex))
        }
    }

    private fun togglePasswordVisibility() {
        val etPassword = binding.etPassword
        if (etPassword.transformationMethod == null) {
            etPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        } else {
            etPassword.transformationMethod = null
        }
        etPassword.setSelection(etPassword.text?.length ?: 0)
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

        userViewModel.getUserByUsername(email) { existing ->
            if (existing != null) {
                userViewModel.login(email, "google_auth") { user ->
                    if (user != null) {
                        launchActivity<HomeActivity> {
                            putExtra("user_id", user.id)
                            putExtra("username", user.username)
                        }
                        finish()
                    } else {
                        showToast("Account exists but authentication failed")
                    }
                }
            } else {
                userViewModel.register(email, "google_auth") { id ->
                    if (id != -1L) {
                        showToast("Welcome, $displayName!")
                        launchActivity<HomeActivity> {
                            putExtra("user_id", id.toInt())
                            putExtra("username", displayName)
                        }
                        finish()
                    } else {
                        showToast("Failed to create account")
                    }
                }
            }
        }
    }

    private fun checkExistingGoogleSignIn() {
        val account = googleSignInHelper.getLastSignedInAccount()
        if (account != null) {
            handleSuccessfulGoogleSignIn(account)
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

    private fun saveKeepSignedInPreference(username: String) {
        val sharedPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putBoolean("keep_signed_in", true)
            putString("saved_username", username)
            putLong("login_timestamp", System.currentTimeMillis())
            apply()
        }
    }

    private fun checkKeepSignedInPreference() {
        val sharedPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val keepSignedIn = sharedPrefs.getBoolean("keep_signed_in", false)
        val savedUsername = sharedPrefs.getString("saved_username", "")
        val loginTimestamp = sharedPrefs.getLong("login_timestamp", 0)

        // Check if preference is still valid
        if (keepSignedIn && !savedUsername.isNullOrEmpty() &&
            System.currentTimeMillis() - loginTimestamp < 30 * 24 * 60 * 60 * 1000
        ) {
            userViewModel.getUserByUsername(savedUsername) { user ->
                if (user != null) {
                    launchActivity<HomeActivity> {
                        putExtra("user_id", user.id)
                        putExtra("username", user.username)
                    }
                    finish()
                }
            }
        }
    }
}
