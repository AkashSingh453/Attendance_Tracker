package com.example.attendance_tracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.attendance_tracker.model.Tracker

@Database(entities = [Tracker::class],version = 1)
abstract class TrackerDatabase : RoomDatabase(){
    abstract fun trackerDao() : TrackerDao
}