package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.data.model.GuideEntity
import com.example.ui.components.RatingBadge
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.SandstoneRed
import com.example.ui.theme.SunsetOrange
import com.example.ui.viewmodel.GandikotaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesEventsScreen(
    viewModel: GandikotaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val guides by viewModel.allGuides.collectAsState()
    val events by viewModel.allEvents.collectAsState()
    val bookingGuide by viewModel.bookingGuide.collectAsState()
    val bookingEvent by viewModel.bookingEvent.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 72.dp)
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Tour Guides (${guides.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Local Events (${events.size})", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 0) {
            // Tour Guides List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(guides, key = { it.id }) { guide ->
                    GuideCard(
                        guide = guide,
                        onBookClick = { viewModel.startGuideBooking(guide) }
                    )
                }
            }
        } else {
            // Local Events List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        onBookClick = { viewModel.startEventBooking(event) }
                    )
                }
            }
        }
    }

    // Guide Booking Sheet
    bookingGuide?.let { guide ->
        GuideBookingSheet(
            guide = guide,
            defaultName = userProfile?.name ?: "A. Manoj Kumar",
            defaultPhone = userProfile?.phone ?: "+91 98765 43210",
            onDismiss = { viewModel.startGuideBooking(null) },
            onConfirmBooking = { name, phone, date, slot, partySize ->
                viewModel.submitGuideBooking(
                    guide = guide,
                    touristName = name,
                    touristPhone = phone,
                    date = date,
                    slot = slot,
                    tourType = guide.specialty,
                    guestsCount = partySize
                )
            }
        )
    }

    // Event Booking Sheet
    bookingEvent?.let { event ->
        EventBookingSheet(
            event = event,
            defaultName = userProfile?.name ?: "A. Manoj Kumar",
            defaultPhone = userProfile?.phone ?: "+91 98765 43210",
            onDismiss = { viewModel.startEventBooking(null) },
            onConfirmBooking = { name, phone, tickets ->
                viewModel.submitEventBooking(
                    event = event,
                    userName = name,
                    userPhone = phone,
                    ticketsCount = tickets
                )
            }
        )
    }
}

@Composable
fun GuideCard(
    guide: GuideEntity,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guide_card_${guide.id}"),
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
                    color = SandstoneRed,
                    shape = CircleShape,
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = guide.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = ForestGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = guide.specialty,
                        fontSize = 12.sp,
                        color = SandstoneRed,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${guide.experienceYears} Years Experience • ${guide.badge}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RatingBadge(rating = guide.rating, reviewsCount = guide.reviewsCount)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = SunsetOrange,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Languages: ${guide.languages}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Slots: ${guide.availableSlots}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tour Fee", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${guide.pricePerTour} / group", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = SandstoneRed)
                }
                Button(
                    onClick = onBookClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("book_guide_button_${guide.id}")
                ) {
                    Text("Hire Guide", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: EventEntity,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ForestGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = event.category,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "₹${event.price} / ticket",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = SunsetOrange
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${event.date} • ${event.time}", fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = SandstoneRed, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.meetingPoint, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${event.availableSeats} of ${event.totalSeats} seats open",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (event.availableSeats <= 5) Color(0xFFC62828) else ForestGreen
                    )
                    Text("Duration: ${event.duration}", fontSize = 10.sp, color = Color.Gray)
                }
                Button(
                    onClick = onBookClick,
                    enabled = event.availableSeats > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("book_event_button_${event.id}")
                ) {
                    Text(if (event.availableSeats > 0) "Register Seats" else "Sold Out", fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideBookingSheet(
    guide: GuideEntity,
    defaultName: String,
    defaultPhone: String,
    onDismiss: () -> Unit,
    onConfirmBooking: (name: String, phone: String, date: String, slot: String, partySize: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(defaultName) }
    var phone by remember { mutableStateOf(defaultPhone) }
    var partySize by remember { mutableIntStateOf(2) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val cal = Calendar.getInstance()
    val today = dateFormat.format(cal.time)
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrow = dateFormat.format(cal.time)

    var selectedDate by remember { mutableStateOf(today) }
    val slots = listOf("5:30 AM Sunrise Tour", "9:30 AM Fort Walk", "4:30 PM Sunset Gorge Trail")
    var selectedSlot by remember { mutableStateOf(slots[0]) }

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
                    Text("Hire Local Guide", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SandstoneRed))
                    Text(guide.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Select Date", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(today to "Today", tomorrow to "Tomorrow").forEach { (d, label) ->
                    val isSelected = selectedDate == d
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDate = d },
                        label = { Text("$label ($d)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SandstoneRed, selectedLabelColor = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Select Time Slot", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                slots.forEach { slot ->
                    val isSelected = selectedSlot == slot
                    Surface(
                        color = if (isSelected) SunsetOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSlot = slot }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isSelected) SunsetOrange else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = slot,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SunsetOrange else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Party Size", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (partySize > 1) partySize-- }) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                    }
                    Text("$partySize People", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { if (partySize < 15) partySize++ }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Lead Traveler Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Mobile Contact") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Guide Fee:", fontWeight = FontWeight.Bold)
                    Text("₹${guide.pricePerTour}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = SandstoneRed)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onConfirmBooking(
                        name.ifBlank { "Tourist" },
                        phone.ifBlank { "+91 9999999999" },
                        selectedDate,
                        selectedSlot,
                        partySize
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_guide_booking_button")
            ) {
                Text("Confirm Guide Reservation", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventBookingSheet(
    event: EventEntity,
    defaultName: String,
    defaultPhone: String,
    onDismiss: () -> Unit,
    onConfirmBooking: (name: String, phone: String, tickets: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(defaultName) }
    var phone by remember { mutableStateOf(defaultPhone) }
    var tickets by remember { mutableIntStateOf(1) }

    val total = tickets * event.price

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
                    Text("Event Registration", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = ForestGreen))
                    Text(event.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Number of Passes", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (tickets > 1) tickets-- }) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                    }
                    Text("$tickets Pass(es)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    IconButton(onClick = { if (tickets < event.availableSeats) tickets++ }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Attendee Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Contact Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Payment Due ($tickets x ₹${event.price}):", fontWeight = FontWeight.Bold)
                    Text("₹$total", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = ForestGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onConfirmBooking(
                        name.ifBlank { "Attendee" },
                        phone.ifBlank { "+91 9999999999" },
                        tickets
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_event_booking_button")
            ) {
                Text("Confirm & Reserve Seats (₹$total)", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
