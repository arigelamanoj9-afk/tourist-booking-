package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GandikotaDatabase
import com.example.data.model.EventBookingEntity
import com.example.data.model.EventEntity
import com.example.data.model.GuideBookingEntity
import com.example.data.model.GuideEntity
import com.example.data.model.RoomBookingEntity
import com.example.data.model.RoomEntity
import com.example.data.model.TouristPlaceEntity
import com.example.data.model.TripPlanEntity
import com.example.data.model.UserEntity
import com.example.data.repository.GandikotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    PLACES,
    ROOMS,
    GUIDES_EVENTS,
    PLANNER,
    PROFILE,
    ADMIN
}

data class BookingMessage(
    val isSuccess: Boolean,
    val title: String,
    val message: String
)

class GandikotaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GandikotaRepository

    init {
        val database = GandikotaDatabase.getDatabase(application, viewModelScope)
        repository = GandikotaRepository(database.gandikotaDao())
    }

    // --- Navigation ---
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // --- Search & Filters ---
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")

    fun setSearch(query: String) {
        searchQuery.value = query
    }

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    // --- Places ---
    val allPlaces: StateFlow<List<TouristPlaceEntity>> = repository.allPlaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPlaces: StateFlow<List<TouristPlaceEntity>> = combine(
        allPlaces,
        searchQuery,
        selectedCategory
    ) { places, query, category ->
        places.filter { place ->
            val matchesQuery = query.isBlank() ||
                place.name.contains(query, ignoreCase = true) ||
                place.shortDescription.contains(query, ignoreCase = true) ||
                place.locationName.contains(query, ignoreCase = true)
            
            val matchesCategory = when (category) {
                "All" -> true
                "Hyderabad Gateway" -> place.isHyderabad
                else -> !place.isHyderabad && place.category.equals(category, ignoreCase = true)
            }
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritePlaces: StateFlow<List<TouristPlaceEntity>> = repository.favoritePlaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected place for bottom sheet detail
    private val _selectedPlace = MutableStateFlow<TouristPlaceEntity?>(null)
    val selectedPlace: StateFlow<TouristPlaceEntity?> = _selectedPlace.asStateFlow()

    fun selectPlace(place: TouristPlaceEntity?) {
        _selectedPlace.value = place
    }

    fun toggleFavorite(place: TouristPlaceEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(place.id, place.isFavorite)
        }
    }

    // --- Rooms & Room Bookings ---
    val allRooms: StateFlow<List<RoomEntity>> = repository.allRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRoomBookings: StateFlow<List<RoomBookingEntity>> = repository.allRoomBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room Booking dialog state
    private val _bookingRoom = MutableStateFlow<RoomEntity?>(null)
    val bookingRoom: StateFlow<RoomEntity?> = _bookingRoom.asStateFlow()

    fun startRoomBooking(room: RoomEntity?) {
        _bookingRoom.value = room
    }

    // Booking notification feedback
    private val _bookingMessage = MutableStateFlow<BookingMessage?>(null)
    val bookingMessage: StateFlow<BookingMessage?> = _bookingMessage.asStateFlow()

    fun clearBookingMessage() {
        _bookingMessage.value = null
    }

    fun submitRoomBooking(
        room: RoomEntity,
        userName: String,
        userEmail: String,
        userPhone: String,
        checkIn: String,
        checkOut: String,
        guests: Int,
        roomsCount: Int,
        nights: Int
    ) {
        viewModelScope.launch {
            val result = repository.bookRoom(
                roomId = room.id,
                roomName = room.name,
                userName = userName,
                userEmail = userEmail,
                userPhone = userPhone,
                checkInDate = checkIn,
                checkOutDate = checkOut,
                guestsCount = guests,
                roomsCount = roomsCount,
                pricePerNight = room.pricePerNight,
                nights = nights
            )
            result.onSuccess { booking ->
                _bookingRoom.value = null
                _bookingMessage.value = BookingMessage(
                    isSuccess = true,
                    title = "Stay Confirmed!",
                    message = "Booking ID: ${booking.id}\n${booking.roomName}\n$checkIn to $checkOut ($nights nights)\nTotal: ₹${booking.totalAmount}"
                )
            }.onFailure { err ->
                _bookingMessage.value = BookingMessage(
                    isSuccess = false,
                    title = "Booking Unavailable",
                    message = err.message ?: "Failed to complete reservation."
                )
            }
        }
    }

    fun cancelRoomBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelRoomBooking(bookingId)
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Booking Cancelled",
                message = "Room booking $bookingId has been cancelled."
            )
        }
    }

    // --- Tour Guides & Bookings ---
    val allGuides: StateFlow<List<GuideEntity>> = repository.allGuides
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGuideBookings: StateFlow<List<GuideBookingEntity>> = repository.allGuideBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _bookingGuide = MutableStateFlow<GuideEntity?>(null)
    val bookingGuide: StateFlow<GuideEntity?> = _bookingGuide.asStateFlow()

    fun startGuideBooking(guide: GuideEntity?) {
        _bookingGuide.value = guide
    }

    fun submitGuideBooking(
        guide: GuideEntity,
        touristName: String,
        touristPhone: String,
        date: String,
        slot: String,
        tourType: String,
        guestsCount: Int
    ) {
        viewModelScope.launch {
            val result = repository.bookGuide(
                guideId = guide.id,
                guideName = guide.name,
                touristName = touristName,
                touristPhone = touristPhone,
                date = date,
                slot = slot,
                tourType = tourType,
                guestsCount = guestsCount,
                totalAmount = guide.pricePerTour
            )
            result.onSuccess { booking ->
                _bookingGuide.value = null
                _bookingMessage.value = BookingMessage(
                    isSuccess = true,
                    title = "Tour Guide Reserved!",
                    message = "Booking ID: ${booking.id}\nGuide: ${booking.guideName}\nDate: ${booking.bookingDate} (${booking.timeSlot})\nPrice: ₹${booking.totalAmount}"
                )
            }.onFailure { err ->
                _bookingMessage.value = BookingMessage(
                    isSuccess = false,
                    title = "Slot Unavailable",
                    message = err.message ?: "Could not reserve guide."
                )
            }
        }
    }

    fun cancelGuideBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelGuideBooking(bookingId)
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Tour Cancelled",
                message = "Guide booking $bookingId has been cancelled."
            )
        }
    }

    // --- Events & Bookings ---
    val allEvents: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEventBookings: StateFlow<List<EventBookingEntity>> = repository.allEventBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _bookingEvent = MutableStateFlow<EventEntity?>(null)
    val bookingEvent: StateFlow<EventEntity?> = _bookingEvent.asStateFlow()

    fun startEventBooking(event: EventEntity?) {
        _bookingEvent.value = event
    }

    fun submitEventBooking(
        event: EventEntity,
        userName: String,
        userPhone: String,
        ticketsCount: Int
    ) {
        viewModelScope.launch {
            val result = repository.bookEvent(
                eventId = event.id,
                eventTitle = event.title,
                userName = userName,
                userPhone = userPhone,
                ticketsCount = ticketsCount,
                pricePerTicket = event.price
            )
            result.onSuccess { booking ->
                _bookingEvent.value = null
                _bookingMessage.value = BookingMessage(
                    isSuccess = true,
                    title = "Event Passes Reserved!",
                    message = "Booking ID: ${booking.id}\n${booking.eventTitle}\nTickets: ${booking.ticketsCount} | Total: ₹${booking.totalAmount}"
                )
            }.onFailure { err ->
                _bookingMessage.value = BookingMessage(
                    isSuccess = false,
                    title = "Booking Failed",
                    message = err.message ?: "Could not reserve event tickets."
                )
            }
        }
    }

    fun cancelEventBooking(booking: EventBookingEntity) {
        viewModelScope.launch {
            repository.cancelEventBooking(booking.id, booking.eventId, booking.ticketsCount)
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Event Cancelled",
                message = "Tickets for ${booking.eventTitle} cancelled. Seats released."
            )
        }
    }

    // --- Trip Planner ---
    val allTripPlans: StateFlow<List<TripPlanEntity>> = repository.allTripPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPlannerPlaces = MutableStateFlow<Set<String>>(
        setOf("place_gandikota_canyon", "place_gandikota_fort", "place_madhavaraya_temple", "place_jumma_masjid")
    )

    fun togglePlannerPlace(placeId: String) {
        val current = selectedPlannerPlaces.value.toMutableSet()
        if (current.contains(placeId)) {
            current.remove(placeId)
        } else {
            current.add(placeId)
        }
        selectedPlannerPlaces.value = current
    }

    fun saveCustomTripPlan(title: String, durationDays: Int, notes: String) {
        viewModelScope.launch {
            repository.saveTripPlan(
                title = title.ifBlank { "Gandikota Adventure Tour" },
                durationDays = durationDays,
                placeIds = selectedPlannerPlaces.value.toList(),
                notes = notes
            )
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Itinerary Saved!",
                message = "Custom trip plan with ${selectedPlannerPlaces.value.size} destinations saved to your planner."
            )
        }
    }

    fun deleteTripPlan(planId: String) {
        viewModelScope.launch {
            repository.deleteTripPlan(planId)
        }
    }

    // --- User Profile & Admin Switch ---
    val userProfile: StateFlow<UserEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateUser(name: String, email: String, phone: String, language: String) {
        viewModelScope.launch {
            val currentRole = userProfile.value?.role ?: "TOURIST"
            val updated = UserEntity(
                name = name,
                email = email,
                phone = phone,
                role = currentRole,
                preferredLanguage = language
            )
            repository.updateUserProfile(updated)
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Profile Updated",
                message = "Your traveler details have been saved."
            )
        }
    }

    fun toggleRole() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserEntity(name = "A. Manoj Kumar", email = "arigelamanoj9@gmail.com", phone = "+91 98765 43210")
            val newRole = if (current.role == "ADMIN") "TOURIST" else "ADMIN"
            repository.updateUserProfile(current.copy(role = newRole))
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Role Switched",
                message = "Now active as: $newRole mode."
            )
        }
    }

    // --- Admin Add Place & Stay ---
    fun adminAddPlace(place: TouristPlaceEntity) {
        viewModelScope.launch {
            repository.addPlace(place)
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Place Added",
                message = "${place.name} has been published to the catalog."
            )
        }
    }

    fun adminDeletePlace(placeId: String) {
        viewModelScope.launch {
            repository.deletePlace(placeId)
        }
    }

    fun adminAddRoom(room: RoomEntity) {
        viewModelScope.launch {
            repository.addRoom(room)
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Stay Added",
                message = "${room.name} is now available for tourists to book."
            )
        }
    }

    fun adminDeleteRoom(roomId: String) {
        viewModelScope.launch {
            repository.deleteRoom(roomId)
        }
    }

    fun adminAddEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.addEvent(event)
            _bookingMessage.value = BookingMessage(
                isSuccess = true,
                title = "Event Published",
                message = "${event.title} is now open for registration."
            )
        }
    }
}
