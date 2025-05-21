interface GeoCoordinateBounds {
  latitudeSW: number;
  longitudeSW: number;
  latitudeNE: number;
  longitudeNE: number;
}

/**
 * Standard cross-platform coordinate type.
 *
 * Corresponds to [CLLocationCoordinate2D](https://developer.apple.com/documentation/corelocation/cllocationcoordinate2d)
 * and [LatLng](https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLng).
 */
export interface LatLng {
  longitude: number;
  latitude: number;
}

// Import the Place interface from the spec file to be extended
import { Place } from './NativeGooglePlacesCompat';

/**
 * Current place, with additional likelihood property.
 *
 * Corresponds roughly to
 * [GMSPlaceLikelihood](https://developers.google.com/places/ios-sdk/reference/interface_g_m_s_place_likelihood.html#a370caf145921ced6374bd39212561d90)
 * and [com.google.android.libraries.places.api.net.PlaceLikelihood](https://developers.google.com/places/android-sdk/reference/com/google/android/libraries/places/api/model/PlaceLikelihood),
 * though the likelihood has been flattened into the Place object.
 */
export interface CurrentPlace extends Place { // Changed GMSTypes.Place to Place
  /**
   * Returns a value from 0.0 to 1.0 indicating the confidence that the user
   * is at this place.
   *
   * The larger the value the more confident we are of the place returned. For
   * example, a likelihood of 0.75 means that the user is at least 75% likely
   * to be at this place.
   */
  likelihood: number;
}

// GMSTypes.Place and GMSTypes.AutocompletePrediction are removed as their counterparts
// are now defined in NativeGooglePlacesCompat.ts and re-exported by src/index.ts.
// Consumers should use Place and AutocompletePrediction from the library's main export.

/**
 * Internal mappings to Google Maps Services types.
 */
export namespace GMSTypes {
  // PlusCode, AddressComponent, PlaceType, ReadOnlyPlaceType, ReturnPlaceType, AutocompleteType
  // are kept as they provide detailed type information useful for library consumers.

  /**
   * Internal mapping to [GMSPlusCode](https://developers.google.com/places/ios-sdk/reference/interface_g_m_s_place)
   * and [com.google.android.libraries.places.api.model.PlusCode](https://developers.google.com/places/android-sdk/reference/com/google/android/libraries/places/api/model/PlusCode).
   *
   * A class containing the Plus codes representation for a location.
   *
   * Plus Code, or Open Location Code (OLC), is a geocode system for
   * identifying any geographical area on Earth, even when a street address
   * does not exist.
   *
   * See https://plus.codes/ for more details.
   */
  export interface PlusCode {
    /**
     * Geo plus code, e.g.
     * "8FVC9G8F+5W"
     */
    globalCode: string;

    /**
     * Compound plus code, e.g.
     * "9G8F+5W Zurich, Switzerland"
     */
    compoundCode: string;
  }

  /**
   * Place Type, corresponding to the
   * [return types table 1](https://developers.google.com/places/ios-sdk/supported_types).
   *
   * You can use the following values in the `types` filter for place
   * [searches](https://developers.google.com/places/web-service/search)
   * (Places API only). These types may also be returned anywhere a Place
   * result is returned (for example as part of the
   * [Place Details](https://developers.google.com/places/android-sdk/place-details)
   * resulting from a call to `fetchPlace()`).
   */
  export type PlaceType =
    | 'accounting'
    | 'airport'
    | 'amusement_park'
    | 'aquarium'
    | 'art_gallery'
    | 'atm'
    | 'bakery'
    | 'bank'
    | 'bar'
    | 'beauty_salon'
    | 'bicycle_store'
    | 'book_store'
    | 'bowling_alley'
    | 'bus_station'
    | 'cafe'
    | 'campground'
    | 'car_dealer'
    | 'car_rental'
    | 'car_repair'
    | 'car_wash'
    | 'casino'
    | 'cemetery'
    | 'church'
    | 'city_hall'
    | 'clothing_store'
    | 'convenience_store'
    | 'courthouse'
    | 'dentist'
    | 'department_store'
    | 'doctor'
    | 'electrician'
    | 'electronics_store'
    | 'embassy'
    | 'fire_station'
    | 'florist'
    | 'funeral_home'
    | 'furniture_store'
    | 'gas_station'
    | 'gym'
    | 'hair_care'
    | 'hardware_store'
    | 'hindu_temple'
    | 'home_goods_store'
    | 'hospital'
    | 'insurance_agency'
    | 'jewelry_store'
    | 'laundry'
    | 'lawyer'
    | 'library'
    | 'liquor_store'
    | 'local_government_office'
    | 'locksmith'
    | 'lodging'
    | 'meal_delivery'
    | 'meal_takeaway'
    | 'mosque'
    | 'movie_rental'
    | 'movie_theater'
    | 'moving_company'
    | 'museum'
    | 'night_club'
    | 'painter'
    | 'park'
    | 'parking'
    | 'pet_store'
    | 'pharmacy'
    | 'physiotherapist'
    | 'plumber'
    | 'police'
    | 'post_office'
    | 'real_estate_agency'
    | 'restaurant'
    | 'roofing_contractor'
    | 'rv_park'
    | 'school'
    | 'shoe_store'
    | 'shopping_mall'
    | 'spa'
    | 'stadium'
    | 'storage'
    | 'store'
    | 'subway_station'
    | 'supermarket'
    | 'synagogue'
    | 'taxi_stand'
    | 'train_station'
    | 'transit_station'
    | 'travel_agency'
    | 'veterinary_care'
    | 'zoo';

  /**
   * Place Type, corresponding to the
   * [return types table 2](https://developers.google.com/places/ios-sdk/supported_types).
   *
   * The following types may be returned anywhere a Place result is returned
   * (for example a [Find Place](https://developers.google.com/places/web-service/search#FindPlaceRequests)
   * request), in addition to the types in table 1 above. These types are also
   * used for address components.
   *
   * For more details on these types, refer to
   * [Address Types](https://developers.google.com/maps/documentation/geocoding/intro#Types).
   *
   * Note: The types below are not supported in the type filter of a place
   * search.
   */
  export type ReadOnlyPlaceType =
    | 'administrative_area_level_1'
    | 'administrative_area_level_2'
    | 'administrative_area_level_3'
    | 'administrative_area_level_4'
    | 'administrative_area_level_5'
    | 'colloquial_area'
    | 'country'
    | 'establishment'
    | 'finance'
    | 'floor'
    | 'food'
    | 'general_contractor'
    | 'geocode'
    | 'health'
    | 'intersection'
    | 'locality'
    | 'natural_feature'
    | 'neighborhood'
    | 'place_of_worship'
    | 'political'
    | 'point_of_interest'
    | 'post_box'
    | 'postal_code'
    | 'postal_code_prefix'
    | 'postal_code_suffix'
    | 'postal_town'
    | 'premise'
    | 'room'
    | 'route'
    | 'street_address'
    | 'street_number'
    | 'sublocality'
    | 'sublocality_level_4'
    | 'sublocality_level_5'
    | 'sublocality_level_3'
    | 'sublocality_level_2'
    | 'sublocality_level_1'
    | 'subpremise';

  /**
   * All the different kinds of place types that the Places API can return.
   */
  export type ReturnPlaceType = ReadOnlyPlaceType | PlaceType;

  /**
   * Place Type, corresponding to the
   * [return types table 3](https://developers.google.com/places/ios-sdk/supported_types).
   *
   * You may restrict results from a Place Autocomplete request to be of a
   * certain type by passing a `types` parameter. The parameter specifies a
   * type or a type collection, as listed in the supported types below. If
   * nothing is specified, all types are returned. In general only a single
   * type is allowed. The exception is that you can safely mix the `geocode`
   * and `establishment` types, but note that this will have the same effect
   * as specifying no types.
   *
   * ## `geocode`
   *
   * Instructs the Place Autocomplete service to return only geocoding
   * results, rather than business results. Generally, you use this request
   * to disambiguate results where the location specified may be
   * indeterminate.
   *
   * ## `address`
   *
   * Instructs the Place Autocomplete service to return only geocoding
   * results with a precise address. Generally, you use this request when
   * you know the user will be looking for a fully specified address.
   *
   * ## `establishment`
   *
   * Instructs the Place Autocomplete service to return only business
   * results.
   *
   * ## `regions`
   *
   * Instructs the Places service to return any result matching the
   * following types:
   *
   * - `locality` (PlaceType.Locality)
   * - `sublocality` (PlaceType.SubLocality)
   * - `postal_code` (PlaceType.PostalCode)
   * - `country` (PlaceType.Country)
   * - `administrative_area_level_1` (PlaceType.AdministrativeAreaLevel1)
   * - `administrative_area_level_2` (PlaceType.AdministrativeAreaLevel2)
   *
   * ## `cities`
   *
   * Instructs the Places service to return results that match `locality`
   * (PlaceType.Locality) or `administrative_area_level_3`
   * (PlaceType.AdministrativeAreaLevel3).
   */
  export type AutocompleteType =
    | 'geocode'
    | 'address'
    | 'establishment'
    | 'regions'
    | 'cities';

  /**
   * Internal mapping to [AddressComponent](https://developers.google.com/places/ios-sdk/reference/interface_g_m_s_address_component)
   * and [com.google.android.libraries.places.api.model.AddressComponent](https://developers.google.com/places/android-sdk/reference/com/google/android/libraries/places/api/model/AddressComponent).
   *
   * Represents a component of an address, e.g., street number, postcode,
   * city, etc.
   */
  export interface AddressComponent {
    /**
     * Type of the address component.
     */
    types: ReturnPlaceType[];

    /**
     * Name of the address component, e.g. "Sydney".
     */
    name: string;

    /**
     * Short name of the address component, e.g. "AU".
     */
    shortName: string;
  }

  /**
   * Internal mapping to [GMSAutocompletePrediction](https://developers.google.com/places/ios-sdk/reference/https://developers.google.com/places/ios-sdk/reference/interface_g_m_s_autocomplete_prediction)
   * and [com.google.android.libraries.places.api.model.AutocompletePrediction](https://developers.google.com/places/android-sdk/reference/com/google/android/libraries/places/api/model/AutocompletePrediction).
   *
   * Represents an autocomplete suggestion of a place, based on a particular
   * text query.
   *
   * An AutocompletePrediction includes the description of the suggested place
   * as well as basic details including place ID and types.
   */
  // AutocompletePrediction interface removed from here
}

// PlaceFields type is removed as it's now exported from src/index.ts,
// deriving from the Place type in NativeGooglePlacesCompat.ts.

/**
 * Google Places options.
 *
 * This type roughly corresponds to
 * [GMSAutocompleteFilter](https://developers.google.com/places/ios-sdk/reference/interface_g_m_s_autocomplete_filter)
 * and [com.google.android.libraries.places.widget.Autocomplete](https://developers.google.com/places/android-sdk/reference/com/google/android/libraries/places/widget/Autocomplete).
 */
export interface RNGooglePlacesNativeOptions {
  /**
   * @Deprecated: Use filterTypes instead.
   * The type of results to return.
   */
  type: GMSTypes.AutocompleteType | null;

  /**
   * The filter applied to an autocomplete request to restrict results using up to 5 different place types.
   */
  types: Array<
    GMSTypes.PlaceType | GMSTypes.ReadOnlyPlaceType | GMSTypes.AutocompleteType
  > | null;

  /**
   * @Deprecated: Use countries instead.
   *
   * Limit results to a specific country using a
   * [ISO 3166-1 Alpha-2 country code](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)
   * (case-insensitive). If this is not set, no country filtering will take
   * place.
   */
  country: string;

  /**
   *
   * The countries to restrict results to.
   * This should be a ISO 3166-1 Alpha-2 country code (case-insensitive).
   * Supports up to 5 countries to filter. If nil, no country filtering will take place.
   */
  countries: string[] | null;

  /**
   * If true, the autocomplete modal will
   * [open as an overlay rather than fullscreen](https://developers.google.com/places/images/acw_overlay.png).
   *
   * Note: This property is Android only.
   */
  useOverlay: boolean;

  /**
   * If present, the autocomplete modal would launch with results
   * pre-populated for the query passed.
   *
   * Note: This property is Android only.
   */
  initialQuery: string;

  /**
   * To bias autocomplete results to a specific geographic region.
   *
   * Note: On iOS, only one of locationBias or locationRestriction is
   * respected, when passing both, only the first passed option would be used.
   */
  locationBias: GeoCoordinateBounds;

  /**
   * To restrict autocomplete results to a specific geographic region.
   *
   * Note: On iOS, only one of locationBias or locationRestriction is
   * respected, when passing both, only the first passed option would be used.
   */
  locationRestriction: GeoCoordinateBounds;
}
