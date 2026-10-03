package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface GandikotaDao {

    // --- Tourist Places ---
    @Query("SELECT * FROM tourist_places ORDER BY rating DESC")
    fun getAllPlaces(): Flow<List<TouristPlaceEntity>>

    @Query("SELECT * FROM tourist_places WHERE id = :id LIMIT 1")
    suspend fun getPlaceById(id: String): TouristPlaceEntity?

    @Query("SELECT * FROM tourist_places WHERE isFavorite = 1")
    fun getFavoritePlaces(): Flow<List<TouristPlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<TouristPlaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: TouristPlaceEntity)

    @Query("UPDATE tourist_places SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: String, isFav: Boolean)

    @Query("DELETE FROM tourist_places WHERE id = :id")
    suspend fun deletePlace(id: String)

    // --- Rooms ---
    @Query("SELECT * FROM rooms")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms WHERE id = :id LIMIT 1")
    suspend fun getRoomById(id: String): RoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<RoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity)

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Query("DELETE FROM rooms WHERE id = :id")
    suspend fun deleteRoom(id: String)

    // --- Room Bookings (Double Booking Prevention) ---
    @Query("SELECT * FROM room_bookings ORDER BY bookingTimestamp DESC")
    fun getAllRoomBookings(): Flow<List<RoomBookingEntity>>

    @Query("""
        SELECT * FROM room_bookings 
        WHERE roomId = :roomId 
          AND status = 'CONFIRMED' 
          AND NOT (checkOutDate <= :checkInDate OR checkInDate >= :checkOutDate)
    """)
    suspend fun getConflictingRoomBookings(
        roomId: String,
        checkInDate: String,
        checkOutDate: String
    ): List<RoomBookingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomBooking(booking: RoomBookingEntity)

    @Query("UPDATE room_bookings SET status = :status WHERE id = :bookingId")
    suspend fun updateRoomBookingStatus(bookingId: String, status: String)

    // --- Tour Guides ---
    @Query("SELECT * FROM guides")
    fun getAllGuides(): Flow<List<GuideEntity>>

    @Query("SELECT * FROM guides WHERE id = :id LIMIT 1")
    suspend fun getGuideById(id: String): GuideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuides(guides: List<GuideEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuide(guide: GuideEntity)

    @Query("DELETE FROM guides WHERE id = :id")
    suspend fun deleteGuide(id: String)

    @Query("SELECT * FROM guide_bookings ORDER BY bookingTimestamp DESC")
    fun getAllGuideBookings(): Flow<List<GuideBookingEntity>>

    @Query("""
        SELECT * FROM guide_bookings 
        WHERE guideId = :guideId 
          AND bookingDate = :date 
          AND timeSlot = :slot 
          AND status = 'CONFIRMED'
    """)
    suspend fun getConflictingGuideBookings(
        guideId: String,
        date: String,
        slot: String
    ): List<GuideBookingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuideBooking(booking: GuideBookingEntity)

    @Query("UPDATE guide_bookings SET status = :status WHERE id = :bookingId")
    suspend fun updateGuideBookingStatus(bookingId: String, status: String)

    // --- Events ---
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: String): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("UPDATE events SET availableSeats = :availableSeats WHERE id = :id")
    suspend fun updateEventSeats(id: String, availableSeats: Int)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEvent(id: String)

    @Query("SELECT * FROM event_bookings ORDER BY bookingTimestamp DESC")
    fun getAllEventBookings(): Flow<List<EventBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventBooking(booking: EventBookingEntity)

    @Query("UPDATE event_bookings SET status = :status WHERE id = :bookingId")
    suspend fun updateEventBookingStatus(bookingId: String, status: String)

    // --- Trip Plans ---
    @Query("SELECT * FROM trip_plans ORDER BY createdAt DESC")
    fun getAllTripPlans(): Flow<List<TripPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripPlan(plan: TripPlanEntity)

    @Query("DELETE FROM trip_plans WHERE id = :id")
    suspend fun deleteTripPlan(id: String)

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 'primary_user' LIMIT 1")
    fun getUserProfile(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserEntity)
}
