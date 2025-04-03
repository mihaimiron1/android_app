package com.mihaimiron.androidapp.objects

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchObject(
    val total: Int,
    val objectIDs: List<Int>?
)