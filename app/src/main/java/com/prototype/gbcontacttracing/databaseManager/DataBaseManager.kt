package com.prototype.gbcontacttracing.databaseManager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

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


    fun deleteOldData(currentTime: Long, maxTimeDiff: Long){
        val dbWrite = this.writableDatabase
        val condition = "? - $COL_endTime >= $maxTimeDiff"
        val deleted = dbWrite.delete(TABLE_NAME, condition, arrayOf(currentTime.toString()))
        //Log.i("DELETED OLD DATA", deleted.toString())
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
                    queryResult.getString(0) + ", " +
                            queryResult.getString(1) + ", " +
                            queryResult.getString(2) + ", " +
                            queryResult.getString(3)
                )
            } while (queryResult.moveToNext())
        }
        Log.i("-------------------", "---------------------------")
    }

    fun readAllData(): String {
        val dbRead = this.readableDatabase
        val inspectQuery = "SELECT * FROM Contacts"
        val result =  StringBuilder("<table>")
        val queryResult = dbRead.rawQuery(inspectQuery, null)
        result.append("<tr>")
        result.append("<th>$COL_id</th>")
        result.append("<th>$COL_startTime</th>")
        result.append("<th>$COL_endTime</th>")
        result.append("<th>Duration(min)</th>")
        result.append("<th>$COL_avgDistance</th>")

        result.append("</tr>")
        val dateFormat = "dd/MM/yyyy hh:mm:ss.SSS"
        if (queryResult.moveToFirst()) {
            do {
                result.append("<tr>")
                result.append("<td>${queryResult.getString(0)}</td>")
                result.append("<td>${getDate(queryResult.getString(1), dateFormat)}</td>")
                result.append("<td>${getDate(queryResult.getString(2), dateFormat)}</td>")

                var duration = queryResult.getString(2).toLong() - queryResult.getString(1).toLong() //in milliseconds
                duration /= (60 * 1000) //convert to minutes
                result.append("<td>${duration}</td>")
                result.append("<td>${queryResult.getString(3)}</td>")
                result.append("</tr>")

            } while (queryResult.moveToNext())
        }
        result.append("</table>")
        return result.toString()

    }

    private fun getDate(milliSeconds: String, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter: DateFormat = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds.toLong()
        return formatter.format(calendar.time)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //do nothing
    }

}
