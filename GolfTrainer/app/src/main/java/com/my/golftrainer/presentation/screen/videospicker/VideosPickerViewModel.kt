package com.my.golftrainer.presentation.screen.videospicker

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.golftrainer.data.repository.SharedPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideosPickerViewModel @Inject constructor(
    private val sp: SharedPreferencesRepository
) : ViewModel() {

    var videos by mutableStateOf(listOf<Video>())

    fun saveUri(uri: String) {
        sp.addNewRecordUri(uri)
    }


    fun getVideosFromDevice(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val videos = mutableListOf<Video>()
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DURATION
            )
            val resolver = context.contentResolver

            try {
                resolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val duration = cursor.getLong(durationColumn)
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        // Thumbnail creation is deferred until required for display.
                        videos.add(
                            Video(
                                uri = contentUri,
                                duration = duration,
                                thumbnailProvider = {
                                    MediaStore.Video.Thumbnails.getThumbnail(
                                        resolver,
                                        id,
                                        MediaStore.Video.Thumbnails.MINI_KIND,
                                        null
                                    )
                                }
                            )
                        )
                    }
                }

                this@VideosPickerViewModel.videos = videos.reversed()
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or notify user.
            }
        }
    }


    data class Video(
        val uri: Uri,
        val duration: Long,
        val thumbnailProvider: () -> Bitmap?
    ) {
        // Lazy load the thumbnail only when it's actually accessed.
        val thumbnail by lazy { thumbnailProvider.invoke() }
    }
}