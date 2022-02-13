package com.ryanjames.jetpackmobileordering.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ryanjames.jetpackmobileordering.db.dao.GlobalDao
import com.ryanjames.jetpackmobileordering.db.dao.MenuDao
import com.ryanjames.jetpackmobileordering.db.dao.OrderDao
import com.ryanjames.jetpackmobileordering.db.dao.VenueDao
import com.ryanjames.jetpackmobileordering.db.model.*

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