package com.hohar.foodmart.viewmodel

import com.hohar.foodmart.model.Food
import com.hohar.foodmart.model.FoodCategory
import io.mockk.coEvery
import io.mockk.mockk
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
    fun `fetchFood updates uiState to Success on valid data`() = runTest {
        val mockRepository = mockk<FoodMartRepository>()
        val dummyFoodList = arrayListOf(
            Food(
                uuid = "1",
                name = "Apple",
                price = 1.99f,
                category_uuid = "1",
                image_url = ""
            )
        )
        val dummyCategoryList = arrayListOf(
            FoodCategory(uuid = "1", name = "Fruits")
        )

        coEvery { mockRepository.getFoods() } returns dummyFoodList
        coEvery { mockRepository.getCategories() } returns dummyCategoryList

        val viewModel = FoodMartViewModel(mockRepository)

        advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertTrue("State should be Success", currentState is UiState.Success)

        // Check if data is populated (assuming your mock/fake data isn't empty)
        val successState = currentState as UiState.Success
        // You might need to expose foodList in UiState to test it easily,
        // or check the separate flows:
        assertTrue(viewModel.foodList.value.isNotEmpty())
    }
}