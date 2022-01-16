package com.edu.mobileapponeassignment.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.edu.mobileapponeassignment.data.remote.ServiceBuilder
import com.edu.mobileapponeassignment.data.remote.dto.login.User
import com.edu.mobileapponeassignment.data.remote.dto.login.toRealUser
import kotlinx.coroutines.*

object LoginRepository {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d("Debug", "Handle $exception in CoroutineExceptionHandler")
    }

    private val jobMap: MutableMap<String, CompletableJob?> = mutableMapOf()

    fun login(firstname: String): LiveData<User> {
        val job = Job()
        jobMap["login"] = job
        return object : LiveData<User>() {
            override fun onActive() {
                super.onActive()
                job.let { theJob ->
                    CoroutineScope(Dispatchers.IO + coroutineExceptionHandler + theJob).launch {
                        val user = ServiceBuilder.apiService.auth().toRealUser(firstname)

                        withContext(Dispatchers.Main) {
                            Log.d("Debug", "user from repository: $user")
                            value = user
                            theJob.complete()
                        }
                    }
                }
            }
        }
    }

    fun cancelJobs() {
        for (job in jobMap.values) {
            job?.cancel()
        }
    }
}