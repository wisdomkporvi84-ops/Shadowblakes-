package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY isFeatured DESC, rating DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("UPDATE products SET stock = :newStock WHERE id = :id")
    suspend fun updateStock(id: String, newStock: Int)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND selectedColor = :color LIMIT 1")
    suspend fun getCartItemByProductAndColor(productId: String, color: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY dateMillis DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, currentStepIndex = :stepIndex WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, stepIndex: Int)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserProfileEntity)

    @Query("UPDATE user_profile SET walletBalance = :newBalance, rewardPoints = :points WHERE id = :userId")
    suspend fun updateWalletBalance(userId: String, newBalance: Double, points: Int)

    @Query("UPDATE user_profile SET defaultAddress = :address WHERE id = :userId")
    suspend fun updateDefaultAddress(userId: String, address: String)
}

@Dao
interface WishlistDao {
    @Query("SELECT productId FROM wishlist")
    fun getWishlistProductIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun deleteWishlist(productId: String)
}

@Dao
interface InventoryAlertDao {
    @Query("SELECT * FROM inventory_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<InventoryAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: InventoryAlertEntity)

    @Query("DELETE FROM inventory_alerts")
    suspend fun clearAlerts()
}
