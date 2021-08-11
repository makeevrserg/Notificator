package com.makeevrserg.notificator.network.response.address

data class PlaceItem(
    val address: Address,
    val boundingbox: List<String>,
    val `class`: String,
    val display_address: String,
    val display_name: String,
    val display_place: String,
    val lat: String,
    val licence: String,
    val lon: String,
    val osm_id: String,
    val osm_type: String,
    val place_id: String,
    val type: String
)