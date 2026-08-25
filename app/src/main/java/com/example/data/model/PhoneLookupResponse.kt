package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhoneLookupResponse(
    @Json(name = "success") val success: Boolean? = false,
    @Json(name = "data") val data: PhoneData? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class PhoneData(
    @Json(name = "name") val name: String? = null,
    @Json(name = "number") val number: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "birthday") val birthday: String? = null,
    @Json(name = "image") val image: String? = null,
    @Json(name = "alt_mobile") val altMobile: String? = null,
    @Json(name = "circle") val circle: String? = null,
    @Json(name = "id_number") val idNumber: String? = null
)
