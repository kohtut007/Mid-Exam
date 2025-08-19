package com.homeworks.midexam.database.repository

import android.content.Context
import com.homeworks.midexam.database.AppDatabase
import com.homeworks.midexam.models.Status

class StatusRepository(context: Context) {
	private val statusDao = AppDatabase.getInstance(context).statusDao()

	suspend fun addStatus(userId: Int, statusText: String): Long {
		return statusDao.insert(Status(userId = userId, statusText = statusText))
	}

	suspend fun getStatusesByUserId(userId: Int): List<Status> = statusDao.getByUserId(userId)

	suspend fun updateStatus(statusId: Int, statusText: String): Int = statusDao.updateText(statusId, statusText)

	suspend fun deleteStatus(statusId: Int): Int = statusDao.deleteById(statusId)
}


