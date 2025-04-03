package com.mihaimiron.androidapp.screens.details // Match ViewModel package

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mihaimiron.androidapp.objects.MuseumObject
import com.mihaimiron.androidapp.ui.theme.AndroidAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(
    onGoBack: () -> Unit,
    viewModel: DetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AndroidAppTheme {
        Scaffold(
            topBar = {
                DetailTopBar(onNavigateBack = onGoBack)
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (val state = uiState) {
                    is DetailsUiState.Loading -> {
                        LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is DetailsUiState.Success -> {
                        MuseumDetailsContent(
                            museumObject = state.museumObject,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is DetailsUiState.Error -> {

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {

        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Search"
                )
            }
        },
        actions = {

        }
    )
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun MuseumDetailsContent(
    museumObject: MuseumObject,
    modifier: Modifier = Modifier
) {
    val carouselState = rememberCarouselState {
        museumObject.additionalImages.size
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = museumObject.title.takeIf { it.isNotBlank() } ?: "Untitled",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.9f),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (museumObject.primaryImageSmall.isNotBlank()) {
            GlideImage(
                model = museumObject.primaryImageSmall,
                contentDescription = museumObject.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Box(modifier = Modifier.height(200.dp).fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)) {
                Text("No primary image available", modifier=Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (museumObject.additionalImages.isNotEmpty()) {
            Text(
                text = "Additional Images",
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalMultiBrowseCarousel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                state = carouselState,
                preferredItemWidth = 280.dp,
                itemSpacing = 8.dp
            ) { imageIndex ->
                GlideImage(
                    model = museumObject.additionalImages[imageIndex],
                    contentDescription = "Additional image ${imageIndex + 1}",
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = "Details",
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        val artistValue = museumObject.artistDisplayName
        if (artistValue.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "Artist", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = artistValue, modifier = Modifier.weight(0.6f), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.End)
            }
        }
        val dateValue = museumObject.objectDate
        if (dateValue.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "Date", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = dateValue, modifier = Modifier.weight(0.6f), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.End)
            }
        }
        val mediumValue = museumObject.medium
        if (mediumValue.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "Medium", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = mediumValue, modifier = Modifier.weight(0.6f), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.End)
            }
        }
        val departmentValue = museumObject.department
        if (departmentValue.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "Department", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = departmentValue, modifier = Modifier.weight(0.6f), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.End)
            }
        }
        val countryValue = museumObject.country
        if (countryValue.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "Country", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = countryValue, modifier = Modifier.weight(0.6f), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.End)
            }
        }
    }
}