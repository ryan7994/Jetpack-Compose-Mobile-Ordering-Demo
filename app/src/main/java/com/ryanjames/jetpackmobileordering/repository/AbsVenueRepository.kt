package com.ryanjames.jetpackmobileordering.repository

import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.domain.Venue
import kotlinx.coroutines.flow.Flow

interface AbsVenueRepository {

    fun getFeaturedVenues(): Flow<Resource<Pair<List<Venue>, List<Venue>>>>

    fun getVenueById(id: String): Flow<Resource<Venue?>>

    suspend fun getCurrentVenueId(): String?

    fun getCurrentVenueIdFlow(): Flow<String?>

    fun getAllVenues(): Flow<Resource<List<Venue>>>
}