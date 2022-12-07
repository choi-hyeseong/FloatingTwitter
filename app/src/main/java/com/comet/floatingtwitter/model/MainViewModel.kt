package com.comet.floatingtwitter.model

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var settings : Settings? = null;

    fun load() {
        //load logic
        //settings = Settings();
    }
}