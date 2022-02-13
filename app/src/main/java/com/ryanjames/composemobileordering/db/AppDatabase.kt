package com.ryanjames.composemobileordering.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ryanjames.composemobileordering.db.dao.GlobalDao
import com.ryanjames.composemobileordering.db.dao.MenuDao
import com.ryanjames.composemobileordering.db.dao.OrderDao
import com.ryanjames.composemobileordering.db.dao.VenueDao
import com.ryanjames.composemobileordering.db.model.*

@Database(
    entities = [
        VenueEntity::class,
        VenueCategoryEntity::class,
        VenueCategoryCrossRef::class,
        BasicMenuProductEntity::class,
        BasicMenuEntity::class,
        BasicMenuCategoryEntity::class,
        LineItemEntity::class,
        LineItemProductEntity::class,
        LineItemModifierGroupEntity::class,
        LineItemModifierInfoEntity::class,
        GlobalEntity::class,
        CurrentOrderEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun venueDao(): VenueDao
    abstract fun menuDao(): MenuDao
    abstract fun orderDao(): OrderDao
    abstract fun globalDao(): GlobalDao
}