package com.ryanjames.composemobileordering.repository

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.domain.Venue
import kotlinx.coroutines.flow.Flow

interface VenueRepository {

    fun getFeaturedVenues(): Flow<Resource<Pair<List<Venue>, List<Venue>>>>

    fun getVenueById(id: String): Flow<Resource<Venue?>>

    suspend fun getCurrentVenue(): Flow<Resource<Venue?>>

    suspend fun getCurrentVenueId(): String?

    fun getAllVenues(): Flow<Resource<List<Venue>>>
}