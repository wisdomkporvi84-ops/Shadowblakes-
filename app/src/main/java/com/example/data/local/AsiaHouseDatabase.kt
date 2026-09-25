package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        UserProfileEntity::class,
        WishlistEntity::class,
        InventoryAlertEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AsiaHouseDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun inventoryAlertDao(): InventoryAlertDao

    companion object {
        @Volatile
        private var INSTANCE: AsiaHouseDatabase? = null

        fun getDatabase(context: Context): AsiaHouseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AsiaHouseDatabase::class.java,
                    "asia_house_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
