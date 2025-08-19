package com.homeworks.midexam.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.homeworks.midexam.database.repository.UserRepository
import com.homeworks.midexam.models.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = UserRepository(application)

	private val _user = MutableLiveData<User?>()
	val user: LiveData<User?> = _user

	fun login(username: String, password: String, onResult: (User?) -> Unit) {
		viewModelScope.launch {
			val result = repository.validateUser(username, password)
			_user.postValue(result)
			onResult(result)
		}
	}

	fun register(username: String, password: String, onResult: (Long) -> Unit) {
		viewModelScope.launch {
			val id = repository.addUser(username, password)
			onResult(id)
		}
	}

	suspend fun userExists(username: String): Boolean {
		return repository.checkUserExists(username)
	}

	fun getUserById(userId: Int, onResult: (User?) -> Unit) {
		viewModelScope.launch {
			onResult(repository.getUserById(userId))
		}
	}

	fun getUserByUsername(username: String, onResult: (User?) -> Unit) {
		viewModelScope.launch {
			onResult(repository.getUserByUsername(username))
		}
	}
}


