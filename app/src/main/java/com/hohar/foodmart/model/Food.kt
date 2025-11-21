package com.hohar.foodmart.model

import kotlinx.serialization.Serializable

@Serializable
data class Food (
    val uuid: String,
    val title: String? = null,
    val price: Float? = null,
    val category_uuid: String? = null,
    val image_url: String?
)