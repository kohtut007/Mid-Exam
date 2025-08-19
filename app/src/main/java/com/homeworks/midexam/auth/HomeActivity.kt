package com.homeworks.midexam.auth

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.homeworks.midexam.R
import com.homeworks.midexam.databinding.ActivityHomeBinding
import com.homeworks.midexam.models.Status
import com.homeworks.midexam.models.User
import com.homeworks.midexam.auth.login.LoginActivity
import com.homeworks.midexam.auth.utils.GoogleSignInHelper
import com.homeworks.midexam.auth.utils.launchActivity
import com.homeworks.midexam.auth.utils.showToast
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import com.homeworks.midexam.auth.viewmodel.StatusViewModel
import com.homeworks.midexam.auth.viewmodel.UserViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private var currentUser: User? = null
    private val statuses: MutableList<Status> = mutableListOf()
    private lateinit var adapter: StatusAdapter
    private lateinit var statusViewModel: StatusViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        googleSignInHelper = GoogleSignInHelper(this)
        statusViewModel = ViewModelProvider(this)[StatusViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        setupUserData()
        setupRecycler()
        setupEventListeners()
        observeStatuses()
    }

    private fun setupUserData() {
        val userId = intent.getIntExtra("user_id", -1)
        val username = intent.getStringExtra("username") ?: "User"
        
        if (userId != -1) {
            userViewModel.getUserById(userId) { user ->
                currentUser = user
                if (user != null) {
                    statusViewModel.loadStatuses(user.id)
                }
            }
        }
        
        binding.tvUsername.text = username
    }

    private fun setupEventListeners() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        
        binding.fabQuickStatus.setOnClickListener { showStatusDialog() }
    }

    private fun setupRecycler() {
        adapter = StatusAdapter(
            onEdit = { status -> showStatusDialog(isEditing = true, statusToEdit = status) },
            onDelete = { status -> showDeleteConfirmation(status) }
        )
        binding.rvStatuses.layoutManager = LinearLayoutManager(this)
        binding.rvStatuses.adapter = adapter
    }

    private fun observeStatuses() {
        statusViewModel.statuses.observe(this) { list ->
            statuses.clear()
            statuses.addAll(list)
            adapter.submitList(list.toList())
            binding.tvNoStatus.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    private fun formatTime(timestamp: String): String {
        return "Just now"
    }

    private fun showStatusDialog(isEditing: Boolean = false, statusToEdit: Status? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_status, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val statusEditText = dialogView.findViewById<EditText>(R.id.etStatus)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<TextView>(R.id.btnSave)

        if (isEditing) {
            dialogTitle.text = "Edit Status"
            statusEditText.setText(statusToEdit?.statusText ?: "")
            statusEditText.setSelection(statusEditText.text.length)
        } else {
            dialogTitle.text = "Add Status"
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val statusText = statusEditText.text.toString().trim()
            if (statusText.isEmpty()) {
                showToast("Please enter a status")
                return@setOnClickListener
            }

            if (statusText.length > 280) {
                showToast("Status is too long (max 280 characters)")
                return@setOnClickListener
            }

            if (isEditing && statusToEdit != null) {
                updateStatus(statusToEdit, statusText)
            } else {
                addStatus(statusText)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addStatus(statusText: String) {
        currentUser?.let { user ->
            statusViewModel.addStatus(user.id, statusText) { ok ->
                if (ok) showToast("Status added successfully!") else showToast("Failed to add status. Please try again.")
            }
        }
    }

    private fun updateStatus(status: Status, statusText: String) {
        currentUser?.let { user ->
            statusViewModel.updateStatus(status.id, user.id, statusText) { ok ->
                if (ok) showToast("Status updated successfully!") else showToast("Failed to update status. Please try again.")
            }
        }
    }

    private fun showDeleteConfirmation(status: Status) {
        AlertDialog.Builder(this)
            .setTitle("Delete Status")
            .setMessage("Are you sure you want to delete your status?")
            .setPositiveButton("Delete") { _, _ ->
                deleteStatus(status)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStatus(status: Status) {
        currentUser?.let { user ->
            statusViewModel.deleteStatus(status.id, user.id) { ok ->
                if (ok) showToast("Status deleted successfully!") else showToast("Failed to delete status. Please try again.")
            }
        }
    }

    private fun showLogoutConfirmation() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancelLogout)
        val btnConfirm = dialogView.findViewById<TextView>(R.id.btnConfirmLogout)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            logout()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun logout() {
        googleSignInHelper.signOut()
        showToast("Logged out successfully!")
        launchActivity<LoginActivity>()
        finish()
    }
}