package com.hohar.foodmart.data


import com.hohar.foodmart.model.Food
import com.hohar.foodmart.model.FoodCategory

// Mark as 'open' so MockK can mock it in tests
open class FoodMartRepository {

    // These functions should call your actual API
    // For now, move the logic from your ViewModel into here
    open suspend fun getFoods(): ArrayList<Food> {
        // TODO: Call your actual API here (e.g., ApiClient.api.getFoods())
        // Return the result
        return arrayListOf()
    }

    open suspend fun getCategories(): ArrayList<FoodCategory> {
        // TODO: Call your actual API here
        return arrayListOf()
    }
}
