package com.example.attendance_tracker.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendance_tracker.Components.EmailInput
import com.example.attendance_tracker.Components.PasswordInput
import com.example.attendance_tracker.Components.TrackerLogo
import com.example.attendance_tracker.R
import com.example.attendance_tracker.model.MTracker
import com.example.attendance_tracker.model.M_User
import com.example.attendance_tracker.navigation.TrackerScreens
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    trackViewModel: TrackViewModel = hiltViewModel()
) {
    var listOfUsers = emptyList<M_User>()

    if (!userViewModel.user.value.data.isNullOrEmpty()){
        listOfUsers = userViewModel.user.value.data!!.toList()
        Log.d("Books" , "HomeContent : ${listOfUsers.toString()}")
    }
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }

    val error = viewModel.error.observeAsState()

    val success = viewModel.success.observeAsState()
    val _email = remember {
        mutableStateOf("")
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            TrackerLogo()
            if (showLoginForm.value){
                UserForm(
                    loading = false,
                    isCreateAccount = false,
                    error = error.value
                    ) { email,password ->
                        _email.value = email
                        viewModel.SigninWithEmailandpassword(email,password){
                        navController.navigate(TrackerScreens.BackupScreen.name)
                        }

                }
            }
            else{
                UserForm(
                    loading = false,
                    isCreateAccount = true,
                    error = error.value
                    ){email,password ->
                    viewModel.createUserWithEmailandpassword(email,password){
                        navController.navigate(TrackerScreens.HomeScreen.name){
                            popUpTo(TrackerScreens.LoginScreen.name) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            ForgotPassword(email = _email.value , listOfUsers = listOfUsers , error = error.value , valid = !showLoginForm.value ){
                viewModel.resetPassword(_email.value)
            }

            Row (
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                val text = if (showLoginForm.value) "Sign Up" else "Login"
                val text1 = if (showLoginForm.value) "New User?" else "Already have An Account?"
                Text(text = text1)
                Text(text,
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF56CEC7)
                )
            }
        }


    }

}

@Composable
fun ForgotPassword(
    email: String,
    listOfUsers: List<M_User>,
    error: String?,
    valid :Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var forgot:Boolean = false
  listOfUsers.forEach { user ->
      if (email == user.displayname) {
          forgot = true
      }
  }
    if (!valid) {
        if (!error.isNullOrEmpty()) {
            if (forgot) {
                Text(
                    text = "Reset password!!",
                    fontSize = 15.sp,
                    color = Color(0xFF56CEC7),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onClick.invoke()
                        Toast.makeText(context, "Reset password link has been sent to your email.", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Text(text = "Email Doesn't Exist.  Please SignUp First",
                    fontSize = 13.sp,
                    color = Color(0xFFF54F40),
                    )
            }
        }
    }
}


@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    error: String? =  "",
    on_Done: (String, String) -> Unit = { email, pwd -> },

    ){
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordvisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value , password.value) {
        email.value.trim().isNotEmpty() &&  password.value.trim().isNotEmpty()
    }

    val modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isCreateAccount) {
            Text(
                text = "Please Login",
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.Start)
                    .fillMaxWidth(),
                color = Color(0xFF6F6A6A)
            )
        } else {
            Text(text = "Please SignUp",
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.Start)
                    .fillMaxWidth(),
                color = Color(0xFF6F6A6A)
            )
        }
        if (isCreateAccount) {
            Text(
                text = stringResource(id = R.string.create_acct),
                modifier = Modifier.padding(4.dp), color = Color(0xFF6F6A6A),
                fontSize = 10.sp
            )
        } else {
            Text(text = "",
                modifier = Modifier.padding(4.dp), color = Color(0xFF6F6A6A),
                fontSize = 10.sp
            )
        }
        EmailInput(
            emailState = email,
            enabled = !loading,
            onAction = KeyboardActions{
                passwordFocusRequest.requestFocus()
            }
        )
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = !loading,
            passwordvisibility = passwordvisibility,
            onAction = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    if (!valid) return@KeyboardActions
                    on_Done( email.value.trim() , password.value.trim() )
                }

            )
        )
        if (!error.isNullOrEmpty()) {
            Text(
                text = error.split(":")[1],
                fontSize = 10.sp,
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                color = Color.Red,
                lineHeight = 15.sp
            )
        }
        SubmitButton(
            textId = if (isCreateAccount) "CreateAccount" else "Login",
            loading = loading,
            validInputs = valid
        ) {
            on_Done(email.value.trim(),password.value.trim())
            keyboardController?.hide()
        }
    }

}

@Composable
fun SubmitButton(
    textId: String,
    loading: Boolean,
    validInputs: Boolean,
    onClick:() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        enabled = !loading  && validInputs,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF56CEC7),
            contentColor = Color.White )
    ) {
        if (loading) CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else Text(
            text = textId,
            modifier = Modifier.padding(5.dp)
        )
    }
}