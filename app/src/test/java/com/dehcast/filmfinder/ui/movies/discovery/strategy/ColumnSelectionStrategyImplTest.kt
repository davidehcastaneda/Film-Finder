package com.dehcast.filmfinder.ui.movies.discovery.strategy

import org.junit.Test

class ColumnSelectionStrategyImplTest {

    private val strategyImpl = ColumnSelectionStrategyImpl()

    @Test
    fun `determineColumnCount cannot return values smaller than 1`() {
        //Given
        val screenWidth: Int = -1
        val viewholderWidth: Int = 2

        //When
        val result =
            strategyImpl.determineColumnCount(screenWidth.toFloat(), viewholderWidth.toFloat())

        //Then
        assert(result == 1)
    }
}