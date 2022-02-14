package com.ryanjames.composemobileordering.repository

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.db.model.BasicMenuWithCategories
import com.ryanjames.composemobileordering.domain.Product
import kotlinx.coroutines.flow.Flow

interface AbsMenuRepository {

    fun getBasicMenuByVenue(venueId: String): Flow<Resource<BasicMenuWithCategories?>>
    fun getProductById(id: String): Flow<Resource<Product>>
}