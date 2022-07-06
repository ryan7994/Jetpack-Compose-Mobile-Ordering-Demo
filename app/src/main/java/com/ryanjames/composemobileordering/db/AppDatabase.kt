package com.ryanjames.composemobileordering.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ryanjames.composemobileordering.db.dao.GlobalDao
import com.ryanjames.composemobileordering.db.dao.MenuDao
import com.ryanjames.composemobileordering.db.dao.OrderDao
import com.ryanjames.composemobileordering.db.dao.VenueDao
import com.ryanjames.composemobileordering.db.model.*

@Database(
    version = 2,
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
        CurrentOrderEntity::class,
        StoreHoursEntity::class
    ],
//    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun venueDao(): VenueDao
    abstract fun menuDao(): MenuDao
    abstract fun orderDao(): OrderDao
    abstract fun globalDao(): GlobalDao
}