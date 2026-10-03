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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.TouristPlaceEntity
import com.example.ui.components.RatingBadge
import com.example.ui.components.getDrawableResourceByName
import com.example.ui.components.launchGoogleMaps
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.SandstoneRed
import com.example.ui.theme.SunsetOrange
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GandikotaViewModel

@Composable
fun HomeScreen(
    viewModel: GandikotaViewModel,
    modifier: Modifier = Modifier
) {
    val places by viewModel.allPlaces.collectAsState()
    val events by viewModel.allEvents.collectAsState()
    val context = LocalContext.current
    var quickSearch by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // --- 1. Hero Canyon Banner ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            AsyncImage(
                model = R.drawable.gandikota_canyon_hero_1791048159112,
                contentDescription = "Gandikota Grand Canyon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Surface(
                    color = SunsetOrange,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "THE GRAND CANYON OF INDIA",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Gandikota Gorge & Fort",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Perched on 300-ft red sandstone cliffs carved by the sacred Pennar River",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val canyon = places.find { it.id == "place_gandikota_canyon" }
                            if (canyon != null) viewModel.selectPlace(canyon)
                            else viewModel.navigateTo(AppScreen.PLACES)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("explore_canyon_button")
                    ) {
                        Text("Explore Gorge", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.ROOMS) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("quick_book_stay_button")
                    ) {
                        Text("Book Stay", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // --- 2. Search & Quick Actions ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = quickSearch,
                    onValueChange = {
                        quickSearch = it
                        viewModel.setSearch(it)
                    },
                    placeholder = { Text("Search gorge, fort, temples, stays...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SandstoneRed) },
                    trailingIcon = {
                        if (quickSearch.isNotBlank()) {
                            IconButton(onClick = {
                                quickSearch = ""
                                viewModel.setSearch("")
                            }) {
                                Icon(Icons.Default.Explore, contentDescription = "Go", tint = SunsetOrange)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick booking options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickCategoryButton(
                        icon = Icons.Default.Hotel,
                        label = "Room Stay",
                        color = SandstoneRed,
                        onClick = { viewModel.navigateTo(AppScreen.ROOMS) }
                    )
                    QuickCategoryButton(
                        icon = Icons.Default.Event,
                        label = "Tour Guides",
                        color = SunsetOrange,
                        onClick = { viewModel.navigateTo(AppScreen.GUIDES_EVENTS) }
                    )
                    QuickCategoryButton(
                        icon = Icons.Default.CameraAlt,
                        label = "Experiences",
                        color = ForestGreen,
                        onClick = { viewModel.navigateTo(AppScreen.GUIDES_EVENTS) }
                    )
                    QuickCategoryButton(
                        icon = Icons.Default.Route,
                        label = "Trip Plan",
                        color = Color(0xFF6D4C41),
                        onClick = { viewModel.navigateTo(AppScreen.PLANNER) }
                    )
                }
            }
        }

        // --- 3. Weather & Sunrise / Sunset Timing Strip ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = SunsetOrange,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Today's Canyon Weather", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF4E342E))
                        Text("24°C • Crisp Dry Breeze • Oct-Mar Peak Season", fontSize = 11.sp, color = Color(0xFF795548))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Sunrise: 5:45 AM", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SunsetOrange)
                    Text("Sunset: 6:15 PM", fontSize = 11.sp, color = Color(0xFF5D4037))
                }
            }
        }

        // --- 4. Popular Destinations Section ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Popular Destinations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Ancient citadels, sacred shrines & canyons",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = { viewModel.navigateTo(AppScreen.PLACES) }) {
                Text("View All", color = SandstoneRed, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SandstoneRed, modifier = Modifier.size(18.dp))
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(places.filter { !it.isHyderabad }.take(5)) { place ->
                DestinationCard(
                    place = place,
                    onClick = { viewModel.selectPlace(place) },
                    onNavigateMaps = { launchGoogleMaps(context, place.latitude, place.longitude, place.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 5. Upcoming Experiences & Tours Banner ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Upcoming Tours & Events",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sunrise photo expeditions & lantern walks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = { viewModel.navigateTo(AppScreen.GUIDES_EVENTS) }) {
                Text("See All", color = ForestGreen, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                EventHighlightCard(
                    event = event,
                    onBook = {
                        viewModel.startEventBooking(event)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 6. Transit & Gateway: Hyderabad / Charminar Feature ---
        // (Requirement: List Charminar separately as a Hyderabad destination)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = SandstoneRed,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flight,
                        contentDescription = "Flight Gateway",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HYDERABAD TRANSIT GATEWAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SandstoneRed
                        )
                    }
                    Text(
                        text = "Charminar & Hyderabad Hub",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Flying into Hyderabad? Explore iconic Charminar and transit to Gandikota (370 km via NH44).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                IconButton(onClick = {
                    val charminar = places.find { it.isHyderabad }
                    if (charminar != null) viewModel.selectPlace(charminar)
                    else viewModel.navigateTo(AppScreen.PLACES)
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "View Charminar Details", tint = SandstoneRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 7. Travel Guidelines Accordion ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Gandikota Visitor Tips",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Best vantage points: Eastern cliff ledge near Jumma Masjid at 5:45 AM sunrise.", fontSize = 12.sp, color = Color(0xFF33691E))
                Text("• Stay options: Haritha Resort inside fort or cliffside tents for stargazing.", fontSize = 12.sp, color = Color(0xFF33691E))
                Text("• Footwear: Wear sturdy grip hiking shoes; canyon rocks can be sharp.", fontSize = 12.sp, color = Color(0xFF33691E))
            }
        }
    }
}

@Composable
fun QuickCategoryButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Surface(
            color = color.copy(alpha = 0.12f),
            shape = CircleShape,
            modifier = Modifier.size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun DestinationCard(
    place: TouristPlaceEntity,
    onClick: () -> Unit,
    onNavigateMaps: () -> Unit
) {
    val context = LocalContext.current
    val drawableRes = getDrawableResourceByName(context, place.imageResName)

    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = drawableRes,
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = SandstoneRed,
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = place.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = place.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = place.locationName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingBadge(rating = place.rating, reviewsCount = 0)
                    IconButton(
                        onClick = onNavigateMaps,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = "Map Directions",
                            tint = SandstoneRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventHighlightCard(
    event: com.example.data.model.EventEntity,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ForestGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = event.category,
                        color = ForestGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "₹${event.price}/person",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = SunsetOrange
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${event.date} • ${event.time}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${event.availableSeats} seats left",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (event.availableSeats <= 5) Color(0xFFC62828) else ForestGreen
                )
                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Book", fontSize = 11.sp)
                }
            }
        }
    }
}
