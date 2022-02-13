package com.ryanjames.jetpackmobileordering.repository

import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuWithCategories
import com.ryanjames.jetpackmobileordering.domain.Product
import kotlinx.coroutines.flow.Flow

interface AbsMenuRepository {

    fun getBasicMenuByVenue(venueId: String): Flow<Resource<BasicMenuWithCategories?>>
    fun getProductById(id: String): Flow<Resource<Product>>
}