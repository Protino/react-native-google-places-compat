package com.googleplacescompat.model

data class AutoCompletePredictionsOptions(
  val useOverlay: Boolean,
  val initialQuery: String?,
  val filterTypes: List<String>,
  val countries: List<String>,
  val locationBias: LocationBias?,
  val locationRestriction: LocationRestriction?) {

  fun isLocationBiasSet(): Boolean {
    return locationBias != null && locationBias.isValid()
  }

  fun isLocationRestrictionSet(): Boolean {
    return locationRestriction != null && locationRestriction.isValid()
  }
}
