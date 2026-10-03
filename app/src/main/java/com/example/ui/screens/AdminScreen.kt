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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.EventEntity
import com.example.data.model.RoomEntity
import com.example.data.model.TouristPlaceEntity
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.SandstoneRed
import com.example.ui.theme.SunsetOrange
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GandikotaViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: GandikotaViewModel,
    modifier: Modifier = Modifier
) {
    val places by viewModel.allPlaces.collectAsState()
    val rooms by viewModel.allRooms.collectAsState()
    val roomBookings by viewModel.allRoomBookings.collectAsState()
    val guideBookings by viewModel.allGuideBookings.collectAsState()
    val eventBookings by viewModel.allEventBookings.collectAsState()
    val events by viewModel.allEvents.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) }
    var showAddPlaceDialog by remember { mutableStateOf(false) }
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }

    val totalRevenue = roomBookings.filter { it.status == "CONFIRMED" }.sumOf { it.totalAmount } +
            guideBookings.filter { it.status == "CONFIRMED" }.sumOf { it.totalAmount } +
            eventBookings.filter { it.status == "CONFIRMED" }.sumOf { it.totalAmount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 72.dp)
    ) {
        // Admin Top Banner
        Surface(
            color = SunsetOrange,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text(
                        text = "Gandikota Tourism Admin Portal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Catalog management, booking approvals, & occupancy",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Overview Metric Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                label = "Total Revenue",
                value = "₹$totalRevenue",
                color = ForestGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Stay Bookings",
                value = "${roomBookings.size}",
                color = SandstoneRed,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Guides Hired",
                value = "${guideBookings.size}",
                color = SunsetOrange,
                modifier = Modifier.weight(1f)
            )
        }

        SecondaryTabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(selected = selectedAdminTab == 0, onClick = { selectedAdminTab = 0 }, text = { Text("Places") })
            Tab(selected = selectedAdminTab == 1, onClick = { selectedAdminTab = 1 }, text = { Text("Rooms") })
            Tab(selected = selectedAdminTab == 2, onClick = { selectedAdminTab = 2 }, text = { Text("Bookings") })
            Tab(selected = selectedAdminTab == 3, onClick = { selectedAdminTab = 3 }, text = { Text("Events") })
        }

        when (selectedAdminTab) {
            0 -> {
                // Manage Places
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tourist Places (${places.size})", fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { showAddPlaceDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                            modifier = Modifier.testTag("admin_add_place_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Place", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    places.forEach { place ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(place.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${place.category} • ${place.locationName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.adminDeletePlace(place.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Manage Rooms
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Stays & Campsites (${rooms.size})", fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { showAddRoomDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                            modifier = Modifier.testTag("admin_add_room_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Stay", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    rooms.forEach { room ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(room.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${room.propertyType} • ₹${room.pricePerNight}/night • ${room.availableRooms}/${room.totalRooms} available", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.adminDeleteRoom(room.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // All Bookings Log
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text("All Confirmed Guest Bookings", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (roomBookings.isEmpty() && guideBookings.isEmpty() && eventBookings.isEmpty()) {
                        Text("No bookings have been logged yet.", color = Color.Gray, fontSize = 12.sp)
                    }

                    roomBookings.forEach { booking ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("STAY: ${booking.roomName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SandstoneRed)
                                Text("Guest: ${booking.userName} (${booking.userPhone})", fontSize = 11.sp)
                                Text("Dates: ${booking.checkInDate} to ${booking.checkOutDate} • ₹${booking.totalAmount}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    guideBookings.forEach { booking ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("GUIDE: ${booking.guideName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SunsetOrange)
                                Text("Tourist: ${booking.touristName} (${booking.touristPhone})", fontSize = 11.sp)
                                Text("Date: ${booking.bookingDate} (${booking.timeSlot}) • ₹${booking.totalAmount}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
            3 -> {
                // Manage Events
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Events & Walks (${events.size})", fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { showAddEventDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier.testTag("admin_add_event_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Event", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    events.forEach { event ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(event.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${event.category} • ₹${event.price} • ${event.availableSeats}/${event.totalSeats} seats", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Place Dialog
    if (showAddPlaceDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Canyon & Gorge") }
        var desc by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("Gandikota, Kadapa District") }
        var fee by remember { mutableStateOf("Free entry") }

        AlertDialog(
            onDismissRequest = { showAddPlaceDialog = false },
            title = { Text("Publish New Tourist Place") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Place Name") }, singleLine = true)
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, singleLine = true)
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, singleLine = true)
                    OutlinedTextField(value = fee, onValueChange = { fee = it }, label = { Text("Entry Fee") }, singleLine = true)
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, maxLines = 3)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.adminAddPlace(
                                TouristPlaceEntity(
                                    id = "place_" + UUID.randomUUID().toString().take(6),
                                    name = name,
                                    category = category,
                                    shortDescription = desc.ifBlank { "Scenic destination in Gandikota." },
                                    fullDescription = desc.ifBlank { "Scenic travel destination with magnificent views." },
                                    locationName = location,
                                    latitude = 14.8149,
                                    longitude = 78.2863,
                                    timings = "6:00 AM - 6:00 PM",
                                    entryFee = fee,
                                    tips = "Wear comfortable walking shoes.",
                                    bestTime = "Early mornings",
                                    imageResName = "gandikota_canyon_hero_1791048159112",
                                    rating = 4.8f,
                                    reviewsCount = 10
                                )
                            )
                        }
                        showAddPlaceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed)
                ) {
                    Text("Publish Place")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPlaceDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Room Dialog
    if (showAddRoomDialog) {
        var roomName by remember { mutableStateOf("") }
        var propType by remember { mutableStateOf("Homestay") }
        var priceStr by remember { mutableStateOf("1500") }
        var totalStr by remember { mutableStateOf("5") }

        AlertDialog(
            onDismissRequest = { showAddRoomDialog = false },
            title = { Text("Add Room or Stay") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = roomName, onValueChange = { roomName = it }, label = { Text("Stay Title") }, singleLine = true)
                    OutlinedTextField(value = propType, onValueChange = { propType = it }, label = { Text("Property Type (Resort/Camping/Homestay)") }, singleLine = true)
                    OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Price per Night (₹)") }, singleLine = true)
                    OutlinedTextField(value = totalStr, onValueChange = { totalStr = it }, label = { Text("Total Room Units") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = priceStr.toIntOrNull() ?: 1500
                        val total = totalStr.toIntOrNull() ?: 5
                        if (roomName.isNotBlank()) {
                            viewModel.adminAddRoom(
                                RoomEntity(
                                    id = "room_" + UUID.randomUUID().toString().take(6),
                                    name = roomName,
                                    propertyType = propType,
                                    pricePerNight = price,
                                    rating = 4.6f,
                                    reviewsCount = 5,
                                    location = "Gandikota",
                                    description = "Comfortable stay near the canyon.",
                                    amenities = "AC, Hot Water, Breakfast, Wi-Fi",
                                    maxGuests = 3,
                                    totalRooms = total,
                                    availableRooms = total,
                                    imageResName = "gandikota_camping_1791048256119"
                                )
                            )
                        }
                        showAddRoomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed)
                ) {
                    Text("Add Stay")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRoomDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Event Dialog
    if (showAddEventDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Heritage Walk") }
        var priceStr by remember { mutableStateOf("399") }
        var seatsStr by remember { mutableStateOf("20") }

        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            title = { Text("Add Tourism Event") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Event Title") }, singleLine = true)
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, singleLine = true)
                    OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Ticket Price (₹)") }, singleLine = true)
                    OutlinedTextField(value = seatsStr, onValueChange = { seatsStr = it }, label = { Text("Available Seats") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = priceStr.toIntOrNull() ?: 399
                        val seats = seatsStr.toIntOrNull() ?: 20
                        if (title.isNotBlank()) {
                            viewModel.adminAddEvent(
                                EventEntity(
                                    id = "event_" + UUID.randomUUID().toString().take(6),
                                    title = title,
                                    category = category,
                                    date = "Coming Weekend",
                                    time = "6:00 AM - 8:00 AM",
                                    price = price,
                                    availableSeats = seats,
                                    totalSeats = seats,
                                    description = "Guided local experience in Gandikota.",
                                    meetingPoint = "Fort Main Gate",
                                    duration = "2 Hours"
                                )
                            )
                        }
                        showAddEventDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Publish Event")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEventDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}
