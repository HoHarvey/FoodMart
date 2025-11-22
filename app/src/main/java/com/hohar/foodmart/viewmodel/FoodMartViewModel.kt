package com.hohar.foodmart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hohar.foodmart.model.Food
import com.hohar.foodmart.model.FoodCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import okhttp3.OkHttpClient
import okhttp3.Request


sealed class UiState {
    object Loading: UiState()
    data class Success(val foodCategories: List<FoodCategory>, val foodList: List<Food>): UiState()
    data class Error(val message: String): UiState()
}


class FoodMartViewModel: ViewModel(){
    /**
     * HTTP client for making network requests to the FoodMart APIs.
     * Configured as a private property to ensure proper lifecycle management.
     */
    private val client = OkHttpClient()

    /**
     * Mutable StateFlow containing the list of food categories.
     * This is the internal state that should not be exposed directly to the UI.
     * Updates are made through ViewModel methods to maintain data integrity.
     */
    private val _foodCategories = MutableStateFlow<List<FoodCategory>>(emptyList())

    /**
     * Mutable StateFlow containing the list of food.
     * This is the internal state that should not be exposed directly to the UI.
     * Updates are made through ViewModel methods to maintain data integrity.
     */
    private val _foodList = MutableStateFlow<List<Food>>(emptyList())

    /**
     * Public StateFlow exposing the food list to the UI.
     * This is the immutable version that the UI should observe.
     * Changes to this flow will automatically trigger UI recomposition.
     */
    val foodList: StateFlow<List<Food>> = _foodList.asStateFlow()

    /**
     * Public StateFlow exposing the food categories to the UI.
     * This is the immutable version that the UI should observe.
     * Changes to this flow will automatically trigger UI recomposition.
     */
    val foodCategories: StateFlow<List<FoodCategory>> = _foodCategories.asStateFlow()

    /**
     * Mutable StateFlow containing the current UI state.
     * Manages loading, success, and error states for better user experience.
     */
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    /**
     * Public StateFlow exposing the UI state to the UI layer.
     * Allows the UI to react to state changes and show appropriate content.
     */
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        fetchFoodMart()
    }

    private fun fetchFoodMart() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val requestFood = Request.Builder()
                    .url("https://7shifts.github.io/mobile-takehome/api/food_items.json")
                    .build()
                val responseFood = withContext(Dispatchers.IO) {
                    client.newCall(requestFood).execute()
                }
                val requestFoodCategory = Request.Builder()
                    .url("https://7shifts.github.io/mobile-takehome/api/food_item_categories.json")
                    .build()
                val responseFoodCategory = withContext(Dispatchers.IO) {
                    client.newCall(requestFoodCategory).execute()
                }
                if (responseFood.isSuccessful && responseFoodCategory.isSuccessful) {
                    val responseBodyFood = responseFood.body?.string() ?: ""
                    var json = Json { ignoreUnknownKeys = true }
                    var jsonElement = json.parseToJsonElement(responseBodyFood)
                    val parsedFood = json.decodeFromJsonElement<List<Food>>(jsonElement)
                    _foodList.value = parsedFood

                    val responseBodyFoodCategory = responseFoodCategory.body?.string() ?: ""
                    json = Json { ignoreUnknownKeys = true }
                    jsonElement = json.parseToJsonElement(responseBodyFoodCategory)
                    val parsedFoodCategory = json.decodeFromJsonElement<List<FoodCategory>>(jsonElement)
                    _foodCategories.value = parsedFoodCategory
                    _uiState.value = UiState.Success(parsedFoodCategory, parsedFood)
                }
                else{
                    _uiState.value = UiState.Error("HTTP error")
                }

            } catch (e: Exception){
                _uiState.value = UiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun retry(){
        fetchFoodMart()
    }

}