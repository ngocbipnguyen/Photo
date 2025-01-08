package com.example.photo.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photo.data.model.Gallery
import com.example.photo.data.model.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    val gallery: Gallery,
    @ApplicationContext val context: Context
) : ViewModel() {

    private var _media = MutableLiveData<List<Media>>()
    val media: LiveData<List<Media>> = _media

    init {
        viewModelScope.launch {
            _media.postValue(gallery.getMedia(context.contentResolver))
        }
    }

}