package com.ryanjames.composemobileordering.mocks

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.domain.Venue
import com.ryanjames.composemobileordering.repository.VenueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockVenueRepository : VenueRepository {

    override fun getFeaturedVenues(): Flow<Resource<Pair<List<Venue>, List<Venue>>>> {
        return flow {

        }
    }

    override fun getVenueById(id: String): Flow<Resource<Venue?>> {
        return flow {

        }
    }

    override suspend fun getCurrentVenue(): Flow<Resource<Venue?>> {
        return flow {

        }
    }

    override suspend fun getCurrentVenueId(): String? {
        return null
    }

    override fun getAllVenues(): Flow<Resource<List<Venue>>> {
        return flow {

        }
    }
}