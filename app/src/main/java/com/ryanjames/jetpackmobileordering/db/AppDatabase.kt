package com.ryanjames.jetpackmobileordering.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ryanjames.jetpackmobileordering.db.dao.MenuDao
import com.ryanjames.jetpackmobileordering.db.dao.VenueDao
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuCategoryEntity
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuEntity
import com.ryanjames.jetpackmobileordering.db.model.BasicMenuProductEntity

@Database(
    entities = arrayOf(
        VenueEntity::class,
        VenueCategoryEntity::class,
        VenueCategoryCrossRef::class,
        BasicMenuProductEntity::class,
        BasicMenuEntity::class,
        BasicMenuCategoryEntity::class
    ), version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun venueDao(): VenueDao
    abstract fun menuDao(): MenuDao
}