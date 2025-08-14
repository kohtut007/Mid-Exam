package com.homeworks.midexam.auth.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleSignInHelper(private val context: Context) {
    
    companion object {
        const val RC_SIGN_IN = 9001
    }
    
    private lateinit var googleSignInClient: GoogleSignInClient
    
    init {
        setupGoogleSignIn()
    }
    
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    fun getSignInIntent(): Intent {
        // Check network connectivity before proceeding
        if (!isNetworkAvailable()) {
            throw IllegalStateException("No internet connection available")
        }
        return googleSignInClient.signInIntent
    }
    
    fun handleSignInResult(data: Intent?): GoogleSignInResult {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(ApiException::class.java)
            GoogleSignInResult.Success(account)
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
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
                com.google.android.gms.common.api.CommonStatusCodes.DEVELOPER_ERROR -> 
                    "Developer error. Please contact support."
                com.google.android.gms.common.api.CommonStatusCodes.ERROR -> 
                    "An error occurred. Please try again."
                else -> "Sign-in failed (Error: ${e.statusCode})"
            }
            GoogleSignInResult.Error(e.statusCode, errorMessage)
        }
    }
    
    fun signOut() {
        googleSignInClient.signOut()
    }
    
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    sealed class GoogleSignInResult {
        data class Success(val account: GoogleSignInAccount) : GoogleSignInResult()
        data class Error(val statusCode: Int, val message: String) : GoogleSignInResult()
    }
}
