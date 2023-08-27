package com.my.golftrainer.presentation.screen.history

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.golftrainer.data.repository.SharedPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    sp: SharedPreferencesRepository
) : ViewModel() {

    var recordUris by mutableStateOf(sp.getAllRecordUris().toList())
    var images by mutableStateOf(emptyList<VideoDetails>())

    data class VideoDetails(
        val uri: String,
        val thumbnail: Bitmap?,
        val date: String?,
        val duration: Long?
    )

    private fun getVideoDetails(context: Context, videoUri: String): VideoDetails {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, Uri.parse(videoUri))
            val thumbnail =
                retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            val date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val duration = durationStr?.toLongOrNull()
            VideoDetails(videoUri, thumbnail, date, duration)
        } catch (e: Exception) {
            VideoDetails("", null, null, null)
        } finally {
            retriever.release()
        }
    }

    fun getThumbnailsFromUris(context: Context, uris: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            images = uris.map { uri ->
                async(Dispatchers.IO) {
                    getVideoDetails(context, uri)
                }
            }.map { it.await() }
        }
    }


}