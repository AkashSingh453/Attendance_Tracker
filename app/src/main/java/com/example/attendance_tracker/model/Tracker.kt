package com.example.attendance_tracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "tracker")
data class Tracker(
    @PrimaryKey
    val Id : UUID,

    @ColumnInfo(name = "subject")
    val Subject : String,

    @ColumnInfo(name = "number")
    val Number : Int,

)
