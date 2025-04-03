package com.mihaimiron.androidapp.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mihaimiron.androidapp.screens.home.ArtObject
import com.mihaimiron.androidapp.ui.theme.AndroidAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(onGoBack: () -> Unit,
                 onClickArtObject: (objectId: Int) -> Unit) {
    AndroidAppTheme {
        Scaffold(topBar = { TopBar(onClick = onGoBack) }) { innerPadding ->
            ItemsComponent(modifier = Modifier.padding(innerPadding), onClickArtObject = onClickArtObject)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onClick: () -> Unit,
                   viewModel: SearchViewModel = koinViewModel()) {
    var text by remember { mutableStateOf(viewModel.search.value) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            TextField(modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search for an art object") },
                value = text,
                singleLine = true,
                onValueChange = { newText ->
                    run {
                        text = newText
                        viewModel.search(text)
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {

        }
    )
}

@Composable
private fun ItemsComponent(modifier: Modifier = Modifier,
                           viewModel: SearchViewModel = koinViewModel(),
                           onClickArtObject: (objectId: Int) -> Unit) {
    val objects by viewModel.objects.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isLastPage by viewModel.isLastPage.collectAsStateWithLifecycle()

    LazyColumn(modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(
            items = objects,
            key = { museumObject -> museumObject.id }
        ) { item ->
            ArtObject(item, onClick = {
                onClickArtObject(item.id)
            })
        }

        if (!isLoading && !isLastPage) {
            item {
                LaunchedEffect(key1 = objects.size){
                    viewModel.nextPage()
                }
            }
        }
    }
}