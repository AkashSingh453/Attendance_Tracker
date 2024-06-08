package com.example.attendance_tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.attendance_tracker.model.Tracker
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {
    @Query("SELECT * from tracker")
    fun getTrack():
            Flow<List<Tracker>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tracker: Tracker)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(tracker: Tracker)

    @Delete
    suspend fun deleteNote(tracker: Tracker)

    @Query("DELETE from tracker")
    suspend fun deleteAll()
}