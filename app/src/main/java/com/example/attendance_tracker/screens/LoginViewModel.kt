package com.example.attendance_tracker.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.attendance_tracker.model.MTracker
import com.example.attendance_tracker.model.MUser
import com.example.attendance_tracker.navigation.TrackerScreens
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success


    fun SigninWithEmailandpassword(
        email:String,
        password:String,
        home: () -> Unit

    )  = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    _loading.value = false
                    if (task.isSuccessful) {
                        Log.d("FB", "signinwithemailandpassword: Successful ${task.result.toString()}")
                        home()
                    } else {
                        task.exception?.let { exception ->
                            handleFirebaseAuthException(exception)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    _loading.value = false
                    handleFirebaseAuthException(exception)
                }

        }catch (ex : Exception){
            Log.d("FB","signInWithEmailandpassword : ${ex.message}")
            _error.value = "An error occurred: ${ex.message}"
        }

    }

    private fun handleFirebaseAuthException(exception: Exception) {
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                _error.value = "Invalid credentials: ${exception.message}"
            }
            is FirebaseAuthInvalidUserException -> {
                _error.value = "No account found with this email."
            }
            is FirebaseAuthEmailException -> {
                _error.value = "Invalid email address: ${exception.message}"
            }
            else -> {
                _error.value = "Authentication failed: ${exception.message}"
            }
        }
        Log.d("FB", "FirebaseAuthException: ${exception.message}")
    }

    fun createUserWithEmailandpassword(
        email: String,
        password: String,
        home: () -> Unit
    ){
        if (_loading.value == false){
            _loading.value = true
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    _loading.value = false
                    if (task.isSuccessful) {
                        val displayName = task.result.user?.email
                        createUser(displayName)
                        home()
                    } else {
                        task.exception?.let { exception ->
                            handleFirebaseAuthException(exception)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    _loading.value = false
                    handleFirebaseAuthException(exception)
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid

        val user = MUser(
            userId = userId.toString(), displayName = displayName.toString(), id = null
        ).toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        _loading.value = true
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    _success.value = "Password reset email sent."
                } else {
                    task.exception?.let { exception ->
                        handleFirebaseAuthException(exception)
                    }
                }
            }
            .addOnFailureListener { exception ->
                _loading.value = false
                handleFirebaseAuthException(exception)
            }
    }


}