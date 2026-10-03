package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.EventBookingEntity
import com.example.data.model.EventEntity
import com.example.data.model.GuideBookingEntity
import com.example.data.model.GuideEntity
import com.example.data.model.RoomBookingEntity
import com.example.data.model.RoomEntity
import com.example.data.model.TouristPlaceEntity
import com.example.data.model.TripPlanEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TouristPlaceEntity::class,
        RoomEntity::class,
        RoomBookingEntity::class,
        GuideEntity::class,
        GuideBookingEntity::class,
        EventEntity::class,
        EventBookingEntity::class,
        TripPlanEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GandikotaDatabase : RoomDatabase() {

    abstract fun gandikotaDao(): GandikotaDao

    companion object {
        @Volatile
        private var INSTANCE: GandikotaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GandikotaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GandikotaDatabase::class.java,
                    "gandikota_explorer.db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.gandikotaDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: GandikotaDao) {
            // Seed Tourist Places
            val places = listOf(
                TouristPlaceEntity(
                    id = "place_gandikota_canyon",
                    name = "Gandikota Grand Canyon",
                    category = "Canyon & Gorge",
                    shortDescription = "The breathtaking Grand Canyon of India carved through the Erramala hills by the sacred Pennar River.",
                    fullDescription = "Often revered as the Grand Canyon of India, Gandikota features dramatic 300-foot-tall red sandstone layered gorge cliffs stretching for miles along the serene Pennar River. The surreal labyrinth of weathered stone boulders, steep canyon drops, and scenic horizons rival world-famous canyons. It offers spellbinding sunrise views, stargazing, and rock climbing.",
                    locationName = "Pennar Gorge, Gandikota, Kadapa Dist, AP",
                    latitude = 14.8149,
                    longitude = 78.2863,
                    timings = "Open 24 Hours (Best: 5:45 AM - 6:30 PM)",
                    entryFee = "Free entry",
                    tips = "Visit at dawn (5:30 AM) to catch the golden rays lighting the red sandstone cliffs. Wear sturdy trail shoes.",
                    bestTime = "October to March (Winter months)",
                    imageResName = "gandikota_canyon_hero_1791048159112",
                    rating = 4.9f,
                    reviewsCount = 1840,
                    isHyderabad = false,
                    isFavorite = true
                ),
                TouristPlaceEntity(
                    id = "place_gandikota_fort",
                    name = "Gandikota Fort & Citadel",
                    category = "Fort & Citadel",
                    shortDescription = "Formidable 12th-century medieval red stone fortress with 5 miles of perimeter bastion walls.",
                    fullDescription = "Constructed in 1123 AD by Kapa Raja of nearby Bommanapalle, Gandikota Fort is an architectural marvel of defensive warfare. The fort was later reinforced by the Pemmasani Nayaks and fortified with 101 bastions. Within its ramparts lie royal granaries, subterranean prisons, guard towers, and ancient weapon magazines.",
                    locationName = "Gandikota Village, Andhra Pradesh 516434",
                    latitude = 14.8140,
                    longitude = 78.2845,
                    timings = "6:00 AM – 6:00 PM Daily",
                    entryFee = "Free entry",
                    tips = "Walk along the northern ramparts for uninterrupted views of the river gorge. Carry water and sun hats.",
                    bestTime = "Early mornings & late afternoons",
                    imageResName = "ic_launcher_fg_1791048143726",
                    rating = 4.8f,
                    reviewsCount = 1420,
                    isHyderabad = false,
                    isFavorite = true
                ),
                TouristPlaceEntity(
                    id = "place_madhavaraya_temple",
                    name = "Madhavaraya Temple",
                    category = "Temples & Mosques",
                    shortDescription = "Majestic 16th-century Vijayanagara temple with towering sculpted gopuram and carved pillars.",
                    fullDescription = "Built during the reign of Sri Krishnadevaraya of the Vijayanagara Empire, the Madhavaraya Temple features a towering multi-tiered gopuram adorned with intricate stone sculptures of Hindu deities, mythical Yali beasts, and celestial dancers. The inner sanctum and hypostyle mandapa showcase high-relief medieval craftsmanship.",
                    locationName = "Inside Gandikota Fort Complex",
                    latitude = 14.8125,
                    longitude = 78.2858,
                    timings = "6:00 AM – 6:00 PM Daily",
                    entryFee = "Free entry",
                    tips = "Notice the fine stone-carved friezes on the pillar bases depicting episodes from the Ramayana and Bhagavata Purana.",
                    bestTime = "Morning light (7:00 AM - 10:00 AM)",
                    imageResName = "madhavaraya_temple_1791048229094",
                    rating = 4.7f,
                    reviewsCount = 980,
                    isHyderabad = false,
                    isFavorite = true
                ),
                TouristPlaceEntity(
                    id = "place_jumma_masjid",
                    name = "Jumma Masjid Gandikota",
                    category = "Temples & Mosques",
                    shortDescription = "Imposing medieval Islamic mosque with graceful stone minarets and expansive prayer arches.",
                    fullDescription = "The Jumma Masjid stands peacefully adjacent to the Madhavaraya Temple, highlighting Gandikota's rich multi-cultural history. Built in elegant Qutb Shahi and Mughal architectural styles, the mosque features symmetrical vaulted arched bays, tall minarets overlooking the gorge, and a central ablution fountain courtyard.",
                    locationName = "Central Fort Plaza, Gandikota",
                    latitude = 14.8138,
                    longitude = 78.2872,
                    timings = "6:00 AM – 6:00 PM Daily",
                    entryFee = "Free entry",
                    tips = "The arches frame the temple gopuram and canyon cliffs behind, making it a favorite for architectural photographers.",
                    bestTime = "Golden hour (4:30 PM - 5:45 PM)",
                    imageResName = "jumma_masjid_1791048242960",
                    rating = 4.7f,
                    reviewsCount = 860,
                    isHyderabad = false,
                    isFavorite = false
                ),
                TouristPlaceEntity(
                    id = "place_historic_jail",
                    name = "Historic Jail & Royal Granary",
                    category = "Fort & Citadel",
                    shortDescription = "Subterranean vaulted dungeons, high stone vaults, and massive grain storage silos.",
                    fullDescription = "The Royal Granary (Dhanyagara) is a massive vaulted stone hall with vaulted arched ceilings used to stockpile grain to withstand multi-year sieges. Right beside it lies the historic underground jail and detention cells with narrow air shafts and stone shackles, associated with medieval Rayalaseema military legends.",
                    locationName = "Fort Quarters, Gandikota",
                    latitude = 14.8130,
                    longitude = 78.2868,
                    timings = "7:00 AM – 5:30 PM",
                    entryFee = "Free entry",
                    tips = "Step inside the granary to experience the naturally cool acoustics and vaulted stone ceiling design.",
                    bestTime = "Midday or afternoon",
                    imageResName = "gandikota_canyon_hero_1791048159112",
                    rating = 4.6f,
                    reviewsCount = 540,
                    isHyderabad = false,
                    isFavorite = false
                ),
                TouristPlaceEntity(
                    id = "place_sunrise_viewpoint",
                    name = "Pennar River Sunrise Viewpoint",
                    category = "Canyon & Gorge",
                    shortDescription = "The quintessential cliff-edge lookout capturing the first morning rays illuminating the gorge.",
                    fullDescription = "Perched on the eastern cliff face of the Gandikota plateau, this natural stone terrace offers an unobstructed 180-degree view of the Pennar river meandering 300 feet below. The morning mist rises from the water as the sandstone turns from fiery magenta to radiant amber.",
                    locationName = "Eastern Cliff Edge, Gandikota",
                    latitude = 14.8155,
                    longitude = 78.2890,
                    timings = "5:00 AM – 8:00 AM (Sunrise peak at 5:45 AM)",
                    entryFee = "Free entry",
                    tips = "Carry a flashlight/headlamp for the short 5-minute boulder walk before dawn. Keep a safe distance from cliff edges.",
                    bestTime = "5:30 AM Sharp",
                    imageResName = "gandikota_canyon_hero_1791048159112",
                    rating = 4.9f,
                    reviewsCount = 2100,
                    isHyderabad = false,
                    isFavorite = true
                ),
                TouristPlaceEntity(
                    id = "place_mylavaram_dam",
                    name = "Mylavaram Dam & Reservoir",
                    category = "Lakes & Waterfalls",
                    shortDescription = "Expansive scenic freshwater reservoir 8 km from Gandikota offering speedboating and peaceful waters.",
                    fullDescription = "Constructed across the Pennar River, the Mylavaram Dam forms a vast shimmering blue lake bordered by craggy scrub hills. The AP Tourism development here offers pedal boats, speedboating, an aqua sports jetty, and lush landscaped garden banks popular with migratory birds.",
                    locationName = "Mylavaram, Kadapa District (8 km from Fort)",
                    latitude = 14.8512,
                    longitude = 78.3360,
                    timings = "9:00 AM – 5:30 PM",
                    entryFee = "Entry ₹20, Speedboating ₹150/head",
                    tips = "Combine this with an afternoon drive after visiting the fort. Sunset over the reservoir waters is calm and serene.",
                    bestTime = "3:30 PM – 5:30 PM",
                    imageResName = "gandikota_camping_1791048256119",
                    rating = 4.5f,
                    reviewsCount = 670,
                    isHyderabad = false,
                    isFavorite = false
                ),
                TouristPlaceEntity(
                    id = "place_owk_waterfalls",
                    name = "Owk Waterfalls & Cascades",
                    category = "Lakes & Waterfalls",
                    shortDescription = "Verified natural rocky waterfalls and freshwater plunge pool near Owk Reservoir (38 km).",
                    fullDescription = "Located within short driving distance from Gandikota, Owk Waterfalls (Gollaleru Cascade) tumbles over rocky geological ledges into a pristine natural pool surrounded by dense Rayalaseema deciduous shrubbery. It is an adventurous offbeat detour for nature lovers.",
                    locationName = "Near Owk Dam, Kurnool-Kadapa Border",
                    latitude = 15.2014,
                    longitude = 78.1189,
                    timings = "8:00 AM – 5:00 PM Daily",
                    entryFee = "Free entry",
                    tips = "Check local water flow levels before heading down. Rocks can be slippery near the plunge basin.",
                    bestTime = "Post-monsoon (September to January)",
                    imageResName = "gandikota_canyon_hero_1791048159112",
                    rating = 4.6f,
                    reviewsCount = 490,
                    isHyderabad = false,
                    isFavorite = false
                ),
                TouristPlaceEntity(
                    id = "place_charminar_hyderabad",
                    name = "Charminar (Hyderabad Gateway)",
                    category = "Transit Hub",
                    shortDescription = "Historical landmark and major travel gateway in Hyderabad connecting tourists to Gandikota.",
                    fullDescription = "Listed separately as the primary international flight and high-speed rail gateway for tourists journeying to Gandikota (approx. 370 km / 6-hour road trip via NH44). Built in 1591 by Sultan Muhammad Quli Qutb Shah, Charminar is world-renowned for its four grand arches and vibrant Laad Bazaar.",
                    locationName = "Charminar Rd, Char Kaman, Ghansi Bazaar, Hyderabad",
                    latitude = 17.3616,
                    longitude = 78.4747,
                    timings = "9:30 AM – 5:30 PM Daily",
                    entryFee = "₹25 (Indian Citizens), ₹300 (Foreign Nationals)",
                    tips = "Ideal transit hub to experience Hyderabadi Biryani and pearl bazaars before renting a self-drive vehicle or cab to Gandikota.",
                    bestTime = "Evening twilight (5:00 PM – 7:00 PM)",
                    imageResName = "jumma_masjid_1791048242960",
                    rating = 4.8f,
                    reviewsCount = 15200,
                    isHyderabad = true,
                    isFavorite = false
                )
            )
            dao.insertPlaces(places)

            // Seed Rooms & Stays
            val rooms = listOf(
                RoomEntity(
                    id = "room_haritha_resort",
                    name = "Haritha Resort Gandikota (APTDC)",
                    propertyType = "Resort",
                    pricePerNight = 2400,
                    rating = 4.4f,
                    reviewsCount = 520,
                    location = "Opp. Gandikota Fort Main Gate",
                    description = "Official Andhra Pradesh Tourism development nestled right at the fort entrance with spacious air-conditioned suites, multi-cuisine restaurant, and sprawling lawns.",
                    amenities = "Air Conditioning, Free Breakfast, Wi-Fi, Restaurant, Parking, 24/7 Security",
                    maxGuests = 3,
                    totalRooms = 15,
                    availableRooms = 12,
                    imageResName = "gandikota_camping_1791048256119"
                ),
                RoomEntity(
                    id = "room_canyon_camping",
                    name = "Gandikota Cliffside Camping & Bonfire",
                    propertyType = "Camping & Tents",
                    pricePerNight = 1299,
                    rating = 4.8f,
                    reviewsCount = 890,
                    location = "Gorge Cliff Edge Campsite",
                    description = "Authentic all-weather camping tents with foam mattresses, sleeping bags, night campfire with live acoustic music, stargazing telescope, and barbecue dinner included.",
                    amenities = "Gorge View, Bonfire, BBQ Dinner, Sleeping Bags, Stargazing Telescope, Clean Washrooms",
                    maxGuests = 2,
                    totalRooms = 25,
                    availableRooms = 18,
                    imageResName = "gandikota_camping_1791048256119"
                ),
                RoomEntity(
                    id = "room_royal_homestay",
                    name = "Royal Gorge Heritage Homestay",
                    propertyType = "Homestay",
                    pricePerNight = 1750,
                    rating = 4.7f,
                    reviewsCount = 310,
                    location = "Gandikota Heritage Village, Street 2",
                    description = "Cozy traditional stone-walled village home operated by a welcoming local family. Serves authentic Andhra spicy chicken, ragi mudda, and warm hospitality.",
                    amenities = "Village Experience, Home-cooked Andhra Food, Hot Water, Rooftop Canyon Terrace",
                    maxGuests = 4,
                    totalRooms = 8,
                    availableRooms = 6,
                    imageResName = "madhavaraya_temple_1791048229094"
                ),
                RoomEntity(
                    id = "room_mylavaram_retreat",
                    name = "Mylavaram Lakeview Retreat",
                    propertyType = "Hotel",
                    pricePerNight = 2899,
                    rating = 4.6f,
                    reviewsCount = 240,
                    location = "Mylavaram Reservoir Shoreline (8 km)",
                    description = "Quiet waterfront retreat featuring panoramic lake views, garden cottages, speedboating access, and private balconies overlooking the water.",
                    amenities = "Lake View Balcony, Boating Access, AC, Room Service, Swimming Pool, Parking",
                    maxGuests = 3,
                    totalRooms = 10,
                    availableRooms = 8,
                    imageResName = "gandikota_camping_1791048256119"
                )
            )
            dao.insertRooms(rooms)

            // Seed Local Tour Guides
            val guides = listOf(
                GuideEntity(
                    id = "guide_ramesh_kumar",
                    name = "Ramesh Kumar Nayak",
                    specialty = "Senior Fort & Citadel Historian",
                    languages = "Telugu, English, Hindi",
                    experienceYears = 12,
                    pricePerTour = 850,
                    rating = 4.9f,
                    reviewsCount = 380,
                    phone = "+91 94401 23456",
                    badge = "Certified Archaeological Guide",
                    availableSlots = "5:30 AM Sunrise Tour, 9:30 AM Fort History Walk, 4:00 PM Sunset Trail"
                ),
                GuideEntity(
                    id = "guide_lakshmi_narayana",
                    name = "Lakshmi Narayana",
                    specialty = "Pennar Gorge Trekking & Photography",
                    languages = "Telugu, Kannada, English",
                    experienceYears = 8,
                    pricePerTour = 750,
                    rating = 4.8f,
                    reviewsCount = 295,
                    phone = "+91 98480 98765",
                    badge = "Cliff Trek Master",
                    availableSlots = "5:15 AM Sunrise Trek, 3:30 PM Rock Scramble, 6:30 PM Night Astro Walk"
                ),
                GuideEntity(
                    id = "guide_swathi_reddy",
                    name = "Swathi Reddy",
                    specialty = "Eco Adventure, Kayak & Cultural Heritage",
                    languages = "Telugu, English, Tamil",
                    experienceYears = 6,
                    pricePerTour = 900,
                    rating = 4.9f,
                    reviewsCount = 210,
                    phone = "+91 89781 54321",
                    badge = "Certified Wilderness Leader",
                    availableSlots = "6:30 AM River Kayaking, 10:30 AM Temple Heritage, 4:30 PM Sunset Walk"
                )
            )
            dao.insertGuides(guides)

            // Seed Events
            val events = listOf(
                EventEntity(
                    id = "event_sunrise_photo",
                    title = "Pennar Gorge Sunrise Photography Expedition",
                    category = "Sunrise & Photo",
                    date = "Tomorrow",
                    time = "5:30 AM – 7:30 AM",
                    price = 499,
                    availableSeats = 14,
                    totalSeats = 20,
                    description = "Guided dawn excursion to vantage points overlooking the 300ft gorge. Master sunrise framing, long exposure, and drone navigation with professional travel photographers.",
                    meetingPoint = "Haritha Resort Entrance Gate",
                    duration = "2 Hours"
                ),
                EventEntity(
                    id = "event_twilight_walk",
                    title = "Gandikota Citadel Twilight Heritage Walk",
                    category = "Heritage Walk",
                    date = "This Weekend",
                    time = "4:30 PM – 6:30 PM",
                    price = 299,
                    availableSeats = 22,
                    totalSeats = 30,
                    description = "Immersive storytelling walk through the 800-year-old bastions, royal granaries, and subterranean dungeons with traditional lantern lighting as dusk falls.",
                    meetingPoint = "Madhavaraya Temple Gopuram",
                    duration = "2 Hours"
                ),
                EventEntity(
                    id = "event_campfire_astro",
                    title = "Cliffside Acoustic Campfire & Deep Sky Stargazing",
                    category = "Campfire & Astronomy",
                    date = "Daily Evening",
                    time = "7:30 PM – 10:00 PM",
                    price = 799,
                    availableSeats = 18,
                    totalSeats = 25,
                    description = "Gandikota has virtually zero light pollution. Enjoy live acoustic folk songs, hot tea, smoky campfire barbecue, and high-powered telescope views of Saturn's rings and the Milky Way.",
                    meetingPoint = "Gandikota Eco Campsite",
                    duration = "2.5 Hours"
                ),
                EventEntity(
                    id = "event_kayaking_pennar",
                    title = "Pennar River Canyon Kayaking & Rock Scramble",
                    category = "Adventure & Kayak",
                    date = "Every Morning",
                    time = "6:45 AM – 9:00 AM",
                    price = 999,
                    availableSeats = 8,
                    totalSeats = 12,
                    description = "Descend into the canyon and paddle across the crystal-clear turquoise waters of the Pennar River beneath towering red cliffs. Lifejackets and safety instructors provided.",
                    meetingPoint = "Pennar River Descent Base",
                    duration = "2 Hours 15 Mins"
                )
            )
            dao.insertEvents(events)

            // Seed Default User Profile
            val defaultUser = UserEntity(
                id = "primary_user",
                name = "A. Manoj Kumar",
                email = "arigelamanoj9@gmail.com",
                phone = "+91 98765 43210",
                role = "TOURIST",
                preferredLanguage = "English"
            )
            dao.insertUserProfile(defaultUser)
        }
    }
}
