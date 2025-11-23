package com.hohar.foodmart.viewmodel

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FoodMartViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

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
    fun `uiState is Success when both API calls succeed`() = runBlocking {
        // Arrange
        val viewModel = FoodMartViewModel()

        // Act: Wait for the network calls to complete
        var attempts = 0
        while (viewModel.uiState.value is UiState.Loading && attempts < 100) {
            delay(50)
            attempts++
        }

        // Assert
        assertTrue(viewModel.uiState.value is UiState.Success)
        val successState = viewModel.uiState.value as UiState.Success
        
        // Verify that data was loaded
        assertFalse("Food list should not be empty", successState.foodList.isEmpty())
        assertFalse("Food categories should not be empty", successState.foodCategories.isEmpty())
        
        // Verify that food items have required fields
        successState.foodList.forEach { food ->
            assertTrue("Food should have a UUID", food.uuid.isNotEmpty())
        }
        
        // Verify that categories have required fields
        successState.foodCategories.forEach { category ->
            assertTrue("Category should have a UUID", category.uuid.isNotEmpty())
        }
    }

    @Test
    fun `retry sets state to Loading then Success`() = runBlocking {
        val viewModel = FoodMartViewModel()

        // Wait for initial network call to complete (they run on IO dispatcher)
        var attempts = 0
        while (viewModel.uiState.value is UiState.Loading && attempts < 100) {
            delay(50)
            attempts++
        }

        // Act
        viewModel.retry()

        // Wait for retry network calls to complete
        attempts = 0
        while (viewModel.uiState.value is UiState.Loading && attempts < 100) {
            delay(50)
            attempts++
        }

        // Assert
        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `uiState is Error when network exception occurs`() = runBlocking {
        // Arrange: Use an invalid base URL that will cause a network exception
        val invalidBaseUrl = "http://invalid-url-that-does-not-exist-12345.com/api"
        val viewModel = FoodMartViewModel(baseUrl = invalidBaseUrl)

        // Act: Wait for the network calls to complete
        // Network calls are blocking on IO dispatcher, so we need to wait
        var attempts = 0
        while (viewModel.uiState.value is UiState.Loading && attempts < 100) {
            delay(50)
            attempts++
        }

        // Assert
        assertTrue(viewModel.uiState.value is UiState.Error)
        val errorState = viewModel.uiState.value as UiState.Error
        assertTrue(errorState.message.startsWith("Network error:"))
    }
}