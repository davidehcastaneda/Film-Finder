package com.dehcast.filmfinder.di.modules

import com.dehcast.filmfinder.ui.movies.discovery.strategy.ColumnSelectionStrategy
import com.dehcast.filmfinder.ui.movies.discovery.strategy.ColumnSelectionStrategyImpl
import dagger.Module
import dagger.Provides

@Module
class StrategyModule {

    @Provides
    fun provideColumnSelectionStrategy(): ColumnSelectionStrategy = ColumnSelectionStrategyImpl()
}