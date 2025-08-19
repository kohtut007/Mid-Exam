package com.homeworks.midexam.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "statuses",
	foreignKeys = [
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["user_id"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index(value = ["user_id"])]
)
data class Status(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int = 0,
	@ColumnInfo(name = "user_id")
	val userId: Int,
	@ColumnInfo(name = "status_text")
	val statusText: String,
	@ColumnInfo(name = "created_at")
	val createdAt: Long = System.currentTimeMillis()
)
