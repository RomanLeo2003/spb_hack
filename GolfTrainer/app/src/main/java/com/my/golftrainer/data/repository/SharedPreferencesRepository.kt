package com.my.golftrainer.data.repository

interface SharedPreferencesRepository {

    fun addNewRecordUri(uri: String)
    fun getAllRecordUris(): Set<String>
}