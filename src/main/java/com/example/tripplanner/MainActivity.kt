package com.example.tripplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tripplanner.data.AppDatabase
import com.example.tripplanner.data.TripRepository
import com.example.tripplanner.ui.theme.TripPlannerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.get(applicationContext)
        val repository = TripRepository(db.tripDao())

        setContent {
            TripPlannerTheme {
                TripPlannerApp(repository)
            }
        }
    }
}
