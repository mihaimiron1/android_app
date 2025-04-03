package com.mihaimiron.androidapp.screens.details

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.bumptech.glide.Glide
import com.mihaimiron.androidapp.network.MuseumRepository
import com.mihaimiron.androidapp.objects.MuseumObject
import com.mihaimiron.androidapp.routes.DetailsRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(val museumObject: MuseumObject) : DetailsUiState
    data class Error(val message: String?) : DetailsUiState
}

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MuseumRepository,
    private val applicationContext: Context,
) : ViewModel() {

    private val objectId: Int = savedStateHandle.toRoute<DetailsRoute>().id

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        fetchObjectDetails()
    }

    private fun fetchObjectDetails() {
        _uiState.value = DetailsUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getObject(objectId)

                result.fold(
                    onSuccess = { museumObject: MuseumObject? ->
                        if (museumObject != null) {
                            _uiState.value = DetailsUiState.Success(museumObject)
                            preCacheImages(museumObject)
                        } else {
                            Log.w("DetailsViewModel", "Object with ID $objectId not found.")
                            _uiState.value = DetailsUiState.Error("Object not found (ID: $objectId)")
                        }
                    },
                    onFailure = { throwable ->
                        Log.e("DetailsViewModel", "Failed to fetch object $objectId", throwable)
                        _uiState.value = DetailsUiState.Error(throwable.message ?: "Failed to load details")
                    }
                )

            } catch (exception: Exception) {
                Log.e("DetailsViewModel", "Error fetching details for object $objectId", exception)
                _uiState.value = DetailsUiState.Error(exception.message ?: "An unexpected error occurred")
            }
        }
    }

    private fun preCacheImages(museumObject: MuseumObject) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageUrls = buildList {
                museumObject.primaryImageSmall.takeIf { it.isNotBlank() }.let { add(it) }
                museumObject.additionalImages.filter { it.isNotBlank() }.let { addAll(it) }
            }

            if (imageUrls.isEmpty()) {
                Log.d("DetailsViewModel", "No images to pre-cache for object $objectId.")
                return@launch
            }

            Log.d("DetailsViewModel", "Starting pre-caching ${imageUrls.size} images for object $objectId.")

            imageUrls.map { url ->
                async {
                    try {
                        val futureTarget = Glide.with(applicationContext)
                            .downloadOnly()
                            .load(url)
                            .submit()

                        futureTarget.get()
                        Log.v("DetailsViewModel", "Successfully cached image: $url")
                    } catch (exception: Exception) {
                        Log.w("DetailsViewModel", "Failed to cache image: $url", exception)
                    }
                }
            }.awaitAll()

            Log.d("DetailsViewModel", "Image pre-caching finished for object $objectId.")
        }
    }
}