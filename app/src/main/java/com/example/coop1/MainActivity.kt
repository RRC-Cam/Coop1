package com.example.coop1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FetchPlaceRequest

class MainActivity : ComponentActivity() {

    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.places_api_key))
        }
        placesClient = Places.createClient(this)

        setContent {
            MaterialTheme {
                PlacesReviewScreen(placesClient)
            }
        }
    }
}

@Composable
fun PlacesReviewScreen(placesClient: PlacesClient) {
    val placeId = "ChIJb1_Ssfdz6lIRPu4jQcqk_Cs" // Example place ID
    val placeInfo = remember { mutableStateOf<String>("Loading...") }

    LaunchedEffect(Unit) {
        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.RATING,
            Place.Field.USER_RATINGS_TOTAL
        )
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            placeInfo.value = "${place.name}\nRating: ${place.rating} (${place.userRatingsTotal} reviews)"
        }.addOnFailureListener {
            placeInfo.value = "Failed to fetch place: ${it.message}"
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = placeInfo.value, style = MaterialTheme.typography.bodyLarge)
    }
}
