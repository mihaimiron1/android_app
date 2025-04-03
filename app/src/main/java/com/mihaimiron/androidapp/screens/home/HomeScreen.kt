package com.mihaimiron.androidapp.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mihaimiron.androidapp.ui.theme.AndroidAppTheme
import org.koin.androidx.compose.koinViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mihaimiron.androidapp.objects.MuseumObject

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel(),
               onSearch: () -> Unit,
               onClickArtObject: (objectId: Int) -> Unit) {
    val objects by viewModel.objects.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isLastPage by viewModel.isLastPage.collectAsStateWithLifecycle()

    AndroidAppTheme {
        Scaffold(topBar = { TopBar(onClick = onSearch) }) { innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(objects) { index, item ->
                    ArtObject(item, onClick = {
                        onClickArtObject(item.id)
                    })
                }

                if (!isLoading && !isLastPage) {
                    item {
                        LaunchedEffect(key1 = objects.size) {
                            viewModel.nextPage()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onClick: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {

        },
        actions = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ArtObject(item: MuseumObject,
              onClick: () -> Unit) {
    Card(modifier = Modifier.clickable {
        onClick()
    }) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = item.artistDisplayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            if (item.primaryImageSmall.isNotBlank()) {
                GlideImage(
                    model = item.primaryImageSmall,
                    contentDescription = ""
                )
            }
        }
    }
}