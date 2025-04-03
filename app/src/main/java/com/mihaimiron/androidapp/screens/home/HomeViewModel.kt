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

    init {
        nextPage()
    }

    fun nextPage() {
        if (_isLoading.value || _isLastPage.value) return

        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getObjects(page)
            val objects = result.getOrDefault(emptyList())

            if (objects.isEmpty()) {
                _isLastPage.value = true
            } else {
                _objects.value += objects
                page++
            }

            _isLoading.value = false
        }
    }

}