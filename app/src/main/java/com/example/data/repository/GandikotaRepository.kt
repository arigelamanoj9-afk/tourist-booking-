package com.example.data.repository

import com.example.data.local.GandikotaDao
import com.example.data.model.EventBookingEntity
import com.example.data.model.EventEntity
import com.example.data.model.GuideBookingEntity
import com.example.data.model.GuideEntity
import com.example.data.model.RoomBookingEntity
import com.example.data.model.RoomEntity
import com.example.data.model.TouristPlaceEntity
import com.example.data.model.TripPlanEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GandikotaRepository(private val dao: GandikotaDao) {

    // --- Tourist Places ---
    val allPlaces: Flow<List<TouristPlaceEntity>> = dao.getAllPlaces()
    val favoritePlaces: Flow<List<TouristPlaceEntity>> = dao.getFavoritePlaces()

    suspend fun getPlaceById(id: String): TouristPlaceEntity? = dao.getPlaceById(id)

    suspend fun toggleFavorite(placeId: String, currentStatus: Boolean) {
        dao.updateFavorite(placeId, !currentStatus)
    }

    suspend fun addPlace(place: TouristPlaceEntity) {
        dao.insertPlace(place)
    }

    suspend fun deletePlace(placeId: String) {
        dao.deletePlace(placeId)
    }

    // --- Rooms & Bookings ---
    val allRooms: Flow<List<RoomEntity>> = dao.getAllRooms()
    val allRoomBookings: Flow<List<RoomBookingEntity>> = dao.getAllRoomBookings()

    suspend fun addRoom(room: RoomEntity) {
        dao.insertRoom(room)
    }

    suspend fun updateRoom(room: RoomEntity) {
        dao.updateRoom(room)
    }

    suspend fun deleteRoom(roomId: String) {
        dao.deleteRoom(roomId)
    }

    /**
     * Real room booking validation preventing double bookings.
     * Verifies that the room has available units and no overlapping confirmed bookings.
     */
    suspend fun bookRoom(
        roomId: String,
        roomName: String,
        userName: String,
        userEmail: String,
        userPhone: String,
        checkInDate: String,
        checkOutDate: String,
        guestsCount: Int,
        roomsCount: Int,
        pricePerNight: Int,
        nights: Int
    ): Result<RoomBookingEntity> {
        val room = dao.getRoomById(roomId)
            ?: return Result.failure(Exception("Room not found in database."))

        if (checkInDate >= checkOutDate) {
            return Result.failure(Exception("Check-out date must be after check-in date."))
        }

        // Check for conflicting confirmed bookings
        val conflicts = dao.getConflictingRoomBookings(roomId, checkInDate, checkOutDate)
        val bookedRoomsInPeriod = conflicts.sumOf { it.roomsCount }

        if (bookedRoomsInPeriod + roomsCount > room.totalRooms) {
            return Result.failure(
                Exception(
                    "Selected stay is fully booked for $checkInDate to $checkOutDate. " +
                    "(${room.totalRooms - bookedRoomsInPeriod} room(s) remaining for this period). " +
                    "Please select different dates or fewer rooms."
                )
            )
        }

        val totalAmount = pricePerNight * nights * roomsCount
        val bookingId = "GDK-RM-" + UUID.randomUUID().toString().take(6).uppercase()

        val booking = RoomBookingEntity(
            id = bookingId,
            roomId = roomId,
            roomName = roomName,
            userName = userName,
            userEmail = userEmail,
            userPhone = userPhone,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            guestsCount = guestsCount,
            roomsCount = roomsCount,
            totalAmount = totalAmount,
            status = "CONFIRMED",
            bookingTimestamp = System.currentTimeMillis()
        )

        dao.insertRoomBooking(booking)
        return Result.success(booking)
    }

    suspend fun cancelRoomBooking(bookingId: String) {
        dao.updateRoomBookingStatus(bookingId, "CANCELLED")
    }

    // --- Guides & Bookings ---
    val allGuides: Flow<List<GuideEntity>> = dao.getAllGuides()
    val allGuideBookings: Flow<List<GuideBookingEntity>> = dao.getAllGuideBookings()

    suspend fun addGuide(guide: GuideEntity) {
        dao.insertGuide(guide)
    }

    suspend fun deleteGuide(guideId: String) {
        dao.deleteGuide(guideId)
    }

    suspend fun bookGuide(
        guideId: String,
        guideName: String,
        touristName: String,
        touristPhone: String,
        date: String,
        slot: String,
        tourType: String,
        guestsCount: Int,
        totalAmount: Int
    ): Result<GuideBookingEntity> {
        val conflicts = dao.getConflictingGuideBookings(guideId, date, slot)
        if (conflicts.isNotEmpty()) {
            return Result.failure(Exception("Guide $guideName is already booked on $date for $slot. Please choose another time slot."))
        }

        val bookingId = "GDK-GD-" + UUID.randomUUID().toString().take(6).uppercase()
        val booking = GuideBookingEntity(
            id = bookingId,
            guideId = guideId,
            guideName = guideName,
            touristName = touristName,
            touristPhone = touristPhone,
            bookingDate = date,
            timeSlot = slot,
            tourType = tourType,
            guestsCount = guestsCount,
            totalAmount = totalAmount,
            status = "CONFIRMED"
        )
        dao.insertGuideBooking(booking)
        return Result.success(booking)
    }

    suspend fun cancelGuideBooking(bookingId: String) {
        dao.updateGuideBookingStatus(bookingId, "CANCELLED")
    }

    // --- Events & Bookings ---
    val allEvents: Flow<List<EventEntity>> = dao.getAllEvents()
    val allEventBookings: Flow<List<EventBookingEntity>> = dao.getAllEventBookings()

    suspend fun addEvent(event: EventEntity) {
        dao.insertEvent(event)
    }

    suspend fun deleteEvent(eventId: String) {
        dao.deleteEvent(eventId)
    }

    suspend fun bookEvent(
        eventId: String,
        eventTitle: String,
        userName: String,
        userPhone: String,
        ticketsCount: Int,
        pricePerTicket: Int
    ): Result<EventBookingEntity> {
        val event = dao.getEventById(eventId)
            ?: return Result.failure(Exception("Event not found."))

        if (event.availableSeats < ticketsCount) {
            return Result.failure(Exception("Only ${event.availableSeats} seat(s) available for $eventTitle. Cannot book $ticketsCount tickets."))
        }

        val totalAmount = ticketsCount * pricePerTicket
        val bookingId = "GDK-EV-" + UUID.randomUUID().toString().take(6).uppercase()

        val booking = EventBookingEntity(
            id = bookingId,
            eventId = eventId,
            eventTitle = eventTitle,
            userName = userName,
            userPhone = userPhone,
            ticketsCount = ticketsCount,
            totalAmount = totalAmount,
            status = "CONFIRMED"
        )

        dao.insertEventBooking(booking)
        dao.updateEventSeats(eventId, event.availableSeats - ticketsCount)
        return Result.success(booking)
    }

    suspend fun cancelEventBooking(bookingId: String, eventId: String, ticketsCount: Int) {
        dao.updateEventBookingStatus(bookingId, "CANCELLED")
        val event = dao.getEventById(eventId)
        if (event != null) {
            dao.updateEventSeats(eventId, (event.availableSeats + ticketsCount).coerceAtMost(event.totalSeats))
        }
    }

    // --- Trip Plans ---
    val allTripPlans: Flow<List<TripPlanEntity>> = dao.getAllTripPlans()

    suspend fun saveTripPlan(title: String, durationDays: Int, placeIds: List<String>, notes: String) {
        val plan = TripPlanEntity(
            id = "plan_" + UUID.randomUUID().toString().take(8),
            title = title,
            durationDays = durationDays,
            selectedPlaceIds = placeIds.joinToString(","),
            notes = notes
        )
        dao.insertTripPlan(plan)
    }

    suspend fun deleteTripPlan(planId: String) {
        dao.deleteTripPlan(planId)
    }

    // --- User Profile ---
    val userProfile: Flow<UserEntity?> = dao.getUserProfile()

    suspend fun updateUserProfile(user: UserEntity) {
        dao.insertUserProfile(user)
    }
}
