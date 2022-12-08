package com.comet.floatingtwitter.model

data class Settings(val token : String?, val refresh : String?, val size : Int, val mention : Int, val dm : Int, val twin : Int) : java.io.Serializable