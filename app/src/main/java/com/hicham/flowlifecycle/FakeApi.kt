package com.hicham.flowlifecycle

import kotlin.random.Random

val FAKE_DATA = listOf(
    "Restaurant",
    "Clothing Store",
    "Coffee Shop",
    "Bookstore",
    "Pharmacy",
    "Electronics Store",
    "College",
    "Fast Food Restaurant",
    "Cupcake Shop",
    "Furniture Store",
    "Supermarket",
    "Gift Shop",
    "Kids Store"
)

class FakeApi {
    suspend fun fetchNearbyLocations(latitude: Double, longitude: Double): List<String> {
        val size = Random.nextInt(1, FAKE_DATA.size)
        return FAKE_DATA.shuffled().take(size)
    }
}
