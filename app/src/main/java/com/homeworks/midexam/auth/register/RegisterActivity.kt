package com.homeworks.midexam.auth.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.homeworks.midexam.databinding.ActivityRegisterBinding
import android.text.method.PasswordTransformationMethod
import android.widget.TextView
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.widget.doAfterTextChanged
import com.homeworks.midexam.auth.utils.showToast
import com.homeworks.midexam.auth.utils.launchActivity
import com.homeworks.midexam.auth.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        catchEvent()
        setupRegisterForm()
    }

    private fun catchEvent() {
        binding.apply {
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
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
            tvPasswordWarning.showWarning(validatePassword(etPassword.text.toString()))
            tvConfirmPasswordWarning.showWarning(validateConfirmPassword(
                etPassword.text.toString(),
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
            if (userValid == null && passValid == null && confirmValid == null) {
                showToast("Registration successful!")
                launchActivity<LoginActivity>()
                finish()
            }
        }
    }

    private fun validateUsername(username: String): String? =
        if (username.isBlank()) "Username cannot be empty" else null

    private fun validatePassword(password: String): String? =
        if (password.length < 6) "Password must be at least 6 characters" else null

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
        (etPassword as? android.widget.EditText)?.setSelection(etPassword.text?.length ?: 0)
    }
}