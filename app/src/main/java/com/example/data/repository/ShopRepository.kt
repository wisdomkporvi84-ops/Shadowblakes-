package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ShopRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao,
    private val userDao: UserDao,
    private val wishlistDao: WishlistDao,
    private val inventoryAlertDao: InventoryAlertDao
) {
    suspend fun initializeSeedDataIfNeeded() {
        if (productDao.getCount() == 0) {
            val initialProducts = listOf(
                ProductEntity(
                    id = "prod_magsafe_case",
                    title = "Asia House TitanShield MagSafe Case",
                    category = "Phone Accessories",
                    subcategory = "Cases & Protection",
                    price = 49.00,
                    originalPrice = 65.00,
                    rating = 4.9f,
                    reviewCount = 384,
                    description = "Aerospace-grade titanium reinforcement with N52 ultra-strong neodymium magnet ring. Military drop-tested up to 14ft with raised camera bezels and antimicrobial nano-coating.",
                    specificationsCsv = "Titanium alloy + Bayer TPU, MagSafe 15W Wireless Speed, 14ft Drop Certified, 1.8mm Camera Lips",
                    compatibility = "iPhone 16 Pro, 16 Pro Max, 15 Pro, 15 Pro Max",
                    stock = 4,
                    initialStock = 50,
                    isFeatured = true,
                    isBestSeller = true,
                    badge = "Low Stock - Only 4 Left",
                    imageResName = "cat_phone_acc",
                    colorsCsv = "Natural Titanium, Deep Obsidian, Matte Sunset, Frost Silver",
                    warrantyMonths = 24
                ),
                ProductEntity(
                    id = "prod_gan_100w",
                    title = "HyperGaN 100W Dual USB-C Fast Charger",
                    category = "Phone Accessories",
                    subcategory = "GaN Fast Chargers",
                    price = 59.00,
                    originalPrice = 79.00,
                    rating = 4.8f,
                    reviewCount = 512,
                    description = "Gallium Nitride III architecture delivering 100W maximum throughput in a pocket-sized form factor. Powers a MacBook Pro to 50% in 30 minutes while simultaneously fast charging an iPhone.",
                    specificationsCsv = "100W Max PD 3.1 & PPS, 2x USB-C + 1x USB-A, ActiveTemp Thermal Guard 2.0, Foldable US/EU/UK Prongs",
                    compatibility = "iPhone, MacBook Pro/Air, Galaxy S24/S25, iPad Pro, ThinkPad",
                    stock = 18,
                    initialStock = 60,
                    isFeatured = true,
                    isBestSeller = true,
                    badge = "Best Seller",
                    imageResName = "hero_asia_house",
                    colorsCsv = "Midnight Black, Arctic White",
                    warrantyMonths = 36
                ),
                ProductEntity(
                    id = "prod_dock_12in1",
                    title = "ThunderPort Pro 12-in-1 Dual 4K 120Hz Hub Dock",
                    category = "Laptop Accessories",
                    subcategory = "Hubs & Docks",
                    price = 149.00,
                    originalPrice = 199.00,
                    rating = 4.9f,
                    reviewCount = 295,
                    description = "Engineered for elite workstation productivity. Features dual HDMI 2.1 (4K 120Hz / 8K 30Hz), Gigabit Ethernet, 10Gbps USB 3.2 Gen 2 ports, UHS-II SD 4.0 slots, and 100W pass-through charging in solid milled aluminum.",
                    specificationsCsv = "Dual 4K 120Hz HDMI + DisplayPort, 10Gbps NVMe/SSD data speed, 100W PD Pass-through, RJ45 1000Mbps LAN",
                    compatibility = "MacBook Pro M1-M4, Dell XPS 13/15, ThinkPad, Surface Pro, iPad Pro",
                    stock = 5,
                    initialStock = 40,
                    isFeatured = true,
                    isBestSeller = false,
                    badge = "Staff Pick",
                    imageResName = "cat_laptop_acc",
                    colorsCsv = "Space Gray, Starlight Silver",
                    warrantyMonths = 36
                ),
                ProductEntity(
                    id = "prod_stand_ergolift",
                    title = "ErgoLift CNC Aluminum Magnetic Laptop Riser",
                    category = "Laptop Accessories",
                    subcategory = "Stands & Ergonomics",
                    price = 65.00,
                    originalPrice = 85.00,
                    rating = 4.7f,
                    reviewCount = 189,
                    description = "Precision sandblasted aviation aluminum with dual-axis 360-degree rotation and tension-locked damping hinges. Elevates screens to eye level for neck relief with integrated cable pass-through.",
                    specificationsCsv = "Full CNC Milled Aluminum, Supports up to 22 lbs (10 kg), Heat Dissipation Cutouts, Silicone Anti-Scratch Pads",
                    compatibility = "10\" to 17.3\" Laptops, MacBooks, Surface, Gaming Laptops",
                    stock = 2,
                    initialStock = 30,
                    isFeatured = true,
                    isBestSeller = true,
                    badge = "Critical Stock: Only 2 Left",
                    imageResName = "cat_laptop_acc",
                    colorsCsv = "Space Gray, Silver Frost",
                    warrantyMonths = 24
                ),
                ProductEntity(
                    id = "prod_travel_3in1",
                    title = "AeroMagnetic 3-in-1 Foldable Qi2 Wireless Station",
                    category = "Phone Accessories",
                    subcategory = "MagSafe & Mounts",
                    price = 89.00,
                    originalPrice = 120.00,
                    rating = 4.8f,
                    reviewCount = 420,
                    description = "Foldable origami magnetic design charges iPhone (15W Qi2), Apple Watch Fast Charge (5W), and wireless earbuds (5W) concurrently. Folds flat to wallet size for seamless travel.",
                    specificationsCsv = "15W Qi2 Certified, Dual-pivot folding, Includes 30W GaN adapter + woven cable + velvet travel pouch",
                    compatibility = "iPhone 12-16 series, Apple Watch Ultra/Series 4-10, AirPods Pro",
                    stock = 7,
                    initialStock = 45,
                    isFeatured = false,
                    isBestSeller = true,
                    badge = "Travel Must-Have",
                    imageResName = "cat_phone_acc",
                    colorsCsv = "Stealth Black, Sandstone Beige",
                    warrantyMonths = 24
                ),
                ProductEntity(
                    id = "prod_sleeve_zensleeve",
                    title = "ZenSleeve Vegan Leather & Merino Felt Laptop Case",
                    category = "Laptop Accessories",
                    subcategory = "Laptop Sleeves & Bags",
                    price = 45.00,
                    originalPrice = 60.00,
                    rating = 4.9f,
                    reviewCount = 164,
                    description = "Sustainable bio-based vegan leather paired with shock-absorbing German merino wool felt lining. Features invisible magnetic snap closure and hidden rear accessory pocket for chargers and hubs.",
                    specificationsCsv = "Water-repellent PU exterior, 4mm high-density impact felt, Scratch-free magnetic clasp, Slim 12mm profile",
                    compatibility = "MacBook Pro 14\" / 16\", MacBook Air 13\" / 15\", Dell XPS 13/15",
                    stock = 12,
                    initialStock = 50,
                    isFeatured = false,
                    isBestSeller = false,
                    badge = "Eco Luxury",
                    imageResName = "cat_laptop_acc",
                    colorsCsv = "Caramel Tan, Forest Green, Charcoal Black",
                    warrantyMonths = 24
                ),
                ProductEntity(
                    id = "prod_cable_kevlar",
                    title = "Kevlar-Shield 240W USB-C to USB-C Fast Cable (2m)",
                    category = "Phone Accessories",
                    subcategory = "Cables & Adapters",
                    price = 24.00,
                    originalPrice = 32.00,
                    rating = 4.9f,
                    reviewCount = 630,
                    description = "Double-braided bulletproof DuPont Kevlar core tested for over 50,000 90-degree bends. E-marker smart chip supports 240W EPR charging and 480Mbps ultra-stable data transfer.",
                    specificationsCsv = "240W (48V/5A) Power Delivery, 2-Meter Length, Zinc Alloy Casing with LED charging status indicator",
                    compatibility = "Universal USB-C Devices (Laptops, Phones, Tablets, Gaming Consoles)",
                    stock = 42,
                    initialStock = 80,
                    isFeatured = false,
                    isBestSeller = false,
                    badge = "50,000+ Bend Tested",
                    imageResName = "hero_asia_house",
                    colorsCsv = "Braided Shadow, Crimson Accent, Gold Fleck",
                    warrantyMonths = 60
                ),
                ProductEntity(
                    id = "prod_keyboard_aeroglide",
                    title = "AeroGlide Low-Profile Wireless Mechanical Keyboard",
                    category = "Laptop Accessories",
                    subcategory = "Input Devices",
                    price = 119.00,
                    originalPrice = 149.00,
                    rating = 4.8f,
                    reviewCount = 210,
                    description = "Ultra-slim 75% layout keyboard featuring custom hot-swappable low-profile Gateron tactile switches. Seamless 1-click toggling between Mac and Windows with Bluetooth 5.3 and 2.4GHz dongle.",
                    specificationsCsv = "75% ANSI Layout, 4000mAh Battery (up to 300 hrs), PBT dye-sublimated keycaps, Per-key RGB backlight",
                    compatibility = "macOS, Windows 11, iOS, iPadOS, Android",
                    stock = 9,
                    initialStock = 35,
                    isFeatured = true,
                    isBestSeller = false,
                    badge = "Workstation Upgrade",
                    imageResName = "cat_laptop_acc",
                    colorsCsv = "Retro Slate, Cyber Neon, Polar White",
                    warrantyMonths = 24
                )
            )
            productDao.insertProducts(initialProducts)
        }

        // Initialize User Profile
        userDao.insertOrUpdateUser(
            UserProfileEntity(
                id = "user_101",
                name = "Kenji Takahashi",
                email = "kenji.takahashi@asiahouse.shop",
                phone = "+1 (555) 389-4021",
                membershipTier = "Black VIP Member",
                rewardPoints = 3450,
                walletBalance = 420.00,
                defaultAddress = "84 Orchard Boulevard, Unit #14-02, Orchard Residences, Singapore 238826",
                secondaryAddress = "7-1 Roppongi Hills Mori Tower, Minato City, Tokyo 106-6108, Japan"
            )
        )

        // Seed initial order for dashboard
        val initialOrders = listOf(
            OrderEntity(
                orderId = "AH-92041",
                dateMillis = System.currentTimeMillis() - 86400000L * 2, // 2 days ago
                status = OrderStatus.OUT_FOR_DELIVERY.name,
                totalAmount = 198.00,
                itemsSummary = "HyperGaN 100W Fast Charger + TitanShield MagSafe Case",
                itemsCount = 2,
                paymentMethod = "Asia House Pay (Encrypted)",
                paymentTxnId = "TXN_AH_7792182041",
                shippingAddress = "84 Orchard Boulevard, Unit #14-02, Singapore 238826",
                trackingNumber = "AH-EXP-992014-SG",
                estimatedDelivery = "Today by 4:30 PM",
                currentStepIndex = 3
            ),
            OrderEntity(
                orderId = "AH-84192",
                dateMillis = System.currentTimeMillis() - 86400000L * 14,
                status = OrderStatus.DELIVERED.name,
                totalAmount = 149.00,
                itemsSummary = "ThunderPort Pro 12-in-1 Dual 4K Dock",
                itemsCount = 1,
                paymentMethod = "Credit Card (•••• 4092)",
                paymentTxnId = "TXN_CARD_8841929120",
                shippingAddress = "84 Orchard Boulevard, Unit #14-02, Singapore 238826",
                trackingNumber = "AH-EXP-773192-SG",
                estimatedDelivery = "Delivered on Sep 12",
                currentStepIndex = 4
            )
        )
        for (ord in initialOrders) {
            orderDao.insertOrder(ord)
        }

        // Seed initial inventory alert
        inventoryAlertDao.insertAlert(
            InventoryAlertEntity(
                id = "alert_1",
                productId = "prod_stand_ergolift",
                productName = "ErgoLift CNC Aluminum Riser",
                message = "CRITICAL LOW STOCK: Only 2 units remaining in Tokyo warehouse.",
                timestamp = System.currentTimeMillis() - 3600000L * 3,
                isLowStock = true
            )
        )
    }

    val products: Flow<List<Product>> = productDao.getAllProducts().map { entities ->
        entities.map { it.toModel() }
    }

    fun getProductById(id: String): Flow<Product?> = productDao.getProductById(id).map { it?.toModel() }

    val wishlistProductIds: Flow<List<String>> = wishlistDao.getWishlistProductIds()

    val userProfile: Flow<UserProfile> = userDao.getUserProfile().map { entity ->
        entity?.let {
            UserProfile(
                id = it.id,
                name = it.name,
                email = it.email,
                phone = it.phone,
                membershipTier = it.membershipTier,
                rewardPoints = it.rewardPoints,
                walletBalance = it.walletBalance,
                defaultAddress = it.defaultAddress,
                secondaryAddress = it.secondaryAddress
            )
        } ?: UserProfile()
    }

    val orders: Flow<List<Order>> = orderDao.getAllOrders().map { entities ->
        entities.map { it.toModel() }
    }

    val cartItems: Flow<List<CartItem>> = combine(cartDao.getAllCartItems(), productDao.getAllProducts()) { cartEntities, productEntities ->
        val productMap = productEntities.associateBy { it.id }
        cartEntities.mapNotNull { cartEntity ->
            val product = productMap[cartEntity.productId]?.toModel()
            if (product != null) {
                CartItem(
                    id = cartEntity.id,
                    product = product,
                    quantity = cartEntity.quantity,
                    selectedColor = cartEntity.selectedColor
                )
            } else null
        }
    }

    val inventoryAlerts: Flow<List<InventoryAlert>> = inventoryAlertDao.getAllAlerts().map { list ->
        list.map {
            InventoryAlert(
                id = it.id,
                productId = it.productId,
                productName = it.productName,
                message = it.message,
                timestamp = it.timestamp,
                isLowStock = it.isLowStock
            )
        }
    }

    suspend fun addToCart(productId: String, color: String, quantity: Int = 1) {
        val existing = cartDao.getCartItemByProductAndColor(productId, color)
        if (existing != null) {
            cartDao.updateQuantity(existing.id, existing.quantity + quantity)
        } else {
            cartDao.insertCartItem(
                CartItemEntity(
                    productId = productId,
                    quantity = quantity,
                    selectedColor = color
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteCartItem(cartItemId)
        } else {
            cartDao.updateQuantity(cartItemId, newQuantity)
        }
    }

    suspend fun removeCartItem(cartItemId: Long) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun toggleWishlist(productId: String, isInWishlist: Boolean) {
        if (isInWishlist) {
            wishlistDao.deleteWishlist(productId)
        } else {
            wishlistDao.insertWishlist(WishlistEntity(productId = productId))
        }
    }

    suspend fun updateStock(productId: String, newStock: Int, productName: String) {
        productDao.updateStock(productId, newStock)
        if (newStock <= 3) {
            inventoryAlertDao.insertAlert(
                InventoryAlertEntity(
                    id = UUID.randomUUID().toString(),
                    productId = productId,
                    productName = productName,
                    message = "REAL-TIME ALERT: Stock dropped to $newStock units!",
                    timestamp = System.currentTimeMillis(),
                    isLowStock = true
                )
            )
        } else if (newStock > 10) {
            inventoryAlertDao.insertAlert(
                InventoryAlertEntity(
                    id = UUID.randomUUID().toString(),
                    productId = productId,
                    productName = productName,
                    message = "RESTOCK CONFIRMED: $newStock fresh units stocked in warehouse.",
                    timestamp = System.currentTimeMillis(),
                    isLowStock = false
                )
            )
        }
    }

    suspend fun restockProduct(productId: String, restockAmount: Int, productName: String) {
        productDao.updateStock(productId, restockAmount)
        inventoryAlertDao.insertAlert(
            InventoryAlertEntity(
                id = UUID.randomUUID().toString(),
                productId = productId,
                productName = productName,
                message = "RESTOCK NOTICE: $productName inventory replenished to $restockAmount units.",
                timestamp = System.currentTimeMillis(),
                isLowStock = false
            )
        )
    }

    suspend fun advanceOrderStatus(orderId: String, currentStep: Int) {
        val nextStep = (currentStep + 1).coerceAtMost(4)
        val newStatus = when (nextStep) {
            0 -> OrderStatus.CONFIRMED.name
            1 -> OrderStatus.PROCESSING.name
            2 -> OrderStatus.DISPATCHED.name
            3 -> OrderStatus.OUT_FOR_DELIVERY.name
            else -> OrderStatus.DELIVERED.name
        }
        orderDao.updateOrderStatus(orderId, newStatus, nextStep)
    }

    suspend fun executeSecureCheckout(
        items: List<CartItem>,
        paymentMethodName: String,
        shippingAddress: String,
        totalAmount: Double,
        discountApplied: Double
    ): Order {
        val randomSuffix = (1000..9999).random()
        val orderId = "AH-$randomSuffix"
        val txnId = "TXN_SECURE_${System.currentTimeMillis().toString().takeLast(8)}_$randomSuffix"
        val trackingNo = "AH-EXP-${(100000..999999).random()}-SG"

        // Deduct inventory in real-time
        for (item in items) {
            val updatedStock = (item.product.stock - item.quantity).coerceAtLeast(0)
            productDao.updateStock(item.product.id, updatedStock)
            if (updatedStock <= 3) {
                inventoryAlertDao.insertAlert(
                    InventoryAlertEntity(
                        id = UUID.randomUUID().toString(),
                        productId = item.product.id,
                        productName = item.product.title,
                        message = "LIVE INVENTORY UPDATE: ${item.product.title} stock reduced to $updatedStock units after order $orderId.",
                        timestamp = System.currentTimeMillis(),
                        isLowStock = true
                    )
                )
            }
        }

        val itemsSummary = items.joinToString(" + ") { "${it.quantity}x ${it.product.title}" }

        val orderEntity = OrderEntity(
            orderId = orderId,
            dateMillis = System.currentTimeMillis(),
            status = OrderStatus.CONFIRMED.name,
            totalAmount = totalAmount,
            itemsSummary = itemsSummary,
            itemsCount = items.sumOf { it.quantity },
            paymentMethod = paymentMethodName,
            paymentTxnId = txnId,
            shippingAddress = shippingAddress,
            trackingNumber = trackingNo,
            estimatedDelivery = "Guaranteed Delivery in 2 Business Days",
            currentStepIndex = 0
        )
        orderDao.insertOrder(orderEntity)

        // Clear cart
        cartDao.clearCart()

        return orderEntity.toModel()
    }

    private fun ProductEntity.toModel(): Product {
        return Product(
            id = id,
            title = title,
            category = category,
            subcategory = subcategory,
            price = price,
            originalPrice = originalPrice,
            rating = rating,
            reviewCount = reviewCount,
            description = description,
            specifications = specificationsCsv.split(",").map { it.trim() },
            compatibility = compatibility,
            stock = stock,
            initialStock = initialStock,
            isFeatured = isFeatured,
            isBestSeller = isBestSeller,
            badge = badge,
            imageResName = imageResName,
            colors = colorsCsv.split(",").map { it.trim() },
            warrantyMonths = warrantyMonths
        )
    }

    private fun OrderEntity.toModel(): Order {
        val statusEnum = try {
            OrderStatus.valueOf(status)
        } catch (e: Exception) {
            OrderStatus.CONFIRMED
        }
        return Order(
            orderId = orderId,
            dateMillis = dateMillis,
            status = statusEnum,
            totalAmount = totalAmount,
            itemsSummary = itemsSummary,
            itemsCount = itemsCount,
            paymentMethod = paymentMethod,
            paymentTxnId = paymentTxnId,
            shippingAddress = shippingAddress,
            trackingNumber = trackingNumber,
            estimatedDelivery = estimatedDelivery,
            currentStepIndex = currentStepIndex
        )
    }
}
