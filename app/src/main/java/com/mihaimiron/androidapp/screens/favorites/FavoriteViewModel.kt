package com.mihaimiron.androidapp.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihaimiron.androidapp.database.FavoriteDao
import com.mihaimiron.androidapp.database.FavoriteEntity
import com.mihaimiron.androidapp.objects.MuseumObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoriteDao: FavoriteDao
) : ViewModel() {
    
    val favorites: Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    fun isFavorite(objectId: Int): Flow<Boolean> = favoriteDao.isFavorite(objectId)

    fun toggleFavorite(museumObject: MuseumObject) {
        viewModelScope.launch {
            val isFavorite = favoriteDao.isFavorite(museumObject.id).first()
            if (isFavorite) {
                favoriteDao.deleteFavorite(museumObject.id)
            } else {
                favoriteDao.insertFavorite(
                    FavoriteEntity(
                        objectId = museumObject.id,
                        title = museumObject.title,
                        artistName = museumObject.artistDisplayName,
                        imageUrl = museumObject.primaryImageSmall
                    )
                )
            }
        }
    }
} 