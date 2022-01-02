package com.senla.fitnessapp.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.senla.fitnessapp.common.models.Notification
import io.reactivex.rxjava3.core.Single

class SQLiteHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "notification.db"
        private const val TABLE_NOTIFICATION = "notification"
        private const val ID = "id"
        private const val TITLE = "title"
        private const val TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(("CREATE TABLE " + TABLE_NOTIFICATION + "(" + ID
                + " INTEGER PRIMARY KEY," + TITLE + " TEXT," + TIME + " TEXT" + ")"))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATION")
        onCreate(db)
    }

    fun insertNotification(notification: Notification): Single<Long> {
        val database = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, notification.id)
        contentValues.put(TITLE, notification.title)
        contentValues.put(TIME, notification.time)

        val success = database.insert(TABLE_NOTIFICATION, null, contentValues)
        database.close()

        return Single.just(success)
    }

    fun getAllNotifications(): Single<ArrayList<Notification>> {
        val notificationList: ArrayList<Notification> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_NOTIFICATION "
        val database = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = database.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            database.execSQL(selectQuery)
            e.printStackTrace()

            return Single.just(notificationList)
        }

        var id: Int
        var title: String
        var time: String

        if(cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID))
                title = cursor.getString(cursor.getColumnIndex(TITLE))
                time = cursor.getString(cursor.getColumnIndex(TIME))

                val notification = Notification(id = id, title = title, time = time)
                notificationList.add(notification)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return Single.just(notificationList)
    }

    fun getNotificationById(id: Int): Single<Notification>? {
        val database = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NOTIFICATION WHERE id = $id"

        database.rawQuery(selectQuery, null).use {
            if (it.moveToFirst()) {
                val notification = Notification()
                notification.id = it.getInt(it.getColumnIndex(ID))
                notification.title = it.getString(it.getColumnIndex(TITLE))
                notification.time = it.getString(it.getColumnIndex(TIME))

                return Single.just(notification)
            }
        }
        return null
    }

    fun updateNotification(notification: Notification): Single<Int> {
        val database = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, notification.id)
        contentValues.put(TITLE, notification.title)
        contentValues.put(TIME, notification.time)

        val success = database.update(TABLE_NOTIFICATION, contentValues,
            "id=" + notification.id, null)

        database.close()

        return Single.just(success)
    }

    fun deleteNotificationById(id: Int): Single<Int> {
        val database = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = database.delete(TABLE_NOTIFICATION, "id=$id", null)

        database.close()

        return Single.just(success)
    }
}