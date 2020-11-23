package com.prototype.gbcontacttracing.databaseManager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

const val DATABASE_NAME = "TracedContacts"
const val TABLE_NAME = "Contacts"
const val COL_id = "id"
const val COL_startTime = "startTime"
const val COL_endTime = "endTime"
const val COL_avgDistance = "avgDistance"


class DataBaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
    }

    fun setupTable() {
        val dbWrite = this.writableDatabase
        val createTable = "CREATE TABLE  IF NOT EXISTS $TABLE_NAME (" +
                "$COL_id VARCHAR(256) NOT NULL," +
                "$COL_startTime VARCHAR(256) NOT NULL," +
                "$COL_endTime VARCHAR(256) NOT NULL," +
                "$COL_avgDistance VARCHAR(256) NOT NULL," +
                "PRIMARY KEY ( $COL_id, $COL_startTime )" +
                ")"
        Log.i("tableCreation", dbWrite?.execSQL(createTable).toString())
    }

    fun insertData(id: String, startTime: Long, endTime: Long, avgDistance: Double) {
        val dbRead = this.readableDatabase
        val dbWrite = this.writableDatabase

        val existQuery =
            "SELECT $COL_id FROM $TABLE_NAME WHERE $COL_id = '$id' AND $COL_startTime = '$startTime'"
        val existQueryRes = dbRead.rawQuery(existQuery, null)

        if (existQueryRes.count > 0) {
            //update
            val contentValues = ContentValues()
            contentValues.put(COL_endTime, endTime)
            contentValues.put(COL_avgDistance, avgDistance)

            val condition = "$COL_id = ? AND $COL_startTime = ?"
            val whereArgs = arrayOf<String>(id, startTime.toString())

            Log.i("UPDATING", "$id $startTime $endTime $avgDistance")
            dbWrite.update(TABLE_NAME, contentValues, condition, whereArgs)
        } else {
            Log.i("INSERTING", "$id $startTime $endTime $avgDistance")
            val contentValues = ContentValues()
            contentValues.put(COL_id, id)
            contentValues.put(COL_startTime, startTime)
            contentValues.put(COL_endTime, endTime)
            contentValues.put(COL_avgDistance, avgDistance)
            dbWrite.insert(TABLE_NAME, null, contentValues)
        }

        val inspectQuery = "SELECT * FROM Contacts"
        val queryResult = dbRead.rawQuery(inspectQuery, null)
        if (queryResult.moveToFirst()) {
            do {
                Log.i(
                    "ROW - >",
                    queryResult.getString(0) + ", " + queryResult.getString(1) + ", " + queryResult.getString(
                        2
                    ) + ", " + queryResult.getString(3)
                )
            } while (queryResult.moveToNext())
        }
        Log.i("-------------------", "---------------------------")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //do nothing
    }

}
