package com.hohar.foodmart.model

import kotlinx.serialization.Serializable

/**
 * Data model for a food category entity in the app
 *
 * uuid is the unique identifier for the category
 * name is the name of the category
 */

@Serializable
data class FoodCategory(
    val uuid: String,
    val name: String? = null
)
