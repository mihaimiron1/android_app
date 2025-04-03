package com.mihaimiron.androidapp.di

import com.mihaimiron.androidapp.network.MuseumRepository
import com.mihaimiron.androidapp.network.MuseumRepositoryImpl
import com.mihaimiron.androidapp.screens.home.HomeViewModel
import com.mihaimiron.androidapp.screens.search.SearchViewModel
import com.mihaimiron.androidapp.screens.details.DetailsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::DetailsViewModel)
    singleOf(::MuseumRepositoryImpl) bind MuseumRepository::class
}