package com.googleplacescompat

import com.facebook.react.bridge.ReadableMap
import com.googleplacescompat.model.AutoCompletePredictionsOptions
import com.googleplacescompat.model.LocationBias
import com.googleplacescompat.model.LocationRestriction

fun ReadableMap.getAutoCompletePredictionsOptions(): AutoCompletePredictionsOptions {
  val filterTypes = getArray("types")?.toArrayList()?.mapNotNull { it as? String } ?: listOf()
  val countries = getArray("countries")?.toArrayList()?.mapNotNull { it as? String } ?: listOf()
  val useOverlay = getBoolean("useOverlay")
  val initialQuery = getString("initialQuery")

  val locationBias = getMap("locationBias")?.let {
    LocationBias(
      latitudeSW = it.getDouble("latitudeSW"),
      longitudeSW = it.getDouble("longitudeSW"),
      latitudeNE = it.getDouble("latitudeNE"),
      longitudeNE = it.getDouble("longitudeNE")
    )
  }

  val locationRestriction = getMap("locationRestriction")?.let {
    LocationRestriction(
      latitudeSW = it.getDouble("latitudeSW"),
      longitudeSW = it.getDouble("longitudeSW"),
      latitudeNE = it.getDouble("latitudeNE"),
      longitudeNE = it.getDouble("longitudeNE")
    )
  }

  return AutoCompletePredictionsOptions(
    useOverlay = useOverlay,
    initialQuery = initialQuery,
    filterTypes = filterTypes,
    countries = countries,
    locationBias = locationBias,
    locationRestriction = locationRestriction
  )
}
