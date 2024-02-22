package com.googleplacescompat

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.googleplacescompat.RNGooglePlacesPlaceFieldEnum.Companion.findByFieldKey
import com.googleplacescompat.RNGooglePlacesPlaceTypeEnum.Companion.findByTypeId

class RNGooglePlacesModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), ActivityEventListener {

  private val reactContext: ReactApplicationContext
  private var pendingPromise: Promise? = null
  private var lastSelectedFields: List<Place.Field>? = null
  private val placesClient: PlacesClient

  init {
    val apiKey = reactContext.applicationContext.getString(R.string.places_api_key)

    // Setup Places Client
    if (!Places.isInitialized() && apiKey != "") {
      Places.initialize(reactContext.applicationContext, apiKey)
    }
    placesClient = Places.createClient(reactContext.applicationContext)
    this.reactContext = reactContext
    this.reactContext.addActivityEventListener(this)
  }

  override fun getName(): String {
    return REACT_CLASS
  }

  /**
   * Called after the autocomplete activity has finished to return its result.
   */
  override fun onActivityResult(
    activity: Activity,
    requestCode: Int,
    resultCode: Int,
    intent: Intent?
  ) {

    // Check that the result was from the autocomplete widget.
    if (requestCode == AUTOCOMPLETE_REQUEST_CODE && intent != null) {
      when (resultCode) {
        AutocompleteActivity.RESULT_OK -> {
          val place = Autocomplete.getPlaceFromIntent(intent)
          val map = propertiesMapForPlace(place, lastSelectedFields)
          resolvePromise(map)
        }

        AutocompleteActivity.RESULT_ERROR -> {
          val status = Autocomplete.getStatusFromIntent(intent)
          rejectPromise("E_RESULT_ERROR", Error(status.statusMessage))
        }

        AutocompleteActivity.RESULT_CANCELED -> {
          rejectPromise("E_USER_CANCELED", Error("Search cancelled"))
        }
      }
    }
  }

  /**
   * Exposed React's methods
   */
  @ReactMethod
  fun openAutocompleteModal(options: ReadableMap, fields: ReadableArray, promise: Promise) {
    if (!Places.isInitialized()) {
      promise.reject(
        "E_API_KEY_ERROR",
        Error("No API key defined in gradle.properties or errors initializing Places")
      )
      return
    }
    val currentActivity = currentActivity
    if (currentActivity == null) {
      promise.reject("E_ACTIVITY_DOES_NOT_EXIST", Error("Activity doesn't exist"))
      return
    }
    pendingPromise = promise
    lastSelectedFields = ArrayList()
    val type = options.getString("type")
    val country =
      options.getString("country")?.let { it.ifBlank { return@let null } }
    val initialQuery = options.getString("initialQuery")
    val useOverlay = options.getBoolean("useOverlay")
    val locationBias = options.getMap("locationBias")
    val biasToLatitudeSW = locationBias?.getDouble("latitudeSW") ?: 0.0
    val biasToLongitudeSW = locationBias?.getDouble("longitudeSW") ?: 0.0
    val biasToLatitudeNE = locationBias?.getDouble("latitudeNE") ?: 0.0
    val biasToLongitudeNE = locationBias?.getDouble("longitudeNE") ?: 0.0
    val locationRestriction = options.getMap("locationRestriction")
    val restrictToLatitudeSW = locationRestriction?.getDouble("latitudeSW") ?: 0.0
    val restrictToLongitudeSW = locationRestriction?.getDouble("longitudeSW") ?: 0.0
    val restrictToLatitudeNE = locationRestriction?.getDouble("latitudeNE") ?: 0.0
    val restrictToLongitudeNE = locationRestriction?.getDouble("longitudeNE") ?: 0.0
    lastSelectedFields = getPlaceFields(fields.toArrayList(), false)
    val autocompleteIntent = Autocomplete.IntentBuilder(
      if (useOverlay) AutocompleteActivityMode.OVERLAY else AutocompleteActivityMode.FULLSCREEN,
      lastSelectedFields!!
    )
    if (biasToLatitudeSW != 0.0 && biasToLongitudeSW != 0.0 && biasToLatitudeNE != 0.0 && biasToLongitudeNE != 0.0) {
      autocompleteIntent.setLocationBias(
        RectangularBounds.newInstance(
          LatLng(biasToLatitudeSW, biasToLongitudeSW),
          LatLng(biasToLatitudeNE, biasToLongitudeNE)
        )
      )
    }
    if (restrictToLatitudeSW != 0.0 && restrictToLongitudeSW != 0.0 && restrictToLatitudeNE != 0.0 && restrictToLongitudeNE != 0.0) {
      autocompleteIntent.setLocationRestriction(
        RectangularBounds.newInstance(
          LatLng(restrictToLatitudeSW, restrictToLongitudeSW),
          LatLng(restrictToLatitudeNE, restrictToLongitudeNE)
        )
      )
    }
    autocompleteIntent.setCountry(country)
    if (initialQuery != null) {
      autocompleteIntent.setInitialQuery(initialQuery)
    }
    autocompleteIntent.setTypeFilter(getFilterType(type))
    currentActivity.startActivityForResult(
      autocompleteIntent.build(reactContext.applicationContext),
      AUTOCOMPLETE_REQUEST_CODE
    )
  }

  @ReactMethod
  fun getAutocompletePredictions(query: String?, options: ReadableMap, promise: Promise) {
    pendingPromise = promise
    if (!Places.isInitialized()) {
      promise.reject(
        "E_API_KEY_ERROR",
        Error("No API key defined in gradle.properties or errors initializing Places")
      )
      return
    }
    val type = options.getString("type")
    val country = options.getString("country")?.let { it.ifBlank { return@let null } }
    val useSessionToken = options.getBoolean("useSessionToken")
    val locationBias = options.getMap("locationBias")
    val biasToLatitudeSW = locationBias?.getDouble("latitudeSW") ?: 0.0
    val biasToLongitudeSW = locationBias?.getDouble("longitudeSW") ?: 0.0
    val biasToLatitudeNE = locationBias?.getDouble("latitudeNE") ?: 0.0
    val biasToLongitudeNE = locationBias?.getDouble("longitudeNE") ?: 0.0
    val locationRestriction = options.getMap("locationRestriction")
    val restrictToLatitudeSW = locationRestriction?.getDouble("latitudeSW") ?: 0.0
    val restrictToLongitudeSW = locationRestriction?.getDouble("longitudeSW") ?: 0.0
    val restrictToLatitudeNE = locationRestriction?.getDouble("latitudeNE") ?: 0.0
    val restrictToLongitudeNE = locationRestriction?.getDouble("longitudeNE") ?: 0.0
    val requestBuilder = FindAutocompletePredictionsRequest.builder()
      .setQuery(query)
    if (country != null) {
      requestBuilder.setCountry(country)
    }
    if (biasToLatitudeSW != 0.0 && biasToLongitudeSW != 0.0 && biasToLatitudeNE != 0.0 && biasToLongitudeNE != 0.0) {
      requestBuilder.locationBias = RectangularBounds.newInstance(
        LatLng(biasToLatitudeSW, biasToLongitudeSW),
        LatLng(biasToLatitudeNE, biasToLongitudeNE)
      )
    }
    if (restrictToLatitudeSW != 0.0 && restrictToLongitudeSW != 0.0 && restrictToLatitudeNE != 0.0 && restrictToLongitudeNE != 0.0) {
      requestBuilder.locationRestriction = RectangularBounds.newInstance(
        LatLng(restrictToLatitudeSW, restrictToLongitudeSW),
        LatLng(restrictToLatitudeNE, restrictToLongitudeNE)
      )
    }
    requestBuilder.typeFilter = getFilterType(type)
    if (useSessionToken) {
      requestBuilder.sessionToken = AutocompleteSessionToken.newInstance()
    }
    val task = placesClient.findAutocompletePredictions(requestBuilder.build())
    task.addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
      if (response.autocompletePredictions.size == 0) {
        val emptyResult = Arguments.createArray()
        promise.resolve(emptyResult)
        return@addOnSuccessListener
      }
      val predictionsList = Arguments.createArray()
      for (prediction in response.autocompletePredictions) {
        val map = Arguments.createMap()
        map.putString("fullText", prediction.getFullText(null).toString())
        map.putString("primaryText", prediction.getPrimaryText(null).toString())
        map.putString("secondaryText", prediction.getSecondaryText(null).toString())
        map.putString("placeID", prediction.placeId.toString())
        if (prediction.types.size > 0) {
          map.putArray("types", Arguments.fromArray(prediction.types.toTypedArray()))
        }
        predictionsList.pushMap(map)
      }
      promise.resolve(predictionsList)
    }
    task.addOnFailureListener { exception: Exception ->
      promise.reject(
        "E_AUTOCOMPLETE_ERROR",
        Error(exception.message)
      )
    }
  }

  @ReactMethod
  fun lookUpPlaceByID(placeID: String?, fields: ReadableArray, promise: Promise) {
    pendingPromise = promise
    if (!Places.isInitialized()) {
      promise.reject(
        "E_API_KEY_ERROR",
        Error("No API key defined in gradle.properties or errors initializing Places")
      )
      return
    }
    if (placeID == null) {
      promise.reject("E_PLACE_ID_ERROR", Error("Place ID is required"))
      return
    }
    val selectedFields = getPlaceFields(fields.toArrayList(), false)
    val request = FetchPlaceRequest.builder(placeID, selectedFields).build()
    placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
      val place = response.place
      val map = propertiesMapForPlace(place, selectedFields)
      promise.resolve(map)
    }.addOnFailureListener { exception: Exception ->
      promise.reject(
        "E_PLACE_DETAILS_ERROR",
        Error(exception.message)
      )
    }
  }

  @ReactMethod
  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_WIFI_STATE])
  fun getCurrentPlace(fields: ReadableArray, promise: Promise) {
    if (ContextCompat.checkSelfPermission(
        reactContext.applicationContext,
        permission.ACCESS_WIFI_STATE
      )
      != PackageManager.PERMISSION_GRANTED
      || ContextCompat.checkSelfPermission(
        reactContext.applicationContext,
        permission.ACCESS_FINE_LOCATION
      )
      != PackageManager.PERMISSION_GRANTED
    ) {
      promise.reject(
        "E_CURRENT_PLACE_ERROR",
        Error("Both ACCESS_WIFI_STATE & ACCESS_FINE_LOCATION permissions are required")
      )
      return
    }
    val selectedFields = getPlaceFields(fields.toArrayList(), true)
    if (checkPermission(permission.ACCESS_FINE_LOCATION)) {
      findCurrentPlaceWithPermissions(selectedFields, promise)
    }
  }

  /**
   * Fetches a list of [PlaceLikelihood] instances that represent the Places the user is
   * most
   * likely to be at currently.
   */
  @RequiresPermission(allOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_WIFI_STATE])
  private fun findCurrentPlaceWithPermissions(
    selectedFields: List<Place.Field>,
    promise: Promise
  ) {
    val currentPlaceRequest = FindCurrentPlaceRequest.newInstance(selectedFields)
    val currentPlaceTask = placesClient.findCurrentPlace(currentPlaceRequest)
    currentPlaceTask.addOnSuccessListener { response: FindCurrentPlaceResponse ->
      if (response.placeLikelihoods.size == 0) {
        val emptyResult = Arguments.createArray()
        promise.resolve(emptyResult)
        return@addOnSuccessListener
      }
      val likelyPlacesList = Arguments.createArray()
      for (placeLikelihood in response.placeLikelihoods) {
        val map = propertiesMapForPlace(placeLikelihood.place, selectedFields)
        map.putDouble("likelihood", placeLikelihood.likelihood)
        likelyPlacesList.pushMap(map)
      }
      promise.resolve(likelyPlacesList)
    }
    currentPlaceTask.addOnFailureListener { exception: Exception ->
      promise.reject(
        "E_CURRENT_PLACE_ERROR",
        Error(exception.message)
      )
    }
  }

  private fun propertiesMapForPlace(
    place: Place,
    selectedFields: List<Place.Field>?
  ): WritableMap {
    // Display attributions if required.
    // CharSequence attributions = place.getAttributions();
    val map = Arguments.createMap()
    if (selectedFields == null) return map
    if (selectedFields.contains(Place.Field.LAT_LNG)) {
      place.latLng?.let {
        val locationMap = Arguments.createMap()
        locationMap.putDouble("latitude", it.latitude)
        locationMap.putDouble("longitude", it.longitude)
        map.putMap("location", locationMap)
      }
    }
    if (selectedFields.contains(Place.Field.NAME)) {
      map.putString("name", place.name)
    }
    if (selectedFields.contains(Place.Field.ADDRESS)) {
      if (!TextUtils.isEmpty(place.address)) {
        map.putString("address", place.address)
      } else {
        map.putString("address", "")
      }
    }
    if (selectedFields.contains(Place.Field.ADDRESS_COMPONENTS)) {
      if (place.addressComponents != null) {
        val items = place.addressComponents?.asList() ?: listOf()
        val addressComponents = WritableNativeArray()
        for (item in items) {
          val addressComponentMap = Arguments.createMap()
          addressComponentMap.putArray("types", Arguments.fromList(item.types))
          addressComponentMap.putString("name", item.name)
          addressComponentMap.putString("shortName", item.shortName)
          addressComponents.pushMap(addressComponentMap)
        }
        map.putArray("addressComponents", addressComponents)
      } else {
        val emptyResult = Arguments.createArray()
        map.putArray("addressComponents", emptyResult)
      }
    }
    if (selectedFields.contains(Place.Field.PHONE_NUMBER)) {
      if (!TextUtils.isEmpty(place.phoneNumber)) {
        map.putString("phoneNumber", place.phoneNumber)
      } else {
        map.putString("phoneNumber", "")
      }
    }
    if (selectedFields.contains(Place.Field.WEBSITE_URI)) {
      place.websiteUri?.let {
        map.putString("website", it.toString())
      } ?: map.putString("website", "")
    }
    if (selectedFields.contains(Place.Field.ID)) {
      map.putString("placeID", place.id)
    }
    place.attributions?.let { attributions ->
      map.putArray("attributions", Arguments.fromArray(ArrayList(attributions).toTypedArray()))
    } ?: {
      val emptyResult = Arguments.createArray()
      map.putArray("attributions", emptyResult)
    }
    if (selectedFields.contains(Place.Field.TYPES)) {
      place.placeTypes?.let {
        map.putArray("types", Arguments.fromArray(it.toTypedArray()))
      } ?: {
        val emptyResult = Arguments.createArray()
        map.putArray("types", emptyResult)
      }
    }
    if (selectedFields.contains(Place.Field.VIEWPORT)) {
      place.viewport?.let {
        val viewportMap = Arguments.createMap()
        viewportMap.putDouble("latitudeNE", it.northeast.latitude)
        viewportMap.putDouble("longitudeNE", it.northeast.longitude)
        viewportMap.putDouble("latitudeSW", it.southwest.latitude)
        viewportMap.putDouble("longitudeSW", it.southwest.longitude)
        map.putMap("viewport", viewportMap)
      } ?: {
        val emptyResult = Arguments.createMap()
        map.putMap("viewport", emptyResult)
      }
    }
    if (selectedFields.contains(Place.Field.PRICE_LEVEL)) {
      map.putInt("priceLevel", place.priceLevel ?: 0)

    }
    if (selectedFields.contains(Place.Field.RATING)) {
      map.putDouble("rating", place.rating ?: 0.0)
    }
    if (selectedFields.contains(Place.Field.OPENING_HOURS)) {
      place.openingHours?.let {
        map.putArray(
          "openingHours",
          Arguments.fromArray(ArrayList(it.weekdayText).toTypedArray())
        )
      } ?: {
        val emptyResult = Arguments.createArray()
        map.putArray("openingHours", emptyResult)
      }
    }
    if (selectedFields.contains(Place.Field.PLUS_CODE)) {
      place.plusCode?.let { plusCode ->
        val plusCodeMap = Arguments.createMap()
        plusCodeMap.putString("compoundCode", plusCode.compoundCode)
        plusCodeMap.putString("globalCode", plusCode.globalCode)
        map.putMap("plusCode", plusCodeMap)
      } ?: {
        val emptyResult = Arguments.createMap()
        map.putMap("plusCode", emptyResult)
      }
    }
    if (selectedFields.contains(Place.Field.USER_RATINGS_TOTAL)) {
      map.putInt("userRatingsTotal", place.userRatingsTotal ?: 0)
    }
    return map
  }

  //todo: Replace usage of TypeFilter
  private fun getFilterType(type: String?): TypeFilter? {
    val mappedFilter: TypeFilter? = when (type) {
      "geocode" -> TypeFilter.GEOCODE
      "address" -> TypeFilter.ADDRESS
      "establishment" -> TypeFilter.ESTABLISHMENT
      "regions" -> TypeFilter.REGIONS
      "cities" -> TypeFilter.CITIES
      else -> null
    }
    return mappedFilter
  }

  private fun getPlaceFields(
    placeFields: ArrayList<Any>,
    isCurrentOrFetchPlace: Boolean
  ): List<Place.Field> {
    val selectedFields: MutableList<Place.Field> = ArrayList()
    if (placeFields.size == 0 && !isCurrentOrFetchPlace) {
      return listOf(*Place.Field.values())
    }
    if (placeFields.size == 0 && isCurrentOrFetchPlace) {
      val allPlaceFields: MutableList<Place.Field> =
        ArrayList(listOf(*Place.Field.values()))
      allPlaceFields.removeAll(
        listOf(
          Place.Field.OPENING_HOURS,
          Place.Field.PHONE_NUMBER,
          Place.Field.WEBSITE_URI,
          Place.Field.ADDRESS_COMPONENTS
        )
      )
      return allPlaceFields
    }
    for (placeField in placeFields) {
      if (findByFieldKey(placeField.toString()) != null) {
        selectedFields.add(findByFieldKey(placeField.toString())!!.field)
      }
    }
    if (placeFields.size != 0 && isCurrentOrFetchPlace) {
      selectedFields.removeAll(
        listOf(
          Place.Field.OPENING_HOURS,
          Place.Field.PHONE_NUMBER,
          Place.Field.WEBSITE_URI,
          Place.Field.ADDRESS_COMPONENTS
        )
      )
    }
    return selectedFields
  }

  private fun checkPermission(permission: String): Boolean {
    val currentActivity = currentActivity
    val hasPermission = ContextCompat.checkSelfPermission(
      reactContext.applicationContext,
      permission
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasPermission && currentActivity != null) {
      ActivityCompat.requestPermissions(currentActivity, arrayOf(permission), 0)
    }
    return hasPermission
  }

  private fun rejectPromise(code: String, err: Error) {
    pendingPromise?.reject(code, err)
    pendingPromise = null
  }

  private fun resolvePromise(data: Any) {
    pendingPromise?.resolve(data)
    this.pendingPromise = null
  }

  private fun findPlaceTypeLabelByPlaceTypeId(id: Int): String {
    return findByTypeId(id).label
  }

  override fun onNewIntent(intent: Intent) {}

  companion object {
    const val TAG = "RNGooglePlaces"
    var AUTOCOMPLETE_REQUEST_CODE = 360
    var REACT_CLASS = "RNGooglePlaces"
  }
}
