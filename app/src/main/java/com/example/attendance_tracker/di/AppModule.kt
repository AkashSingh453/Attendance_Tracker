package com.example.attendance_tracker.di

import android.content.Context
import androidx.room.Room
import com.example.attendance_tracker.data.TrackerDao
import com.example.attendance_tracker.data.TrackerDatabase
import com.example.attendance_tracker.repository.FireRepository
import com.example.attendance_tracker.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFireBookRepository()
            = FireRepository(queryTrack = FirebaseFirestore.getInstance()
        .collection("tracks"))

    @Singleton
    @Provides
    fun provideFireUserRepository()
            = UserRepository(queryTrack = FirebaseFirestore.getInstance()
        .collection("users"))

    @Singleton
    @Provides
    fun provideTrackerDao(trackerDatabase: TrackerDatabase):TrackerDao
            = trackerDatabase.trackerDao()


    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context):TrackerDatabase
            = Room.databaseBuilder(
        context,
        TrackerDatabase::class.java,
        "tracker_db")
        .fallbackToDestructiveMigration()
        .build()


}