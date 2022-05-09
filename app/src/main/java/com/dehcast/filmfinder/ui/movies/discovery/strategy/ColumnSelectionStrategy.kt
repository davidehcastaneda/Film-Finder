package com.dehcast.filmfinder.ui.movies.discovery.strategy

interface ColumnSelectionStrategy {

    fun determineColumnCount(screenWidth: Float, viewHolderWidth: Float): Int
}

class ColumnSelectionStrategyImpl : ColumnSelectionStrategy {

    override fun determineColumnCount(screenWidth: Float, viewHolderWidth: Float): Int {
        val rawCount = (screenWidth / viewHolderWidth).toInt()
        return if (rawCount < 1) 1
        else rawCount
    }
}