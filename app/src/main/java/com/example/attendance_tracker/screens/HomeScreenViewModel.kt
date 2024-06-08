package com.example.attendance_tracker.screens

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance_tracker.data.DataOrException
import com.example.attendance_tracker.model.MTracker
import com.example.attendance_tracker.model.M_User
import com.example.attendance_tracker.repository.FireRepository
import com.example.attendance_tracker.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository : FireRepository) : ViewModel() {
    val data : MutableState<DataOrException<List<MTracker>, Boolean, Exception>>
            = mutableStateOf(DataOrException(listOf(),true,Exception("")))

    init {
        getAllTracksFromDatabase()
    }

    private fun getAllTracksFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllTracksFromDatabase()
            if (!data.value.data.isNullOrEmpty()) data.value.loading = false
        }
        Log.d("GET","getalolbooks : ${data.value.data?.toList().toString()}")
        Log.d("GETLoading","getalolbooks : ${data.value.loading.toString()}")
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(private val  userRepository: UserRepository) : ViewModel() {

    val user : MutableState<DataOrException<List<M_User>, Boolean, Exception>>
            = mutableStateOf(DataOrException(listOf(),true,Exception("")))

    init {
        getAllUsersFromDatabase()
    }

    private fun getAllUsersFromDatabase() {
        viewModelScope.launch {
            user.value.loading = true
            user.value = userRepository.getAllUsersFromDatabase()
            if (!user.value.data.isNullOrEmpty()) user.value.loading = false
        }
        Log.d("GET","getalolbooks : ${user.value.data?.toList().toString()}")
        Log.d("GETLoading","getalolbooks : ${user.value.loading.toString()}")
    }
}
