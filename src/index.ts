// Assuming the interfaces and types from your index.d.ts or similar have been defined
import { NativeModules } from 'react-native';
import type {
  GMSTypes,
  PlaceFields,
  CurrentPlace,
  RNGooglePlacesNativeOptions,
} from './types';

const RNGooglePlacesNative = NativeModules.RNGooglePlaces;

class RNGooglePlaces {
  static optionsDefaults: RNGooglePlacesNativeOptions = {
    type: null,
    types: null,
    country: '',
    countries: null,
    useOverlay: false,
    initialQuery: '',
    locationBias: {
      latitudeSW: 0,
      longitudeSW: 0,
      latitudeNE: 0,
      longitudeNE: 0,
    },
    locationRestriction: {
      latitudeSW: 0,
      longitudeSW: 0,
      latitudeNE: 0,
      longitudeNE: 0,
    },
  };

  static placeFieldsDefaults: (keyof GMSTypes.Place)[] = [];

  initializePlaceClient(
    apiKey: string,
    sessionBasedAutocomplete: boolean = false
  ) {
    RNGooglePlacesNative.initializePlaceClient(
      apiKey,
      sessionBasedAutocomplete
    );
  }

  openAutocompleteModal(
    options: Partial<RNGooglePlacesNativeOptions> = {},
    placeFields: PlaceFields[] = []
  ): Promise<GMSTypes.Place> {
    return RNGooglePlacesNative.openAutocompleteModal(
      {
        ...RNGooglePlaces.optionsDefaults,
        ...options,
      },
      [...RNGooglePlaces.placeFieldsDefaults, ...placeFields]
    );
  }

  getAutocompletePredictions(
    query: string,
    options: Partial<RNGooglePlacesNativeOptions> = {}
  ): Promise<GMSTypes.AutocompletePrediction[]> {
    return RNGooglePlacesNative.getAutocompletePredictions(query, {
      ...RNGooglePlaces.optionsDefaults,
      ...options,
    });
  }

  lookUpPlaceByID(
    placeID: string,
    placeFields: PlaceFields[] = []
  ): Promise<GMSTypes.Place> {
    return RNGooglePlacesNative.lookUpPlaceByID(placeID, [
      ...RNGooglePlaces.placeFieldsDefaults,
      ...placeFields,
    ]);
  }

  getCurrentPlace(placeFields: PlaceFields[] = []): Promise<CurrentPlace[]> {
    return RNGooglePlacesNative.getCurrentPlace([
      ...RNGooglePlaces.placeFieldsDefaults,
      ...placeFields,
    ]);
  }

  setSessionBasedAutocomplete(enabled: boolean) {
    RNGooglePlacesNative.setSessionBasedAutocomplete(enabled);
    this.refreshSessionToken();
  }

  refreshSessionToken() {
    RNGooglePlacesNative.refreshSessionToken();
  }
}

const RNGooglePlacesCompat = new RNGooglePlaces();
export type { GMSTypes };
export default RNGooglePlacesCompat;
