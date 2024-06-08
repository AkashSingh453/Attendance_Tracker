package com.example.attendance_tracker.repository

import android.util.Log
import com.example.attendance_tracker.data.DataOrException
import com.example.attendance_tracker.model.MTracker
import com.example.attendance_tracker.model.M_User
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FireRepository @Inject constructor(private val queryTrack : Query) {

    suspend fun getAllTracksFromDatabase() : DataOrException<List<MTracker>, Boolean, Exception> {
        val dataOrException = DataOrException<List<MTracker>,Boolean,Exception>()

        try {
            dataOrException.loading = true
            dataOrException.data = queryTrack.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MTracker::class.java)!!
            }
            if (!dataOrException.data.isNullOrEmpty())  dataOrException.loading = false

        }catch (exception : FirebaseFirestoreException){
            dataOrException.e = exception
        }
        return dataOrException
    }
}


class UserRepository @Inject constructor(private val queryTrack : Query) {

    suspend fun getAllUsersFromDatabase() : DataOrException<List<M_User>, Boolean, Exception> {
        val dataOrException = DataOrException<List<M_User>,Boolean,Exception>()

        try {
            dataOrException.loading = true
            dataOrException.data = queryTrack.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(M_User::class.java)!!
            }
            if (!dataOrException.data.isNullOrEmpty())  dataOrException.loading = false

        }catch (exception : FirebaseFirestoreException){
            dataOrException.e = exception
        }
        return dataOrException
    }
}