package com.homeworks.midexam.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.homeworks.midexam.models.Status
import com.homeworks.midexam.models.User
import com.homeworks.midexam.database.dao.StatusDao
import com.homeworks.midexam.database.dao.UserDao

@Database(
	entities = [User::class, Status::class],
	version = 2, // version 1 ocurring crash when trying to login
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
	abstract fun statusDao(): StatusDao

	companion object {
		@Volatile
		private var INSTANCE: AppDatabase? = null

		fun getInstance(context: Context): AppDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"LoginSystem.db"
				)
					.fallbackToDestructiveMigration()
					.build()
				INSTANCE = instance
				instance
			}
		}
	}
}


