package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AsiaHouseDatabase
import com.example.data.model.*
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Home : ScreenDestination()
    object Catalog : ScreenDestination()
    object Cart : ScreenDestination()
    object Tracking : ScreenDestination()
    object Dashboard : ScreenDestination()
    data class ProductDetail(val productId: String) : ScreenDestination()
    data class OrderDetail(val orderId: String) : ScreenDestination()
    data class OrderSuccess(val order: Order) : ScreenDestination()
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AsiaHouseDatabase.getDatabase(application)
    private val repository = ShopRepository(
        productDao = db.productDao(),
        cartDao = db.cartDao(),
        orderDao = db.orderDao(),
        userDao = db.userDao(),
        wishlistDao = db.wishlistDao(),
        inventoryAlertDao = db.inventoryAlertDao()
    )

    val products: StateFlow<List<Product>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val wishlistIds: StateFlow<List<String>> = repository.wishlistProductIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryAlerts: StateFlow<List<InventoryAlert>> = repository.inventoryAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation state
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Search and Catalog filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All") // "All", "Phone Accessories", "Laptop Accessories"
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _selectedSubcategoryFilter = MutableStateFlow("All")
    val selectedSubcategoryFilter: StateFlow<String> = _selectedSubcategoryFilter.asStateFlow()

    private val _onlyInStockFilter = MutableStateFlow(false)
    val onlyInStockFilter: StateFlow<String> = MutableStateFlow("false") // helper

    // Checkout & Reservation Timer State
    private val _reservationSecondsRemaining = MutableStateFlow(600) // 10 minutes
    val reservationSecondsRemaining: StateFlow<Int> = _reservationSecondsRemaining.asStateFlow()
    private var timerJob: Job? = null

    // Promo code
    private val _appliedPromoCode = MutableStateFlow<String?>(null)
    val appliedPromoCode: StateFlow<String?> = _appliedPromoCode.asStateFlow()

    private val _promoDiscountPercent = MutableStateFlow(0)
    val promoDiscountPercent: StateFlow<Int> = _promoDiscountPercent.asStateFlow()

    // Checkout Processing State
    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

    private val _checkoutError = MutableStateFlow<String?>(null)
    val checkoutError: StateFlow<String?> = _checkoutError.asStateFlow()

    // Notification Sheet
    private val _showInventoryAlertsSheet = MutableStateFlow(false)
    val showInventoryAlertsSheet: StateFlow<Boolean> = _showInventoryAlertsSheet.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
        startReservationTimer()
    }

    private fun startReservationTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_reservationSecondsRemaining.value > 0) {
                    _reservationSecondsRemaining.value -= 1
                } else {
                    _reservationSecondsRemaining.value = 600 // auto-renew session lock
                }
            }
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategoryFilter.value = category
        _selectedSubcategoryFilter.value = "All"
    }

    fun setSelectedSubcategory(subcategory: String) {
        _selectedSubcategoryFilter.value = subcategory
    }

    fun setInventoryAlertsSheetVisible(visible: Boolean) {
        _showInventoryAlertsSheet.value = visible
    }

    fun addToCart(product: Product, color: String = product.colors.firstOrNull() ?: "Default", quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product.id, color, quantity)
            _reservationSecondsRemaining.value = 600 // Refresh reservation countdown lock
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val isCurrentlyWishlisted = wishlistIds.value.contains(product.id)
            repository.toggleWishlist(product.id, isCurrentlyWishlisted)
        }
    }

    fun applyPromoCode(code: String): Boolean {
        val clean = code.trim().uppercase()
        return when (clean) {
            "ASIA15" -> {
                _appliedPromoCode.value = "ASIA15"
                _promoDiscountPercent.value = 15
                true
            }
            "TECHVIP" -> {
                _appliedPromoCode.value = "TECHVIP"
                _promoDiscountPercent.value = 20
                true
            }
            "WELCOME10" -> {
                _appliedPromoCode.value = "WELCOME10"
                _promoDiscountPercent.value = 10
                true
            }
            else -> false
        }
    }

    fun removePromoCode() {
        _appliedPromoCode.value = null
        _promoDiscountPercent.value = 0
    }

    fun processSecureCheckout(
        paymentMethod: PaymentType,
        deliveryAddress: String,
        onSuccess: (Order) -> Unit
    ) {
        val currentItems = cartItems.value
        if (currentItems.isEmpty()) return

        // Verify stock availability
        for (item in currentItems) {
            if (item.product.stock < item.quantity) {
                _checkoutError.value = "Stock mismatch for ${item.product.title}. Only ${item.product.stock} units remaining!"
                return
            }
        }

        _isProcessingPayment.value = true
        _checkoutError.value = null

        viewModelScope.launch {
            // Simulate 256-bit SSL handshake and 3D Secure / Biometric validation delay
            delay(1500)

            val subtotal = currentItems.sumOf { it.product.price * it.quantity }
            val discount = subtotal * (_promoDiscountPercent.value / 100.0)
            val shipping = if (subtotal > 99.0) 0.0 else 9.99
            val total = subtotal - discount + shipping

            val confirmedOrder = repository.executeSecureCheckout(
                items = currentItems,
                paymentMethodName = "${paymentMethod.title} (${paymentMethod.badge})",
                shippingAddress = deliveryAddress,
                totalAmount = total,
                discountApplied = discount
            )

            // Deduct or award points
            val currentUser = userProfile.value
            val earnedPoints = (total * 5).toInt()
            val newBalance = if (paymentMethod == PaymentType.ASIA_HOUSE_PAY) {
                (currentUser.walletBalance - total).coerceAtLeast(0.0)
            } else {
                currentUser.walletBalance
            }
            db.userDao().updateWalletBalance(
                userId = currentUser.id,
                newBalance = newBalance,
                points = currentUser.rewardPoints + earnedPoints
            )

            _isProcessingPayment.value = false
            _appliedPromoCode.value = null
            _promoDiscountPercent.value = 0

            _currentScreen.value = ScreenDestination.OrderSuccess(confirmedOrder)
            onSuccess(confirmedOrder)
        }
    }

    fun advanceOrderStep(orderId: String, currentStep: Int) {
        viewModelScope.launch {
            repository.advanceOrderStatus(orderId, currentStep)
        }
    }

    // Demo / Real-time Inventory triggers
    fun simulateStockDrop(product: Product) {
        viewModelScope.launch {
            val newStock = (product.stock - 1).coerceAtLeast(0)
            repository.updateStock(product.id, newStock, product.title)
        }
    }

    fun restockProduct(product: Product, amount: Int = 20) {
        viewModelScope.launch {
            repository.restockProduct(product.id, amount, product.title)
        }
    }
}
