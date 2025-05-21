import NativeModule, {
  type Spec,
  type Place,
  type AutocompletePrediction,
  type AutoCompleteOptions,
} from './NativeGooglePlacesCompat'; // Assuming NativeGooglePlacesCompat.ts exports the TurboModule instance as default

// Re-export types from the spec for external use if needed, or define new ones.
// For now, let's assume the types from NativeGooglePlacesCompat.ts are the source of truth.
export type { Place, AutocompletePrediction, AutoCompleteOptions };

// It's good practice to ensure the native module is available.
const RNGooglePlacesCompatModule = NativeModule;

if (!RNGooglePlacesCompatModule) {
  // Fallback for older RN versions or if TurboModule linkage fails.
  // This part might need adjustment based on how the old arch was handled.
  // For simplicity, we'll throw an error if the TurboModule isn't found.
  // In a real library, you might have a bridge to the old NativeModules.RNGooglePlaces.
  throw new Error(
    'Failed to load NativeGooglePlacesCompat module. Did you properly link the library? Or is the New Architecture not enabled?'
  );
}

// Define PlaceFields based on the keys of the Place interface from the spec
// This makes it more type-safe and aligned with the actual data.
// We need to be careful here, as PlaceFields was used as an array of strings.
// The native side expects string keys.
export type PlaceFields = keyof Place; // This creates a union of string literals

// Keep this if it's still relevant for users, but ensure its values are valid PlaceFields.
export const LOCATION_ONLY_FIELDS: PlaceFields[] = [
  'addressComponents',
  'address',
  'location',
  'plusCode',
  'types',
  'viewport',
];

class RNGooglePlaces {
  // Default options should align with AutoCompleteOptions from the spec
  static optionsDefaults: AutoCompleteOptions = {
    // type: undefined, // Let native side handle defaults if not provided
    // types: undefined,
    country: '', // Or undefined
    // countries: undefined,
    useOverlay: false,
    initialQuery: '',
    locationBias: undefined, // Use undefined for optional complex objects
    locationRestriction: undefined,
  };

  // Default fields for place requests - can be an empty array or predefined common fields.
  // Ensure these are valid keys of the `Place` interface.
  static placeFieldsDefaults: Array<string> = []; // Keep as string array for native module

  initializePlaceClient(
    apiKey: string,
    sessionBasedAutocomplete: boolean = false
  ): void {
    RNGooglePlacesCompatModule.initializePlaceClient(
      apiKey,
      sessionBasedAutocomplete
    );
  }

  async openAutocompleteModal(
    options: Partial<AutoCompleteOptions> = {},
    placeFields: string[] = [] // string array matching native expectation
  ): Promise<Place> {
    const mergedOptions: AutoCompleteOptions = {
      ...RNGooglePlaces.optionsDefaults,
      ...options,
    };
    // Ensure all fields are strings
    const mergedFields = [
      ...RNGooglePlaces.placeFieldsDefaults,
      ...placeFields,
    ].map(String);
    return RNGooglePlacesCompatModule.openAutocompleteModal(mergedOptions, mergedFields);
  }

  async getAutocompletePredictions(
    query: string,
    options: Partial<AutoCompleteOptions> = {}
  ): Promise<Array<AutocompletePrediction>> {
    const mergedOptions: AutoCompleteOptions = {
      ...RNGooglePlaces.optionsDefaults,
      ...options,
    };
    return RNGooglePlacesCompatModule.getAutocompletePredictions(query, mergedOptions);
  }

  async lookUpPlaceByID(
    placeID: string,
    placeFields: string[] = [] // string array
  ): Promise<Place> {
    // Ensure all fields are strings
    const mergedFields = [
      ...RNGooglePlaces.placeFieldsDefaults,
      ...placeFields,
    ].map(String);
    return RNGooglePlacesCompatModule.lookUpPlaceByID(placeID, mergedFields);
  }

  async getCurrentPlace(placeFields: string[] = []): Promise<Array<Place>> {
    // Ensure all fields are strings
    const mergedFields = [
      ...RNGooglePlaces.placeFieldsDefaults,
      ...placeFields,
    ].map(String);
    return RNGooglePlacesCompatModule.getCurrentPlace(mergedFields);
  }

  setSessionBasedAutocomplete(enabled: boolean): void {
    RNGooglePlacesCompatModule.setSessionBasedAutocomplete(enabled);
    // The native module's setSessionBasedAutocomplete might already refresh the token if needed.
    // Calling refreshSessionToken() here might be redundant or intended.
    // For now, keeping it to match original behavior.
    this.refreshSessionToken();
  }

  refreshSessionToken(): void {
    RNGooglePlacesCompatModule.refreshSessionToken();
  }
}

const RNGooglePlacesInstance = new RNGooglePlaces();
// Export the instance as default, and potentially the class and types too.
export default RNGooglePlacesInstance;
