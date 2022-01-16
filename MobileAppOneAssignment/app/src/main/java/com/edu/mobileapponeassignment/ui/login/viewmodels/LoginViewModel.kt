package com.edu.mobileapponeassignment.ui.login.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.edu.mobileapponeassignment.data.remote.dto.login.User
import com.edu.mobileapponeassignment.data.remote.dto.login.UserLogin
import com.edu.mobileapponeassignment.data.repository.LoginRepository

class LoginViewModel: ViewModel() {
    private val _userLogin: MutableLiveData<UserLogin> = MutableLiveData()

    val loggedInUser: LiveData<User> = Transformations.switchMap(_userLogin) {
        Log.d("Debug", "switchMap called: $it")
        LoginRepository.login(it.username)
    }

    fun setUserLogin(user: UserLogin) {
        if (_userLogin.value == user) { return }
        _userLogin.value = user
        Log.d("Debug", "user is set: $user")
    }

    fun cancelJobs() {
        LoginRepository.cancelJobs()
    }
}