package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.GandikotaDao
import com.example.data.local.GandikotaDatabase
import com.example.data.model.EventEntity
import com.example.data.model.RoomEntity
import com.example.data.repository.GandikotaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GandikotaBookingTest {

    private lateinit var db: GandikotaDatabase
    private lateinit var dao: GandikotaDao
    private lateinit var repository: GandikotaRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, GandikotaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.gandikotaDao()
        repository = GandikotaRepository(dao)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testRoomBookingAndDoubleBookingPrevention() = runBlocking {
        // Insert a test room with 1 unit capacity
        val room = RoomEntity(
            id = "test_cottage",
            name = "Cliff Edge Cottage",
            propertyType = "Resort",
            pricePerNight = 2000,
            rating = 4.8f,
            reviewsCount = 10,
            location = "Gandikota",
            description = "Test description",
            amenities = "AC, Gorge View",
            maxGuests = 2,
            totalRooms = 1,
            availableRooms = 1,
            imageResName = ""
        )
        dao.insertRoom(room)

        // First booking: 2026-10-10 to 2026-10-12
        val firstResult = repository.bookRoom(
            roomId = "test_cottage",
            roomName = "Cliff Edge Cottage",
            userName = "Manoj",
            userEmail = "manoj@example.com",
            userPhone = "9876543210",
            checkInDate = "2026-10-10",
            checkOutDate = "2026-10-12",
            guestsCount = 2,
            roomsCount = 1,
            pricePerNight = 2000,
            nights = 2
        )
        assertTrue(firstResult.isSuccess)
        assertEquals(4000, firstResult.getOrNull()?.totalAmount)

        // Conflicting booking on overlapping date: 2026-10-11 to 2026-10-13
        val conflictResult = repository.bookRoom(
            roomId = "test_cottage",
            roomName = "Cliff Edge Cottage",
            userName = "Tourist2",
            userEmail = "tourist2@example.com",
            userPhone = "9876543211",
            checkInDate = "2026-10-11",
            checkOutDate = "2026-10-13",
            guestsCount = 2,
            roomsCount = 1,
            pricePerNight = 2000,
            nights = 2
        )
        assertTrue("Double booking should be prevented", conflictResult.isFailure)

        // Non-overlapping booking after check-out: 2026-10-12 to 2026-10-14
        val nonConflictResult = repository.bookRoom(
            roomId = "test_cottage",
            roomName = "Cliff Edge Cottage",
            userName = "Tourist3",
            userEmail = "tourist3@example.com",
            userPhone = "9876543212",
            checkInDate = "2026-10-12",
            checkOutDate = "2026-10-14",
            guestsCount = 2,
            roomsCount = 1,
            pricePerNight = 2000,
            nights = 2
        )
        assertTrue("Subsequent non-overlapping booking should succeed", nonConflictResult.isSuccess)
    }

    @Test
    fun testEventBookingAndSeatDecrement() = runBlocking {
        val event = EventEntity(
            id = "test_sunrise",
            title = "Sunrise Walk",
            category = "Sunrise & Photo",
            date = "Tomorrow",
            time = "5:30 AM",
            price = 500,
            availableSeats = 5,
            totalSeats = 5,
            description = "Test sunrise",
            meetingPoint = "Fort Gate",
            duration = "2 Hours"
        )
        dao.insertEvent(event)

        val result = repository.bookEvent(
            eventId = "test_sunrise",
            eventTitle = "Sunrise Walk",
            userName = "Manoj",
            userPhone = "9876543210",
            ticketsCount = 3,
            pricePerTicket = 500
        )
        assertTrue(result.isSuccess)

        val updatedEvent = dao.getEventById("test_sunrise")
        assertEquals(2, updatedEvent?.availableSeats)

        // Attempt to book more seats than remaining
        val overbookResult = repository.bookEvent(
            eventId = "test_sunrise",
            eventTitle = "Sunrise Walk",
            userName = "Another",
            userPhone = "9876543211",
            ticketsCount = 3,
            pricePerTicket = 500
        )
        assertTrue(overbookResult.isFailure)
    }
}
