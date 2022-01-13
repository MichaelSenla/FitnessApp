package com.senla.fitnessapp.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.senla.fitnessapp.data.database.models.DataBaseSavedTrack
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
        private const val TABLE_UNSENT_TRACK = "unsent_track"
        private const val ID = "id"
        private const val TITLE = "Title"
        private const val TIME = "Time"
        private const val DISTANCE = "Distance"
        private const val START_TIME = "Start_time"
        private const val JOGGING_TIME = "Jogging_time"
        private const val IS_TRACK_ON_SERVER = "Is_track_on_server"
        private const val START_LONGITUDE = "Start_longitude"
        private const val START_LATITUDE = "Start_latitude"
        private const val FINISH_LONGITUDE = "Finish_longitude"
        private const val FINISH_LATITUDE = "Finish_latitude"
        private const val IS_TRACK_ON_THE_SERVER_DEFAULT_VALUE = "false"
        private const val IS_TRACK_ON_THE_SERVER_TRUE_VALUE = "true"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(("CREATE TABLE " + TABLE_NOTIFICATION + "(" + ID
                + " INTEGER PRIMARY KEY," + TITLE + " TEXT," + TIME + " TEXT" + ")"))
        db?.execSQL(("CREATE TABLE " + TABLE_TRACK + "(" + ID
                + " INTEGER PRIMARY KEY," + START_TIME + " INTEGER UNIQUE," + DISTANCE + " TEXT," +
                JOGGING_TIME + " INTEGER," + START_LONGITUDE + " REAL," +
                START_LATITUDE + " REAL, " + FINISH_LONGITUDE + " REAL," +
                FINISH_LATITUDE + " REAL" + ")"))
        db?.execSQL(("CREATE TABLE " + TABLE_UNSENT_TRACK + "(" + ID
                + " INTEGER PRIMARY KEY," + START_TIME + " INTEGER UNIQUE," + DISTANCE + " TEXT," +
                JOGGING_TIME + " INTEGER," + IS_TRACK_ON_SERVER + " TEXT," +
                START_LONGITUDE + " REAL," + START_LATITUDE + " REAL," +
                FINISH_LONGITUDE + " REAL," + FINISH_LATITUDE + " REAL" + ")"))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATION")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRACK")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_UNSENT_TRACK")
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
        contentValues.put(START_LONGITUDE, dataBaseTrack.startLongitude)
        contentValues.put(START_LATITUDE, dataBaseTrack.startLatitude)
        contentValues.put(FINISH_LONGITUDE, dataBaseTrack.finishLongitude)
        contentValues.put(FINISH_LATITUDE, dataBaseTrack.finishLatitude)

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
                track.startTime = it.getLong(it.getColumnIndexOrThrow(START_TIME))
                track.distance = it.getString(it.getColumnIndexOrThrow(DISTANCE))
                track.joggingTime = it.getLong(it.getColumnIndexOrThrow(JOGGING_TIME))
                track.startLongitude = it.getDouble(it.getColumnIndexOrThrow(START_LONGITUDE))
                track.startLatitude = it.getDouble(it.getColumnIndexOrThrow(START_LATITUDE))
                track.finishLongitude = it.getDouble(it.getColumnIndexOrThrow(FINISH_LONGITUDE))
                track.finishLatitude = it.getDouble(it.getColumnIndexOrThrow(FINISH_LATITUDE))

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
        var startTime: Long
        var distance: String
        var joggingTime: Long
        var startLongitude = 37.6377
        var startLatitude = 55.7305
        var finishLongitude = 37.6377
        var finishLatitude = 55.7305

        if(cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                startTime = cursor.getLong(cursor.getColumnIndexOrThrow(START_TIME))
                distance = cursor.getString(cursor.getColumnIndexOrThrow(DISTANCE))
                joggingTime = cursor.getLong(cursor.getColumnIndexOrThrow(JOGGING_TIME))
                startLongitude = cursor.getDouble(cursor.getColumnIndexOrThrow(START_LONGITUDE))
                startLatitude = cursor.getDouble(cursor.getColumnIndexOrThrow(START_LATITUDE))
                finishLongitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FINISH_LONGITUDE))
                finishLatitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FINISH_LATITUDE))

                val track = DataBaseTrack(id = id, startTime = startTime, distance = distance,
                    joggingTime = joggingTime, startLongitude = startLongitude,
                    startLatitude = startLatitude, finishLongitude = finishLongitude,
                    finishLatitude = finishLatitude)

                dataBaseTrackList.add(track)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return Single.just(dataBaseTrackList)
    }

    fun insertSavedTrack(dataBaseSavedTrack: DataBaseSavedTrack): Single<Long> {
        val database = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, dataBaseSavedTrack.id)
        contentValues.put(START_TIME, dataBaseSavedTrack.startTime)
        contentValues.put(DISTANCE, dataBaseSavedTrack.distance)
        contentValues.put(JOGGING_TIME, dataBaseSavedTrack.joggingTime)
        contentValues.put(IS_TRACK_ON_SERVER, IS_TRACK_ON_THE_SERVER_DEFAULT_VALUE)
        contentValues.put(START_LONGITUDE, dataBaseSavedTrack.startLongitude)
        contentValues.put(START_LATITUDE, dataBaseSavedTrack.startLatitude)
        contentValues.put(FINISH_LONGITUDE, dataBaseSavedTrack.finishLongitude)
        contentValues.put(FINISH_LATITUDE, dataBaseSavedTrack.finishLatitude)

        val success = database.insert(TABLE_UNSENT_TRACK, null, contentValues)
        database.close()

        return Single.just(success)
    }

    fun getAllSavedTracks(): Single<List<DataBaseSavedTrack>> {
        val dataBaseSavedTrackList: ArrayList<DataBaseSavedTrack> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_UNSENT_TRACK"
        val database = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = database.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            database.execSQL(selectQuery)
            e.printStackTrace()

            return Single.just(dataBaseSavedTrackList)
        }

        var id: Int
        var startTime: Long
        var distance: String
        var joggingTime: Long
        var isTrackOnServer: String
        var startLongitude: Double
        var startLatitude: Double
        var finishLongitude: Double
        var finishLatitude: Double

        if(cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                startTime = cursor.getLong(cursor.getColumnIndexOrThrow(START_TIME))
                distance = cursor.getString(cursor.getColumnIndexOrThrow(DISTANCE))
                joggingTime = cursor.getLong(cursor.getColumnIndexOrThrow(JOGGING_TIME))
                isTrackOnServer = cursor.getString(cursor.getColumnIndexOrThrow(IS_TRACK_ON_SERVER))
                startLongitude = cursor.getDouble(cursor.getColumnIndexOrThrow(START_LONGITUDE))
                startLatitude = cursor.getDouble(cursor.getColumnIndexOrThrow(START_LATITUDE))
                finishLongitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FINISH_LONGITUDE))
                finishLatitude = cursor.getDouble(cursor.getColumnIndexOrThrow(FINISH_LATITUDE))

                val track = DataBaseSavedTrack(id = id, startTime = startTime, distance = distance,
                    joggingTime = joggingTime, isTrackOnServer = isTrackOnServer,
                    startLongitude = startLongitude, startLatitude = startLatitude,
                    finishLongitude = finishLongitude, finishLatitude = finishLatitude)

                dataBaseSavedTrackList.add(track)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return Single.just(dataBaseSavedTrackList)
    }

    fun changeIsTrackOnServerToTrue(savedTrack: DataBaseSavedTrack): Single<Int> {
        val database = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(IS_TRACK_ON_SERVER, IS_TRACK_ON_THE_SERVER_TRUE_VALUE)

        val success = database.update(
            TABLE_UNSENT_TRACK, contentValues, "id=" + savedTrack.id, null)

        database.close()

        return Single.just(success)
    }
}