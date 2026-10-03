package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.RoomEntity
import com.example.ui.components.RatingBadge
import com.example.ui.components.getDrawableResourceByName
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.SandstoneRed
import com.example.ui.theme.SunsetOrange
import com.example.ui.viewmodel.GandikotaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomsScreen(
    viewModel: GandikotaViewModel,
    modifier: Modifier = Modifier
) {
    val rooms by viewModel.allRooms.collectAsState()
    val bookingRoom by viewModel.bookingRoom.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedType by remember { mutableStateOf("All") }
    val propertyTypes = listOf("All", "Resort", "Camping & Tents", "Homestay", "Hotel")

    val filteredRooms = rooms.filter { room ->
        selectedType == "All" || room.propertyType.equals(selectedType, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 72.dp)
    ) {
        // Header & Type Filters
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Canyon Stays & Camping",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = SandstoneRed
                )
            )
            Text(
                text = "Resorts, eco cliff tents, and traditional Rayalaseema homestays",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(propertyTypes) { type ->
                    val isSelected = selectedType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedType = type },
                        label = {
                            Text(
                                text = type,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SandstoneRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Rooms List
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredRooms, key = { it.id }) { room ->
                RoomCard(
                    room = room,
                    onBookClick = { viewModel.startRoomBooking(room) }
                )
            }
        }
    }

    // Interactive Room Booking Sheet
    bookingRoom?.let { room ->
        RoomBookingSheet(
            room = room,
            defaultName = userProfile?.name ?: "A. Manoj Kumar",
            defaultEmail = userProfile?.email ?: "arigelamanoj9@gmail.com",
            defaultPhone = userProfile?.phone ?: "+91 98765 43210",
            onDismiss = { viewModel.startRoomBooking(null) },
            onConfirmBooking = { name, email, phone, checkIn, checkOut, guests, roomUnits, nights ->
                viewModel.submitRoomBooking(
                    room = room,
                    userName = name,
                    userEmail = email,
                    userPhone = phone,
                    checkIn = checkIn,
                    checkOut = checkOut,
                    guests = guests,
                    roomsCount = roomUnits,
                    nights = nights
                )
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomCard(
    room: RoomEntity,
    onBookClick: () -> Unit
) {
    val context = LocalContext.current
    val drawableRes = getDrawableResourceByName(context, room.imageResName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("room_card_${room.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = drawableRes,
                    contentDescription = room.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = SandstoneRed,
                    shape = RoundedCornerShape(bottomEnd = 10.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = room.propertyType,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "₹${room.pricePerNight} / night",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    RatingBadge(rating = room.rating, reviewsCount = room.reviewsCount)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SunsetOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.location,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Amenities Flow
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    room.amenities.split(",").take(4).forEach { amenity ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = amenity.trim(),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${room.availableRooms} rooms available",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreen
                            )
                        }
                        Text(
                            text = "Max ${room.maxGuests} guests / unit",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("book_button_${room.id}")
                    ) {
                        Text("Book Stay", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomBookingSheet(
    room: RoomEntity,
    defaultName: String,
    defaultEmail: String,
    defaultPhone: String,
    onDismiss: () -> Unit,
    onConfirmBooking: (name: String, email: String, phone: String, checkIn: String, checkOut: String, guests: Int, rooms: Int, nights: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Form state
    var name by remember { mutableStateOf(defaultName) }
    var email by remember { mutableStateOf(defaultEmail) }
    var phone by remember { mutableStateOf(defaultPhone) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displayFormat = remember { SimpleDateFormat("EEE, dd MMM", Locale.getDefault()) }

    var checkInOffsetDays by remember { mutableIntStateOf(0) } // 0 = today, 1 = tomorrow
    var nights by remember { mutableIntStateOf(1) }
    var guests by remember { mutableIntStateOf(2) }
    var roomUnits by remember { mutableIntStateOf(1) }

    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, checkInOffsetDays)
    val checkInDateStr = dateFormat.format(cal.time)
    val checkInDisplayStr = displayFormat.format(cal.time)

    cal.add(Calendar.DAY_OF_YEAR, nights)
    val checkOutDateStr = dateFormat.format(cal.time)
    val checkOutDisplayStr = displayFormat.format(cal.time)

    val totalPrice = room.pricePerNight * nights * roomUnits

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reserve Your Stay",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SandstoneRed
                        )
                    )
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Check-in quick selector
            Text("Select Check-in Date", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0 to "Today", 1 to "Tomorrow", 2 to "+2 Days", 5 to "Weekend").forEach { (offset, label) ->
                    val selected = checkInOffsetDays == offset
                    FilterChip(
                        selected = selected,
                        onClick = { checkInOffsetDays = offset },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SunsetOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dates Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Check-In", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(checkInDisplayStr, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SandstoneRed)
                        Text(checkInDateStr, fontSize = 10.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$nights Night(s)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SunsetOrange)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (nights > 1) nights-- },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { if (nights < 7) nights++ },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Check-Out", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(checkOutDisplayStr, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SandstoneRed)
                        Text(checkOutDateStr, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guests & Rooms Steppers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Guests Stepper
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Guests", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$guests Guests", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row {
                                IconButton(
                                    onClick = { if (guests > 1) guests-- },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                                IconButton(
                                    onClick = { if (guests < room.maxGuests * roomUnits) guests++ },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Rooms Stepper
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Rooms", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$roomUnits Unit(s)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row {
                                IconButton(
                                    onClick = { if (roomUnits > 1) roomUnits-- },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                                IconButton(
                                    onClick = { if (roomUnits < room.availableRooms) roomUnits++ },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Traveler Contact Inputs
            Text("Guest Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Primary Guest Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_guest_name")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("booking_guest_phone")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("booking_guest_email")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Breakdown Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Rate per night:", fontSize = 12.sp, color = Color(0xFF5D4037))
                        Text("₹${room.pricePerNight}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Calculation ($nights nights x $roomUnits room):", fontSize = 12.sp, color = Color(0xFF5D4037))
                        Text("₹$totalPrice", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount Due:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SandstoneRed)
                        Text("₹$totalPrice", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = SandstoneRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Confirm Button
            Button(
                onClick = {
                    onConfirmBooking(
                        name.ifBlank { "Guest" },
                        email.ifBlank { "guest@example.com" },
                        phone.ifBlank { "+91 9999999999" },
                        checkInDateStr,
                        checkOutDateStr,
                        guests,
                        roomUnits,
                        nights
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_room_booking_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Reservation (₹$totalPrice)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
