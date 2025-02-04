package com.bachnn.messenger.ui.viewModel

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bachnn.messenger.base.BaseViewModel
import com.bachnn.messenger.data.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(@ApplicationContext val context: Context) : BaseViewModel(){

    private var _medias = MutableLiveData<List<Media>>()
    val medias: LiveData<List<Media>> = _medias

    private val mainHandler = Handler(Looper.getMainLooper())


    fun getAllMedia(contentResolver: ContentResolver) {
        val executorService = Executors.newFixedThreadPool(2)
        executorService.execute {
            val allMedia = getImagesMedia(contentResolver) + getVideoMedias(contentResolver)
            val medias = allMedia.sortedByDescending { it.dateAdd }

            medias.forEach {
                Log.e("getAllMedia", it.dateAdd.toString())
            }

            mainHandler.post{
                _medias.postValue(medias)
            }
        }

    }


    private fun getImagesMedia(contentResolver: ContentResolver): List<Media> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED
        )

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val imageMedias = ArrayList<Media>()

        contentResolver.query(
            collectionUri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val dateAddColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(collectionUri,cursor.getLong(idColumn))
                val name = cursor.getString(displayNameColumn)
                val size = cursor.getLong(sizeColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val dateAdd = cursor.getLong(dateAddColumn)
                Log.e("GalleryViewModel","uri : $uri")
                Log.e("GalleryViewModel","mimeType : $mimeType")
                imageMedias.add(Media(uri,name, size, mimeType,dateAdd))

            }
        }
        return imageMedias
    }



    private fun getVideoMedias(contentResolver: ContentResolver) : List<Media> {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED
        )

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val medias = ArrayList<Media>()

        contentResolver.query(
            collectionUri,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val dateAddColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(collectionUri,cursor.getLong(idColumn))
                val name = cursor.getString(displayNameColumn)
                val size = cursor.getLong(sizeColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val dateAdd = cursor.getLong(dateAddColumn)
                Log.e("GalleryViewModel","uri : $uri")
                Log.e("GalleryViewModel","mimeType : $mimeType")
                medias.add(Media(uri,name, size, mimeType,dateAdd))

            }
        }

        return medias
    }


}