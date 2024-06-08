package com.example.attendance_tracker.repository

import com.example.attendance_tracker.data.TrackerDao
import com.example.attendance_tracker.model.Tracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TrackerRepository @Inject constructor(private val trackerDao: TrackerDao) {

    suspend fun addTrack(tracker: Tracker) = trackerDao.insert(tracker)
    suspend fun updateTrack(tracker: Tracker)=trackerDao.update(tracker)
    suspend fun deleteTrack(tracker: Tracker)=trackerDao.deleteNote(tracker)
    suspend fun deleteAllTrack()=trackerDao.deleteAll()
    suspend fun getAllTrack(): Flow<List<Tracker>> = trackerDao.getTrack().flowOn(Dispatchers.IO)
        .conflate()
}