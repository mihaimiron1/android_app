package com.mihaimiron.androidapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihaimiron.androidapp.network.MuseumRepository
import com.mihaimiron.androidapp.objects.MuseumObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: MuseumRepository
) : ViewModel() {

    private val _objects = MutableStateFlow<List<MuseumObject>>(emptyList())
    val objects = _objects.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage = _isLastPage.asStateFlow()

    private var page = 0
    private val loadedIds = mutableSetOf<Int>()

    init {
        nextPage()
    }

    fun nextPage() {
        if (_isLoading.value || _isLastPage.value) return

        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getObjects(page)
            val newObjects = result.getOrDefault(emptyList())

            if (newObjects.isEmpty()) {
                _isLastPage.value = true
            } else {
                // Filter out any objects we've already loaded
                val uniqueNewObjects = newObjects.filter { obj -> 
                    loadedIds.add(obj.id)
                }
                
                if (uniqueNewObjects.isEmpty()) {
                    _isLastPage.value = true
                } else {
                    _objects.value = _objects.value + uniqueNewObjects
                    page++
                }
            }

            _isLoading.value = false
        }
    }

}