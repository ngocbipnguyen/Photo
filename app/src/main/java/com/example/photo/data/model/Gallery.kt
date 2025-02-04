package com.example.photo.data.model

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/*
* todo : handler all picture and video
* */
@Singleton
class Gallery @Inject constructor(@ApplicationContext val context: Context) {

    fun getMedia(contentResolver: ContentResolver): List<Media> {
        val allMedia: List<Media> =
            getImagesMedia(contentResolver) + getVideosMedia(contentResolver)
        return allMedia.sortedByDescending { it.dateAdd }
    }


    private fun getImagesMedia(contentResolver: ContentResolver): ArrayList<Media> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val medias = ArrayList<Media>()

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
            val albumAddColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(collectionUri,cursor.getLong(idColumn))
                val name = cursor.getString(displayNameColumn)
                val size = cursor.getLong(sizeColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val dateAdd = cursor.getLong(dateAddColumn)
                val albums = cursor.getString(albumAddColumn)
                Log.e("GalleryViewModel","uri : $uri")
                Log.e("GalleryViewModel","albums : $albums")
                medias.add(Media(uri,name, size, mimeType,dateAdd, albums))

            }

        }

        return medias
    }

    private fun getVideosMedia(contentResolver: ContentResolver): ArrayList<Media> {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.ALBUM,
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
            val albumAddColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)

            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(collectionUri,cursor.getLong(idColumn))
                val name = cursor.getString(displayNameColumn)
                val size = cursor.getLong(sizeColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val dateAdd = cursor.getLong(dateAddColumn)
                val album = cursor.getString(albumAddColumn)

                Log.e("GalleryViewModel","uri : $uri")
                Log.e("GalleryViewModel","album : $album")
                medias.add(Media(uri,name, size, mimeType,dateAdd, album))

            }
        }

        return medias
    }

    private fun getMediaFromUri(contentResolver: ContentResolver,uri: Uri): Media? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
        )

        var newMedia: Media? = null

        val cursor = contentResolver.query(uri, projection, null, null,null)

        if (cursor!!.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            val name =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
            val mimeType =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
            val dateAdd = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
            val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
            Log.e("GalleryViewModel","name " + name)
            Log.e("GalleryViewModel","size " + size)
            Log.e("GalleryViewModel","mimeType " + mimeType)
            newMedia = Media(uri, name, size, mimeType,dateAdd,album)
        }

        return newMedia
    }

}