package com.example.attendance_tracker.utils

import androidx.room.TypeConverter
import java.util.UUID


    @TypeConverter
    fun uuidFromString(string: String?): UUID {
        return UUID.fromString(string)
    }
