package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.SandstoneRed
import com.example.ui.theme.SunsetOrange
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GandikotaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: GandikotaViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val favorites by viewModel.favoritePlaces.collectAsState()
    val roomBookings by viewModel.allRoomBookings.collectAsState()
    val guideBookings by viewModel.allGuideBookings.collectAsState()
    val eventBookings by viewModel.allEventBookings.collectAsState()

    var showEditProfile by remember { mutableStateOf(false) }

    val name = userProfile?.name ?: "A. Manoj Kumar"
    val email = userProfile?.email ?: "arigelamanoj9@gmail.com"
    val phone = userProfile?.phone ?: "+91 98765 43210"
    val role = userProfile?.role ?: "TOURIST"
    val isAdmin = role == "ADMIN"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 72.dp)
    ) {
        // Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (isAdmin) SunsetOrange else SandstoneRed,
                        shape = CircleShape,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isAdmin) Icons.Default.SupervisorAccount else Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(text = email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { showEditProfile = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = SandstoneRed)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Role Toggle Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Admin Management Mode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isAdmin) "Active as Administrator" else "Standard Tourist view",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isAdmin,
                        onCheckedChange = { viewModel.toggleRole() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SunsetOrange),
                        modifier = Modifier.testTag("admin_role_switch")
                    )
                }

                if (isAdmin) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("go_to_admin_screen_button")
                    ) {
                        Icon(Icons.Default.SupervisorAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Admin Dashboard")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Favorites Collection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favorite Places (${favorites.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (favorites.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No saved favorites yet. Tap the bookmark icon on any tourist place to save it.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favorites) { place ->
                    DestinationCard(
                        place = place,
                        onClick = { viewModel.selectPlace(place) },
                        onNavigateMaps = { viewModel.selectPlace(place) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bookings History Section
        Text(
            text = "Booking History & Reservations",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Stay Bookings
        Text("Room & Tent Stays (${roomBookings.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SandstoneRed)
        Spacer(modifier = Modifier.height(6.dp))
        if (roomBookings.isEmpty()) {
            Text("No room reservations yet.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
        } else {
            roomBookings.forEach { booking ->
                BookingItemCard(
                    title = booking.roomName,
                    subtitle = "Dates: ${booking.checkInDate} to ${booking.checkOutDate} • ${booking.roomsCount} room(s)",
                    amount = booking.totalAmount,
                    bookingId = booking.id,
                    status = booking.status,
                    onCancel = { viewModel.cancelRoomBooking(booking.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Guide Bookings
        Text("Tour Guides Reserved (${guideBookings.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SunsetOrange)
        Spacer(modifier = Modifier.height(6.dp))
        if (guideBookings.isEmpty()) {
            Text("No guide reservations yet.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
        } else {
            guideBookings.forEach { booking ->
                BookingItemCard(
                    title = "Guide: ${booking.guideName}",
                    subtitle = "Date: ${booking.bookingDate} (${booking.timeSlot}) • Party: ${booking.guestsCount}",
                    amount = booking.totalAmount,
                    bookingId = booking.id,
                    status = booking.status,
                    onCancel = { viewModel.cancelGuideBooking(booking.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Event Bookings
        Text("Events & Experience Passes (${eventBookings.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreen)
        Spacer(modifier = Modifier.height(6.dp))
        if (eventBookings.isEmpty()) {
            Text("No event bookings yet.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
        } else {
            eventBookings.forEach { booking ->
                BookingItemCard(
                    title = booking.eventTitle,
                    subtitle = "Passes: ${booking.ticketsCount} tickets • Attendee: ${booking.userName}",
                    amount = booking.totalAmount,
                    bookingId = booking.id,
                    status = booking.status,
                    onCancel = { viewModel.cancelEventBooking(booking) }
                )
            }
        }
    }

    if (showEditProfile) {
        var editName by remember { mutableStateOf(name) }
        var editEmail by remember { mutableStateOf(email) }
        var editPhone by remember { mutableStateOf(phone) }

        AlertDialog(
            onDismissRequest = { showEditProfile = false },
            title = { Text("Edit Profile Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUser(editName, editEmail, editPhone, "English")
                        showEditProfile = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfile = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BookingItemCard(
    title: String,
    subtitle: String,
    amount: Int,
    bookingId: String,
    status: String,
    onCancel: () -> Unit
) {
    val isConfirmed = status == "CONFIRMED"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isConfirmed) ForestGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = status,
                        color = if (isConfirmed) ForestGreen else Color.Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text("Ref: $bookingId", fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Amount: ₹$amount", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = SandstoneRed)
                if (isConfirmed) {
                    OutlinedButton(
                        onClick = onCancel,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Cancel Booking", fontSize = 10.sp, color = Color.Red)
                    }
                }
            }
        }
    }
}
