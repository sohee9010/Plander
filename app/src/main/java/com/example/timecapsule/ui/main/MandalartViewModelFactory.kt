package com.example.timecapsule.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MandalartViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MandalartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MandalartViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
