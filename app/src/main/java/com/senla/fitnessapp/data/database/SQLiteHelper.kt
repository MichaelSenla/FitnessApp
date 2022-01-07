package com.senla.fitnessapp.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import io.reactivex.rxjava3.core.Single
import java.lang.Exception

class SQLiteHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "notification.db"
        private const val TABLE_NOTIFICATION = "notification"
        private const val TABLE_TRACK = "track"
        private const val ID = "id"
        private const val TITLE = "title"
        private const val TIME = "time"
        private const val DISTANCE = "DISTANCE"
        private const val START_TIME = "START_TIME"
        private const val JOGGING_TIME = "JOGGING_TIME"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(("CREATE TABLE " + TABLE_NOTIFICATION + "(" + ID
                + " INTEGER PRIMARY KEY," + TITLE + " TEXT," + TIME + " TEXT" + ")"))
        db?.execSQL(("CREATE TABLE " + TABLE_TRACK + "(" + ID
                + " INTEGER PRIMARY KEY," + START_TIME + " TEXT," + DISTANCE + " TEXT," +
                JOGGING_TIME + " TEXT" + ")"))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATION")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRACK")
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
        val selectQuery = "SELECT * FROM $TABLE_NOTIFICATION"
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
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                time = cursor.getString(cursor.getColumnIndexOrThrow(TIME))

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
                notification.id = it.getInt(it.getColumnIndexOrThrow(ID))
                notification.title = it.getString(it.getColumnIndexOrThrow(TITLE))
                notification.time = it.getString(it.getColumnIndexOrThrow(TIME))

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

    fun insertTrack(dataBaseTrack: DataBaseTrack): Single<Long> {
        val database = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, dataBaseTrack.id)
        contentValues.put(START_TIME, dataBaseTrack.startTime)
        contentValues.put(DISTANCE, dataBaseTrack.distance)
        contentValues.put(JOGGING_TIME, dataBaseTrack.joggingTime)

        val success = database.insert(TABLE_TRACK, null, contentValues)
        database.close()

        return Single.just(success)
    }

    fun getTrackById(id: Int): Single<DataBaseTrack>? {
        val database = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_TRACK WHERE id = $id"

        database.rawQuery(selectQuery, null).use {
            if (it.moveToFirst()) {
                val track = DataBaseTrack()
                track.startTime = it.getString(it.getColumnIndexOrThrow(START_TIME))
                track.distance = it.getString(it.getColumnIndexOrThrow(DISTANCE))
                track.joggingTime = it.getString(it.getColumnIndexOrThrow(JOGGING_TIME))

                return Single.just(track)
            }
        }
        return null
    }

    fun getAllTracks(): Single<ArrayList<DataBaseTrack>> {
        val dataBaseTrackList: ArrayList<DataBaseTrack> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_TRACK"
        val database = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = database.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            database.execSQL(selectQuery)
            e.printStackTrace()

            return Single.just(dataBaseTrackList)
        }

        var id: Int
        var startTime: String
        var distance: String
        var joggingTime: String

        if(cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                startTime = cursor.getString(cursor.getColumnIndexOrThrow(START_TIME))
                distance = cursor.getString(cursor.getColumnIndexOrThrow(DISTANCE))
                joggingTime = cursor.getString(cursor.getColumnIndexOrThrow(JOGGING_TIME))


                val track = DataBaseTrack(id = id, startTime = startTime, distance = distance,
                    joggingTime = joggingTime)
                dataBaseTrackList.add(track)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return Single.just(dataBaseTrackList)
    }
}