package com.homeworks.midexam.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.homeworks.midexam.models.User

@Dao
interface UserDao {
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(user: User): Long

	@Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
	suspend fun exists(username: String): Boolean

	@Query("SELECT * FROM users WHERE username = :username COLLATE NOCASE  AND password = :password LIMIT 1")
	suspend fun validate(username: String, password: String): User?

	@Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
	suspend fun getById(userId: Int): User?

	@Query("SELECT * FROM users WHERE username = :username LIMIT 1")
	suspend fun getByUsername(username: String): User?
}


