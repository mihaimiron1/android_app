package com.mihaimiron.androidapp.objects

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MuseumObject(
    @Json(name = "objectID") val id: Int,
    val title: String,
    val artistDisplayName: String,
    val primaryImageSmall: String,
    val additionalImages: List<String>,
    val objectDate: String,
    val department: String,
    val country: String,
    val state: String,
    val medium: String
)