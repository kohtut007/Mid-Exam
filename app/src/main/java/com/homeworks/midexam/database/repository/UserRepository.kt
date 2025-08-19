package com.homeworks.midexam.database.repository

import android.content.Context
import com.homeworks.midexam.database.AppDatabase
import com.homeworks.midexam.models.User

class UserRepository(context: Context) {
	private val userDao = AppDatabase.getInstance(context).userDao()

	suspend fun addUser(username: String, password: String): Long {
		return userDao.insert(User(username = username, password = password))
	}

	suspend fun checkUserExists(username: String): Boolean = userDao.exists(username)

	suspend fun validateUser(username: String, password: String): User? = userDao.validate(username, password)

	suspend fun getUserById(userId: Int): User? = userDao.getById(userId)

	suspend fun getUserByUsername(username: String): User? = userDao.getByUsername(username)
}


