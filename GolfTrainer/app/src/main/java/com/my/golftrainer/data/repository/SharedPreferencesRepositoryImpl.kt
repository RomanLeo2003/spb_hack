package com.my.golftrainer.data.repository

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesRepositoryImpl @Inject constructor(private val sharedPref: SharedPreferences) :
    SharedPreferencesRepository {

    override fun addNewRecordUri(uri: String) {
        val allRecords = getAllRecordUris()
        with(sharedPref.edit()) {
            putStringSet(RECORD_URI, allRecords + uri)
            apply()
        }
    }

    override fun getAllRecordUris() = sharedPref.getStringSet(RECORD_URI, setOf()) ?: setOf()

    private val RECORD_URI = "record_uri"
}