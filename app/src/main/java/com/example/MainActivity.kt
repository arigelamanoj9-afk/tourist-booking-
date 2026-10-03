package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BookingMessageDialog
import com.example.ui.components.GandikotaBottomNavigation
import com.example.ui.components.GandikotaTopAppBar
import com.example.ui.components.PlaceDetailBottomSheet
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.GuidesEventsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlacesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoomsScreen
import com.example.ui.screens.TripPlannerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GandikotaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GandikotaApp()
            }
        }
    }
}

@Composable
fun GandikotaApp(
    viewModel: GandikotaViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()
    val bookingMessage by viewModel.bookingMessage.collectAsState()

    val isAdmin = userProfile?.role == "ADMIN"

    // BackHandler: Handle back navigation cleanly
    BackHandler(enabled = selectedPlace != null || currentScreen != AppScreen.HOME) {
        if (selectedPlace != null) {
            viewModel.selectPlace(null)
        } else {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != AppScreen.ADMIN) {
                GandikotaTopAppBar(
                    currentScreen = currentScreen,
                    isAdmin = isAdmin,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.ADMIN) {
                GandikotaBottomNavigation(
                    currentScreen = currentScreen,
                    onSelectScreen = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        when (currentScreen) {
            AppScreen.HOME -> HomeScreen(viewModel = viewModel, modifier = screenModifier)
            AppScreen.PLACES -> PlacesScreen(viewModel = viewModel, modifier = screenModifier)
            AppScreen.ROOMS -> RoomsScreen(viewModel = viewModel, modifier = screenModifier)
            AppScreen.GUIDES_EVENTS -> GuidesEventsScreen(viewModel = viewModel, modifier = screenModifier)
            AppScreen.PLANNER -> TripPlannerScreen(viewModel = viewModel, modifier = screenModifier)
            AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel, modifier = screenModifier)
            AppScreen.ADMIN -> AdminScreen(viewModel = viewModel, modifier = screenModifier)
        }

        // Place Detail Sheet
        selectedPlace?.let { place ->
            PlaceDetailBottomSheet(
                place = place,
                onDismiss = { viewModel.selectPlace(null) },
                onToggleFavorite = { viewModel.toggleFavorite(place) }
            )
        }

        // Booking confirmation / error feedback dialog
        bookingMessage?.let { msg ->
            BookingMessageDialog(
                message = msg,
                onDismiss = { viewModel.clearBookingMessage() }
            )
        }
    }
}
