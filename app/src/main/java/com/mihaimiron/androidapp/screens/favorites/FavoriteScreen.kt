package com.mihaimiron.androidapp.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mihaimiron.androidapp.screens.home.ArtObject
import com.mihaimiron.androidapp.ui.theme.AndroidAppTheme
import org.koin.androidx.compose.koinViewModel
import com.mihaimiron.androidapp.objects.MuseumObject

@Composable
fun FavoriteScreen(
    onGoBack: () -> Unit,
    onClickArtObject: (objectId: Int) -> Unit,
    viewModel: FavoriteViewModel = koinViewModel()
) {
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

    AndroidAppTheme {
        Scaffold(
            topBar = { TopBar(onGoBack = onGoBack) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites) { favorite ->
                    ArtObject(
                        museumObject = MuseumObject(
                            id = favorite.objectId,
                            title = favorite.title,
                            artistDisplayName = favorite.artistName,
                            primaryImageSmall = favorite.imageUrl,
                            additionalImages = emptyList(),
                            objectDate = "",
                            department = "",
                            country = "",
                            state = "",
                            medium = ""
                        ),
                        onArtObjectClick = { onClickArtObject(favorite.objectId) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onGoBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text("Favorites") },
        navigationIcon = {
            IconButton(onClick = onGoBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        }
    )
} 