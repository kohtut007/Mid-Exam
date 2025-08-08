package com.homeworks.midexam.auth.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

inline fun <reified T : Activity> Context.launchActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivity(intent)
}

