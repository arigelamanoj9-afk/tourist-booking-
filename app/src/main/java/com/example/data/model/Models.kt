package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tourist_places")
data class TouristPlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // "Canyon & Gorge", "Fort & Citadel", "Temples & Mosques", "Lakes & Waterfalls", "Transit Hub"
    val shortDescription: String,
    val fullDescription: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val timings: String,
    val entryFee: String,
    val tips: String,
    val bestTime: String,
    val imageResName: String,
    val rating: Float,
    val reviewsCount: Int,
    val isHyderabad: Boolean = false,
    val isFavorite: Boolean = false
)

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val propertyType: String, // "Resort", "Camping & Tents", "Homestay", "Hotel"
    val pricePerNight: Int,
    val rating: Float,
    val reviewsCount: Int,
    val location: String,
    val description: String,
    val amenities: String,
    val maxGuests: Int,
    val totalRooms: Int,
    val availableRooms: Int,
    val imageResName: String
)

@Entity(tableName = "room_bookings")
data class RoomBookingEntity(
    @PrimaryKey val id: String,
    val roomId: String,
    val roomName: String,
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val checkInDate: String, // YYYY-MM-DD
    val checkOutDate: String, // YYYY-MM-DD
    val guestsCount: Int,
    val roomsCount: Int,
    val totalAmount: Int,
    val status: String, // "CONFIRMED", "CANCELLED"
    val bookingTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "guides")
data class GuideEntity(
    @PrimaryKey val id: String,
    val name: String,
    val specialty: String,
    val languages: String,
    val experienceYears: Int,
    val pricePerTour: Int,
    val rating: Float,
    val reviewsCount: Int,
    val phone: String,
    val badge: String,
    val availableSlots: String
)

@Entity(tableName = "guide_bookings")
data class GuideBookingEntity(
    @PrimaryKey val id: String,
    val guideId: String,
    val guideName: String,
    val touristName: String,
    val touristPhone: String,
    val bookingDate: String,
    val timeSlot: String,
    val tourType: String,
    val guestsCount: Int,
    val totalAmount: Int,
    val status: String, // "CONFIRMED", "CANCELLED"
    val bookingTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Sunrise & Photo", "Heritage Walk", "Adventure & Kayak", "Campfire & Astronomy"
    val date: String,
    val time: String,
    val price: Int,
    val availableSeats: Int,
    val totalSeats: Int,
    val description: String,
    val meetingPoint: String,
    val duration: String
)

@Entity(tableName = "event_bookings")
data class EventBookingEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val eventTitle: String,
    val userName: String,
    val userPhone: String,
    val ticketsCount: Int,
    val totalAmount: Int,
    val status: String, // "CONFIRMED", "CANCELLED"
    val bookingTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "trip_plans")
data class TripPlanEntity(
    @PrimaryKey val id: String,
    val title: String,
    val durationDays: Int,
    val selectedPlaceIds: String, // comma separated IDs
    val notes: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: String = "primary_user",
    val name: String,
    val email: String,
    val phone: String,
    val role: String = "TOURIST", // "TOURIST" or "ADMIN"
    val preferredLanguage: String = "English"
)
