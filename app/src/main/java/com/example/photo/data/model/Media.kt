package com.example.photo.data.model

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Media(
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String,
    val dateAdd: Long
)

fun groupMediaByDate(medias: List<Media>): Map<String, List<Media>> {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    return medias.groupBy { media ->
        val date = Date(media.dateAdd)
        dateFormat.format(date)
    }
}

data class MediaHeader(val isHeader: Boolean, val date: String? = null, val media: Media? = null)
