package com.edu.mobileapponeassignment.ui.main.viewmodels

import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val _imageBitmap: MutableLiveData<BitmapDrawable> = MutableLiveData()
    val imageBitmap: LiveData<BitmapDrawable> = _imageBitmap

    fun setImageBitmap(bitmap: BitmapDrawable) {
        if (_imageBitmap.value == bitmap) { return }
        _imageBitmap.value = bitmap
    }
}