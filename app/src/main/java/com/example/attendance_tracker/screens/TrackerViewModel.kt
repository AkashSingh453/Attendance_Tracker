package com.example.attendance_tracker.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance_tracker.model.Tracker
import com.example.attendance_tracker.repository.TrackerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(private val repository: TrackerRepository) : ViewModel() {
    private val _TrackList = MutableStateFlow<List<Tracker>>(emptyList())
    val TrackList = _TrackList.asStateFlow()
    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllTrack().distinctUntilChanged()
                .collect{listOfTrack ->
                    if (listOfTrack.isEmpty()){
                        Log.d("mkcd",":Empty List")
                    }else{
                        _TrackList.value = listOfTrack
                    }

                }
        }
    }
    fun addTrack(tracker: Tracker) = viewModelScope.launch { repository.addTrack(tracker) }
    fun updateTrack(tracker: Tracker) = viewModelScope.launch { repository.updateTrack(tracker) }
    fun removeTracker(tracker: Tracker) = viewModelScope.launch { repository.deleteTrack(tracker) }
    fun removeAllTrack() = viewModelScope.launch { repository.deleteAllTrack() }
}