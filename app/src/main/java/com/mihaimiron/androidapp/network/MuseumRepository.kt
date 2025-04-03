package com.mihaimiron.androidapp.network

import com.mihaimiron.androidapp.objects.MuseumObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.IOException

interface MuseumRepository {
    suspend fun getObjects(query: String, page: Int): Result<List<MuseumObject>>
    suspend fun getObjects(page: Int): Result<List<MuseumObject>>
    suspend fun getObject(objectId: Int): Result<MuseumObject?>
}

class MuseumRepositoryImpl() : MuseumRepository {

    private val api = museumAPI
    private val pageSize = 15

    private suspend fun fetchObjectDetails(ids: List<Int>): List<MuseumObject?> = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) return@withContext emptyList()

        return@withContext ids.map { objectId ->
            async {
                try {
                    val response = api.getObject(objectId)
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        println("Error fetching object $objectId: ${response.code()}")
                        null
                    }
                } catch (e: Exception) {
                    println("Exception fetching object $objectId: ${e.message}")
                    null
                }
            }
        }.awaitAll()
    }

    override suspend fun getObjects(query: String, page: Int): Result<List<MuseumObject>> = withContext(Dispatchers.IO) {
        runCatching {
            val idResponse = api.getObjects(query)
            if (!idResponse.isSuccessful) {
                throw IOException("Failed to fetch object IDs for query '$query': ${idResponse.code()}")
            }

            val searchResult = idResponse.body()
            val objectIds = searchResult?.objectIDs ?: emptyList()

            if (objectIds.isEmpty()) {
                return@runCatching emptyList<MuseumObject>()
            }

            val startIndex = page * pageSize
            val endIndex = minOf(startIndex + pageSize, objectIds.size)

            if (startIndex >= objectIds.size) {
                return@runCatching emptyList<MuseumObject>()
            }

            val idsToFetch = objectIds.subList(startIndex, endIndex)

            val museumObjectsWithNulls = fetchObjectDetails(idsToFetch)
            museumObjectsWithNulls.filterNotNull()
        }
    }

    override suspend fun getObjects(page: Int): Result<List<MuseumObject>> = withContext(Dispatchers.IO) {
        runCatching {
            val idResponse = api.getObjects()
            if (!idResponse.isSuccessful) {
                throw IOException("Failed to fetch all object IDs: ${idResponse.code()}")
            }

            val searchResult = idResponse.body()
            val objectIds = searchResult?.objectIDs ?: emptyList()

            if (objectIds.isEmpty()) {
                return@runCatching emptyList<MuseumObject>()
            }

            val startIndex = page * pageSize
            val endIndex = minOf(startIndex + pageSize, objectIds.size)

            if (startIndex >= objectIds.size) {
                return@runCatching emptyList<MuseumObject>()
            }

            val idsToFetch = objectIds.subList(startIndex, endIndex)

            val museumObjectsWithNulls = fetchObjectDetails(idsToFetch)
            museumObjectsWithNulls.filterNotNull()
        }
    }

    override suspend fun getObject(objectId: Int): Result<MuseumObject?> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getObject(objectId)

            if (response.isSuccessful) {
                response.body()
            } else {
                if (response.code() == 404) {
                    null
                } else {
                    throw IOException("Failed to fetch object $objectId: ${response.code()}")
                }
            }
        }
    }
}