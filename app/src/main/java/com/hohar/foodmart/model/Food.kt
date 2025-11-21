package com.hohar.foodmart.model

import kotlinx.serialization.Serializable

/**
 * Data model for a food entity in the app
 *
 * uuid is the unique identifier for the food
 * title is the name of the food
 * price is the price of the food
 * category_uuid is the unique identifier for the category of the food
 * image_url is the URL for the image of the food
 * nullable for API compatibility
 */

@Serializable
data class Food (
    val uuid: String,
    val title: String? = null,
    val price: Float? = null,
    val category_uuid: String? = null,
    val image_url: String? = null
)