package com.homeworks.midexam.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.homeworks.midexam.database.repository.StatusRepository
import com.homeworks.midexam.models.Status
import kotlinx.coroutines.launch

class StatusViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = StatusRepository(application)

	private val _statuses = MutableLiveData<List<Status>>()
	val statuses: LiveData<List<Status>> = _statuses

	fun loadStatuses(userId: Int) {
		viewModelScope.launch {
			_statuses.postValue(repository.getStatusesByUserId(userId))
		}
	}

	fun addStatus(userId: Int, statusText: String, onResult: (Boolean) -> Unit) {
		viewModelScope.launch {
			val id = repository.addStatus(userId, statusText)
			onResult(id != -1L)
			loadStatuses(userId)
		}
	}

	fun updateStatus(statusId: Int, userId: Int, statusText: String, onResult: (Boolean) -> Unit) {
		viewModelScope.launch {
			val rows = repository.updateStatus(statusId, statusText)
			onResult(rows > 0)
			loadStatuses(userId)
		}
	}

	fun deleteStatus(statusId: Int, userId: Int, onResult: (Boolean) -> Unit) {
		viewModelScope.launch {
			val rows = repository.deleteStatus(statusId)
			onResult(rows > 0)
			loadStatuses(userId)
		}
	}
}


