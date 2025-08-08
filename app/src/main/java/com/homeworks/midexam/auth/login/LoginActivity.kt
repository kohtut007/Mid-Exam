package com.homeworks.midexam.auth.login

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.homeworks.midexam.auth.HomeActivity
import com.homeworks.midexam.auth.register.RegisterActivity
import com.homeworks.midexam.auth.utils.launchActivity
import com.homeworks.midexam.auth.utils.showToast
import com.homeworks.midexam.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        catchEvent()
        setupLoginForm()
    }

    private fun catchEvent() {
        binding.apply {
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            llSignUp.setOnClickListener {
                launchActivity<RegisterActivity>()
            }
        }
    }

    private fun setupLoginForm() {
        binding.etPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.tilPassword.setEndIconTintList(ColorStateList.valueOf(Color.BLACK))

        binding.etUsername.doAfterTextChanged {
            binding.tvUsernameWarning.showWarning(validateUsername(binding.etUsername.text.toString()))
        }
        binding.etPassword.doAfterTextChanged {
            binding.tvPasswordWarning.showWarning(validatePassword(binding.etPassword.text.toString()))
        }
        binding.tilPassword.setEndIconOnClickListener {
            togglePasswordVisibility()
        }
        binding.btnLogin.setOnClickListener {
            val userValid = validateUsername(binding.etUsername.text.toString())
            val passValid = validatePassword(binding.etPassword.text.toString())
            binding.tvUsernameWarning.showWarning(userValid)
            binding.tvPasswordWarning.showWarning(passValid)
            if (userValid == null && passValid == null) {
                launchActivity<HomeActivity> {
                    putExtra("username", binding.etUsername.text.toString())
                }
                finish()
            }
        }
        binding.tvSignUpHere.setOnClickListener {
            launchActivity<RegisterActivity>()
        }
        binding.btnSignInWithGoogle.setOnClickListener {
            showToast("Google login clicked")
        }
    }

    private fun validateUsername(username: String): String? =
        if (username.isBlank()) "Username cannot be empty" else null

    private fun validatePassword(password: String): String? =
        if (password.length < 6) "Password must be at least 6 characters" else null

    private fun TextView.showWarning(msg: String?) {
        text = msg ?: ""
        visibility = if (msg == null) TextView.GONE else TextView.VISIBLE
    }

    class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
        override fun getTransformation(
            source: CharSequence,
            view: android.view.View,
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
}
