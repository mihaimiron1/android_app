package com.mihaimiron.androidapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val objectId: Int,
    val title: String,
    val artistName: String,
    val imageUrl: String
) 