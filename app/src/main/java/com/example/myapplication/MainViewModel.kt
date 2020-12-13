package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.AppList

class MainViewModel: ViewModel() {

    var AllData=MutableLiveData<List<AppList>>()

    val searchContent=MutableLiveData<String>()

    init {
        searchContent.value=""
    }
}