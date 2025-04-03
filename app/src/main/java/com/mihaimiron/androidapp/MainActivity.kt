package com.mihaimiron.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.mihaimiron.androidapp.routes.HomeRoute
import com.mihaimiron.androidapp.ui.theme.AndroidAppTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mihaimiron.androidapp.routes.DetailsRoute
import com.mihaimiron.androidapp.routes.SearchRoute
import com.mihaimiron.androidapp.screens.details.DetailsScreen
import com.mihaimiron.androidapp.screens.home.HomeScreen
import com.mihaimiron.androidapp.screens.search.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAppTheme {
                NavBar()
            }
        }
    }
}

@Composable
fun NavBar() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeScreen(onSearch = {
                navController.navigate(SearchRoute)
            }, onClickArtObject = { objectId ->
                navController.navigate(DetailsRoute(objectId))
            })
        }
        composable<SearchRoute> {
            SearchScreen(onGoBack = {
                navController.popBackStack()
            }, onClickArtObject = { objectId ->
                navController.navigate(DetailsRoute(objectId))
            })
        }
        composable<DetailsRoute> {
            DetailsScreen(onGoBack = {
                navController.popBackStack()
            })
        }
    }
}