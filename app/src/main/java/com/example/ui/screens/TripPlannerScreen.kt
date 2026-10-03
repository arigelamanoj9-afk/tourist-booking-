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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TouristPlaceEntity
import com.example.ui.components.launchGoogleMapsRoute
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.SandstoneRed
import com.example.ui.theme.SunsetOrange
import com.example.ui.viewmodel.GandikotaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(
    viewModel: GandikotaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedPlanTab by remember { mutableIntStateOf(0) }
    val places by viewModel.allPlaces.collectAsState()
    val savedPlans by viewModel.allTripPlans.collectAsState()
    val selectedPlacesIds by viewModel.selectedPlannerPlaces.collectAsState()
    val context = LocalContext.current

    var customTitle by remember { mutableStateOf("My Gandikota Expedition") }
    var customDuration by remember { mutableIntStateOf(1) }
    var customNotes by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 72.dp)
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedPlanTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedPlanTab == 0,
                onClick = { selectedPlanTab = 0 },
                text = { Text("1-Day Express", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedPlanTab == 1,
                onClick = { selectedPlanTab = 1 },
                text = { Text("2-Day Explorer", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedPlanTab == 2,
                onClick = { selectedPlanTab = 2 },
                text = { Text("Custom Builder", fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedPlanTab) {
            0 -> {
                // 1-Day Express Itinerary
                OneDayItineraryView(
                    places = places,
                    onOpenMapRoute = {
                        val routePlaces = places.filter {
                            it.id in listOf(
                                "place_sunrise_viewpoint",
                                "place_gandikota_canyon",
                                "place_madhavaraya_temple",
                                "place_jumma_masjid",
                                "place_historic_jail",
                                "place_mylavaram_dam"
                            )
                        }
                        launchGoogleMapsRoute(context, routePlaces)
                    }
                )
            }
            1 -> {
                // 2-Day Adventure Itinerary
                TwoDayItineraryView(
                    places = places,
                    onOpenMapRoute = {
                        val routePlaces = places.filter {
                            it.id in listOf(
                                "place_gandikota_canyon",
                                "place_gandikota_fort",
                                "place_owk_waterfalls",
                                "place_charminar_hyderabad"
                            )
                        }
                        launchGoogleMapsRoute(context, routePlaces)
                    }
                )
            }
            2 -> {
                // Custom Itinerary Builder
                CustomPlannerView(
                    places = places,
                    selectedPlaceIds = selectedPlacesIds,
                    onTogglePlace = { viewModel.togglePlannerPlace(it) },
                    title = customTitle,
                    onTitleChange = { customTitle = it },
                    duration = customDuration,
                    onDurationChange = { customDuration = it },
                    notes = customNotes,
                    onNotesChange = { customNotes = it },
                    onSavePlan = {
                        viewModel.saveCustomTripPlan(customTitle, customDuration, customNotes)
                    },
                    savedPlans = savedPlans,
                    onDeletePlan = { viewModel.deleteTripPlan(it) },
                    onOpenMapRoute = {
                        val selectedList = places.filter { it.id in selectedPlacesIds }
                        launchGoogleMapsRoute(context, selectedList)
                    }
                )
            }
        }
    }
}

@Composable
fun OneDayItineraryView(
    places: List<TouristPlaceEntity>,
    onOpenMapRoute: () -> Unit
) {
    val items = listOf(
        "5:30 AM – 7:00 AM" to ("Pennar River Sunrise Viewpoint" to "Watch the first morning rays illuminate the 300ft gorge into brilliant red amber."),
        "7:30 AM – 8:30 AM" to ("Breakfast at Haritha Resort" to "Enjoy piping hot Andhra idli, vada, and local filter coffee."),
        "8:45 AM – 11:00 AM" to ("Gandikota Fort & Citadel Exploration" to "Traverse the 12th-century stone bastions, watchtowers, and cannon viewpoints."),
        "11:15 AM – 12:45 PM" to ("Madhavaraya Temple & Sculptures" to "Marvel at the 16th-century Vijayanagara gopuram with high-relief deity friezes."),
        "1:00 PM – 2:00 PM" to ("Rayalaseema Lunch" to "Authentic village thali with ragi mudda and spicy curries."),
        "2:15 PM – 3:45 PM" to ("Jumma Masjid & Royal Granary" to "Explore symmetrical Islamic prayer arches and the subterranean historic prison."),
        "4:00 PM – 5:45 PM" to ("Mylavaram Dam & Speedboating" to "Short 8 km drive for refreshing breeze, boating, and scenic sunset lake waters."),
        "6:30 PM – 8:30 PM" to ("Cliffside Campfire & Dinner" to "End the evening by the gorge with hot tea, bonfire, and star gazing.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SandstoneRed.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "1-Day Express Canyon & Citadel Circuit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SandstoneRed
                )
                Text(
                    text = "Ideal for weekend travelers covering all essential gorge vantage points and historic monuments.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenMapRoute,
                    colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("open_1day_maps_route")
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open 1-Day Route in Google Maps")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hour-by-Hour Timeline",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        items.forEachIndexed { index, (time, placeInfo) ->
            TimelineItem(
                time = time,
                title = placeInfo.first,
                description = placeInfo.second,
                isLast = index == items.size - 1
            )
        }
    }
}

@Composable
fun TwoDayItineraryView(
    places: List<TouristPlaceEntity>,
    onOpenMapRoute: () -> Unit
) {
    val day1 = listOf(
        "Day 1 • 6:00 AM" to ("Arrive at Gandikota & Check into Campsite/Resort" to "Set up tents or check into Haritha Resort."),
        "Day 1 • 8:00 AM" to ("Complete Fort Citadel & Temples Tour" to "Visit Madhavaraya temple, granary, and royal prison vaults."),
        "Day 1 • 1:00 PM" to ("Lunch & Siesta" to "Rest during peak noon sun."),
        "Day 1 • 3:30 PM" to ("Pennar River Kayaking & Descent" to "Trek down the canyon to paddle on the calm turquoise river water."),
        "Day 1 • 7:30 PM" to ("Acoustic Bonfire & Deep Sky Stargazing" to "Zero light pollution view of the Milky Way and constellations.")
    )

    val day2 = listOf(
        "Day 2 • 5:45 AM" to ("Eastern Sunrise Point & Photography" to "Capture the dramatic dawn light reflecting on the gorge."),
        "Day 2 • 8:30 AM" to ("Breakfast & Check-out" to "Savor local breakfast."),
        "Day 2 • 9:30 AM" to ("Drive to Owk Waterfalls (38 km)" to "Explore rocky cascade and natural freshwater plunge pools."),
        "Day 2 • 12:30 PM" to ("Belum Caves Exploration (Optional nearby)" to "India's second longest cave system with stalactite formations."),
        "Day 2 • 3:30 PM" to ("Hyderabad Transit & Charminar Gateway" to "Proceed on NH44 highway back to Hyderabad airport/rail hub with evening stroll at Charminar.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SunsetOrange.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "2-Day Grand Canyon & Rayalaseema Trail",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SunsetOrange
                )
                Text(
                    text = "Comprehensive tour including gorge trekking, waterfalls, camping under stars, and Hyderabad transit.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenMapRoute,
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("open_2day_maps_route")
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open 2-Day Route in Google Maps")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Day 1: Gandikota Gorge & Stargazing", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SandstoneRed)
        Spacer(modifier = Modifier.height(8.dp))
        day1.forEachIndexed { i, (time, info) ->
            TimelineItem(time = time, title = info.first, description = info.second, isLast = i == day1.size - 1)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Day 2: Waterfalls & Hyderabad Gateway", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ForestGreen)
        Spacer(modifier = Modifier.height(8.dp))
        day2.forEachIndexed { i, (time, info) ->
            TimelineItem(time = time, title = info.first, description = info.second, isLast = i == day2.size - 1)
        }
    }
}

@Composable
fun CustomPlannerView(
    places: List<TouristPlaceEntity>,
    selectedPlaceIds: Set<String>,
    onTogglePlace: (String) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    duration: Int,
    onDurationChange: (Int) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSavePlan: () -> Unit,
    savedPlans: List<com.example.data.model.TripPlanEntity>,
    onDeletePlan: (String) -> Unit,
    onOpenMapRoute: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Create Custom Itinerary", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Trip Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "1-Day Trip", 2 to "2-Day Trip").forEach { (d, label) ->
                FilterChip(
                    selected = duration == d,
                    onClick = { onDurationChange(d) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SandstoneRed, selectedLabelColor = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Select Destinations (${selectedPlaceIds.size} selected):",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        places.forEach { place ->
            val isChecked = selectedPlaceIds.contains(place.id)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onTogglePlace(place.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) SandstoneRed.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onTogglePlace(place.id) },
                        colors = CheckboxDefaults.colors(checkedColor = SandstoneRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(place.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${place.category} • ${place.timings}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Trip Notes (e.g. carry sunscreen, start at 5:30 AM)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onOpenMapRoute,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Map Route", fontSize = 12.sp)
            }
            Button(
                onClick = onSavePlan,
                colors = ButtonDefaults.buttonColors(containerColor = SandstoneRed),
                modifier = Modifier.weight(1f).testTag("save_itinerary_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Plan", fontSize = 12.sp)
            }
        }

        // Saved Itineraries
        if (savedPlans.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Your Saved Itineraries", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            savedPlans.forEach { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(plan.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${plan.durationDays}-Day Trip • ${plan.selectedPlaceIds.split(",").size} destinations", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (plan.notes.isNotBlank()) {
                                Text("Note: ${plan.notes}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        IconButton(onClick = { onDeletePlan(plan.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineItem(
    time: String,
    title: String,
    description: String,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = SandstoneRed,
                shape = CircleShape,
                modifier = Modifier.size(12.dp)
            ) {}
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(64.dp)
                        .background(SandstoneRed.copy(alpha = 0.3f))
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(time, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SunsetOrange)
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
        }
    }
}
