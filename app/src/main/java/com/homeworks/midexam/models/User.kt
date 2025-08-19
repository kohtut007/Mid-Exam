package com.homeworks.midexam.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "users",
	indices = [Index(value = ["username"], unique = true)]
)
data class User(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int = 0,
	@ColumnInfo(name = "username")
	val username: String,
	@ColumnInfo(name = "password")
	val password: String
)
