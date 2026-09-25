package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.components.AsiaHouseBottomNav
import com.example.ui.components.AsiaHouseTopBar
import com.example.ui.components.MainNavTab
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    private val shopViewModel: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AsiaHouseApp(viewModel = shopViewModel)
            }
        }
    }
}

@Composable
fun AsiaHouseApp(viewModel: ShopViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val alerts by viewModel.inventoryAlerts.collectAsState()
    val showAlertsDialog by viewModel.showInventoryAlertsSheet.collectAsState()

    val cartCount = cartItems.sumOf { it.quantity }
    val activeOrdersCount = orders.count { it.currentStepIndex < 4 }

    val activeNavTab = when (currentScreen) {
        ScreenDestination.Home -> MainNavTab.STORE
        ScreenDestination.Catalog -> MainNavTab.CATALOG
        ScreenDestination.Tracking -> MainNavTab.TRACKING
        ScreenDestination.Dashboard -> MainNavTab.DASHBOARD
        else -> null
    }

    if (showAlertsDialog) {
        InventoryAlertsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setInventoryAlertsSheetVisible(false) }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Show custom top bar on main tabs
            if (activeNavTab != null || currentScreen is ScreenDestination.Cart) {
                AsiaHouseTopBar(
                    cartCount = cartCount,
                    alertCount = alerts.size,
                    onCartClick = { viewModel.navigateTo(ScreenDestination.Cart) },
                    onAlertsClick = { viewModel.setInventoryAlertsSheetVisible(true) },
                    onSearchClick = { viewModel.navigateTo(ScreenDestination.Catalog) },
                    title = when (currentScreen) {
                        ScreenDestination.Cart -> "Shopping Cart"
                        ScreenDestination.Dashboard -> "User Dashboard"
                        ScreenDestination.Tracking -> "Live Tracking"
                        ScreenDestination.Catalog -> "Asia House Catalog"
                        else -> "Asia House"
                    }
                )
            }
        },
        bottomBar = {
            if (activeNavTab != null) {
                AsiaHouseBottomNav(
                    selectedTab = activeNavTab,
                    onTabSelected = { tab ->
                        when (tab) {
                            MainNavTab.STORE -> viewModel.navigateTo(ScreenDestination.Home)
                            MainNavTab.CATALOG -> viewModel.navigateTo(ScreenDestination.Catalog)
                            MainNavTab.TRACKING -> viewModel.navigateTo(ScreenDestination.Tracking)
                            MainNavTab.DASHBOARD -> viewModel.navigateTo(ScreenDestination.Dashboard)
                        }
                    },
                    activeOrdersCount = activeOrdersCount
                )
            }
        }
    ) { innerPadding ->
        when (val screen = currentScreen) {
            ScreenDestination.Home -> HomeScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            ScreenDestination.Catalog -> CatalogScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            ScreenDestination.Tracking -> OrderTrackingScreen(
                targetOrderId = null,
                viewModel = viewModel,
                onBack = { viewModel.navigateTo(ScreenDestination.Home) },
                modifier = Modifier.padding(innerPadding)
            )
            ScreenDestination.Dashboard -> DashboardScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            is ScreenDestination.ProductDetail -> ProductDetailScreen(
                productId = screen.productId,
                viewModel = viewModel,
                onBack = { viewModel.navigateTo(ScreenDestination.Home) },
                modifier = Modifier.padding(innerPadding)
            )
            ScreenDestination.Cart -> CartScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            is ScreenDestination.OrderDetail -> OrderTrackingScreen(
                targetOrderId = screen.orderId,
                viewModel = viewModel,
                onBack = { viewModel.navigateTo(ScreenDestination.Dashboard) },
                modifier = Modifier.padding(innerPadding)
            )
            is ScreenDestination.OrderSuccess -> OrderSuccessScreen(
                order = screen.order,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
