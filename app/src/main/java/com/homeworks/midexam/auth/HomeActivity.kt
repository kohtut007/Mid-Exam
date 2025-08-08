package com.homeworks.midexam.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.homeworks.midexam.R
import com.homeworks.midexam.databinding.ActivityHomeBinding
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showWelcomeMessage()
    }

    private fun showWelcomeMessage() {
        val username = intent.getStringExtra("username") ?: "User"
        binding.main.removeAllViews()
        val welcomeText = TextView(this).apply {
            text = "Welcome, $username!"
            textSize = 28f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(resources.getColor(R.color.colorPrimary, null))
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "We're glad to have you back."
            textSize = 18f
            setTextColor(resources.getColor(R.color.black, null))
            gravity = Gravity.CENTER
        }
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        welcomeText.layoutParams = params
        subtitle.layoutParams = params
        binding.main.addView(welcomeText)
        binding.main.addView(subtitle)
    }
}