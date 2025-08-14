package com.homeworks.midexam.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.homeworks.midexam.models.Status
import com.homeworks.midexam.models.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LoginSystem.db"
        private const val DATABASE_VERSION = 1

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        // Status table
        private const val TABLE_STATUSES = "statuses"
        private const val COLUMN_STATUS_ID = "id"
        private const val COLUMN_USER_ID_FK = "user_id"
        private const val COLUMN_STATUS_TEXT = "status_text"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create users table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        // Create statuses table
        val createStatusesTable = """
            CREATE TABLE $TABLE_STATUSES (
                $COLUMN_STATUS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID_FK INTEGER NOT NULL,
                $COLUMN_STATUS_TEXT TEXT NOT NULL,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createStatusesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STATUSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // User operations
    fun addUser(username: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun checkUserExists(username: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun validateUser(username: String, password: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun getUserById(userId: Int): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD),
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // Status operations
    fun addStatus(userId: Int, statusText: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID_FK, userId)
            put(COLUMN_STATUS_TEXT, statusText)
        }
        return db.insert(TABLE_STATUSES, null, values)
    }

    fun getStatusByUserId(userId: Int): Status? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_STATUSES,
            arrayOf(COLUMN_STATUS_ID, COLUMN_USER_ID_FK, COLUMN_STATUS_TEXT, COLUMN_CREATED_AT),
            "$COLUMN_USER_ID_FK = ?",
            arrayOf(userId.toString()),
            null, null, "$COLUMN_CREATED_AT DESC", "1"
        )

        return if (cursor.moveToFirst()) {
            val status = Status(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)),
                statusText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS_TEXT)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            )
            cursor.close()
            status
        } else {
            cursor.close()
            null
        }
    }

    fun getStatusesByUserId(userId: Int): List<Status> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_STATUSES,
            arrayOf(COLUMN_STATUS_ID, COLUMN_USER_ID_FK, COLUMN_STATUS_TEXT, COLUMN_CREATED_AT),
            "$COLUMN_USER_ID_FK = ?",
            arrayOf(userId.toString()),
            null, null, "$COLUMN_CREATED_AT DESC"
        )

        val statuses = mutableListOf<Status>()
        if (cursor.moveToFirst()) {
            do {
                val status = Status(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS_ID)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)),
                    statusText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS_TEXT)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                )
                statuses.add(status)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return statuses
    }

    fun updateStatus(statusId: Int, statusText: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS_TEXT, statusText)
        }
        return db.update(TABLE_STATUSES, values, "$COLUMN_STATUS_ID = ?", arrayOf(statusId.toString()))
    }

    fun deleteStatus(statusId: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_STATUSES, "$COLUMN_STATUS_ID = ?", arrayOf(statusId.toString()))
    }
    
    fun getUserByUsername(username: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }
}
