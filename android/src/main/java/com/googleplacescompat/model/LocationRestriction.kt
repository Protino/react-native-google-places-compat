package com.googleplacescompat.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.RectangularBounds

data class LocationRestriction(
    val latitudeSW: Double,
    val longitudeSW: Double,
    val latitudeNE: Double,
    val longitudeNE: Double
) {
  fun isValid(): Boolean {
    return latitudeSW != 0.0 && longitudeSW != 0.0 && latitudeNE != 0.0 && longitudeNE != 0.0
  }

  fun toRectangularBounds(): RectangularBounds {
    return RectangularBounds.newInstance(
      LatLng(latitudeSW, longitudeSW),
      LatLng(latitudeNE, longitudeNE)
    )
  }
}
