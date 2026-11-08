package com.example.tripplanner

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tripplanner.data.TripRepository
import com.example.tripplanner.ui.detail.TripDetailScreen
import com.example.tripplanner.ui.detail.TripDetailViewModel
import com.example.tripplanner.ui.detail.TripDetailViewModelFactory
import com.example.tripplanner.ui.edit.TripEditScreen
import com.example.tripplanner.ui.trips.TripListScreen
import com.example.tripplanner.ui.trips.TripsViewModel
import com.example.tripplanner.ui.trips.TripsViewModelFactory

@Composable
fun TripPlannerApp(repo: TripRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "trips") {

        composable("trips") {
            val vm: TripsViewModel = viewModel(factory = TripsViewModelFactory(repo))
            TripListScreen(
                vm = vm,
                onOpen = { id -> navController.navigate("trip/$id") },
                onAdd = { navController.navigate("trip_edit/-1") }
            )
        }

        composable(
            route = "trip/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: -1L
            val vm: TripDetailViewModel = viewModel(
                key = "trip_$id",
                factory = TripDetailViewModelFactory(repo, id)
            )
            TripDetailScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onEdit = { tripId -> navController.navigate("trip_edit/$tripId") }
            )
        }

        composable(
            route = "trip_edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: -1L
            TripEditScreen(
                repo = repo,
                tripId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
