package com.mihaimiron.androidapp.objects

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MuseumObject(
    @Json(name = "objectID") val id: Int,
    val title: String,
    @Json(name = "artistDisplayName") val artistDisplayName: String,
    @Json(name = "primaryImageSmall") val primaryImageSmall: String,
    @Json(name = "additionalImages") val additionalImages: List<String>,
    @Json(name = "objectDate") val objectDate: String,
    @Json(name = "department") val department: String,
    @Json(name = "country") val country: String,
    @Json(name = "state") val state: String,
    @Json(name = "medium") val medium: String
) {
    val hasValidImage: Boolean
        get() = primaryImageSmall.isNotBlank() && primaryImageSmall.startsWith("http")
}