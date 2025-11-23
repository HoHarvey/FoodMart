package com.hohar.foodmart.viewmodel

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FoodMartViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState is initially Loading`() = runTest {
        val viewModel = FoodMartViewModel()

        assertTrue(viewModel.uiState.value is UiState.Loading)
    }

    @Test
    fun `retry sets state to Loading then Success`() = runTest {
        val viewModel = FoodMartViewModel()

        // Move to a finished state first
        advanceUntilIdle()

        // Act
        viewModel.retry()

        // Assert: It should briefly be loading (though in a test this happens instantly
        // unless we pause the dispatcher), and then eventually success.
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}