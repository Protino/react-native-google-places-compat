package com.googleplacescompat

import com.google.android.libraries.places.api.model.Place

/**
 * Mapping between Place.Field's class id and label, to fit with iOS functionality
 */
enum class RNGooglePlacesPlaceFieldEnum(val key: String, val field: Place.Field) {
  ADDRESS("address", Place.Field.ADDRESS),
  ID("placeID", Place.Field.ID),
  LAT_LNG("location", Place.Field.LAT_LNG),
  NAME("name", Place.Field.NAME),
  OPENING_HOURS("openingHours", Place.Field.OPENING_HOURS),
  PHONE_NUMBER("phoneNumber", Place.Field.PHONE_NUMBER),
  PHOTO_METADATAS("photos", Place.Field.PHOTO_METADATAS),
  PLUS_CODE("plusCode", Place.Field.PLUS_CODE),
  PRICE_LEVEL("priceLevel", Place.Field.PRICE_LEVEL),
  RATING("rating", Place.Field.RATING),
  TYPES("types", Place.Field.TYPES),
  USER_RATINGS_TOTAL("userRatingsTotal", Place.Field.USER_RATINGS_TOTAL),
  VIEWPORT("viewport", Place.Field.VIEWPORT),
  WEBSITE_URI("website", Place.Field.WEBSITE_URI),
  ADDRESS_COMPONENTS("addressComponents", Place.Field.ADDRESS_COMPONENTS);

  companion object {
    fun findByFieldKey(key: String): RNGooglePlacesPlaceFieldEnum? {
      return when (key) {
        "address" -> ADDRESS
        "placeID" -> ID
        "location" -> LAT_LNG
        "name" -> NAME
        "openingHours" -> OPENING_HOURS
        "phoneNumber" -> PHONE_NUMBER
        "photos" -> PHOTO_METADATAS
        "plusCode" -> PLUS_CODE
        "priceLevel" -> PRICE_LEVEL
        "rating" -> RATING
        "types" -> TYPES
        "userRatingsTotal" -> USER_RATINGS_TOTAL
        "viewport" -> VIEWPORT
        "website" -> WEBSITE_URI
        "addressComponents" -> ADDRESS_COMPONENTS
        else -> null
      }
    }
  }
}
