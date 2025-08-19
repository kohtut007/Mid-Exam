package com.homeworks.midexam.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.homeworks.midexam.models.Status

@Dao
interface StatusDao {
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(status: Status): Long

	@Query("SELECT * FROM statuses WHERE user_id = :userId ORDER BY created_at DESC")
	suspend fun getByUserId(userId: Int): List<Status>

	@Query("UPDATE statuses SET status_text = :statusText WHERE id = :statusId")
	suspend fun updateText(statusId: Int, statusText: String): Int

	@Query("DELETE FROM statuses WHERE id = :statusId")
	suspend fun deleteById(statusId: Int): Int
}


