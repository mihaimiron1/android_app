package com.mihaimiron.androidapp.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihaimiron.androidapp.network.MuseumRepository
import com.mihaimiron.androidapp.objects.MuseumObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: MuseumRepository
) : ViewModel() {

    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isPageLoading = MutableStateFlow(false)
    val isPageLoading = _isPageLoading.asStateFlow()

    private val _objects = MutableStateFlow<List<MuseumObject>>(emptyList())
    val objects = _objects.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage = _isLastPage.asStateFlow()

    private val page = MutableStateFlow(0)
    private var fetchJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _search
                .debounce(500)
                .distinctUntilChanged()
                .filterNot { text -> text.isEmpty() }
                .flatMapLatest { text ->
                    flow {
                        _isLoading.value = true
                        _isPageLoading.value = false
                        try {
                            val result = repository.getObjects(text, 0)
                            val initialObjects = result.getOrDefault(emptyList())

                            _isLastPage.value = initialObjects.isEmpty()
                            emit(initialObjects)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            _isLastPage.value = true
                            emit(emptyList())
                        } finally {
                            _isLoading.value = false
                        }
                    }
                }.collect { initialList ->
                    _objects.value = initialList
                }
        }
    }

    fun search(text: String) {
        _isLastPage.value = false
        page.value = 0
        fetchJob?.cancel()
        _search.value = text
    }

    fun nextPage() {
        if (_isPageLoading.value || _isLoading.value || _search.value.isEmpty() || _isLastPage.value) {
            return
        }

        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            _isPageLoading.value = true
            val nextPage = page.value + 1

            try {
                val result = repository.getObjects(_search.value, nextPage)
                val newObjects = result.getOrDefault(emptyList())

                if (newObjects.isEmpty()) {
                    _isLastPage.value = true
                } else {
                    _objects.update { currentList -> currentList + newObjects }
                    page.value = nextPage
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            } finally {
                _isPageLoading.value = false
            }
        }
    }
}