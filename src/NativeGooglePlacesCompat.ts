import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

// Define the structure of the options for openAutocompleteModal and getAutocompletePredictions
// These are based on common patterns and would ideally be more strictly typed
// based on the actual structure used in the JS side of the original library.
// For now, using generic Object.
interface AutoCompleteOptions {
  type?: string;
  country?: string; // Or countries: string[] depending on original lib
  useOverlay?: boolean;
  initialQuery?: string;
  // Add other potential fields from 'getAutoCompletePredictionsOptions' like
  // locationBias, locationRestriction, filterTypes, etc.
  // Using 'any' for now as the exact structure isn't fully clear from the Java code alone.
  [key: string]: any;
}

// Define the structure for a Place object (simplified)
interface Place {
  address?: string;
  addressComponents?: Array<Object>;
  name?: string;
  placeID?: string;
  location?: {
    latitude: number;
    longitude: number;
  };
  phoneNumber?: string;
  website?: string;
  types?: Array<string>;
  viewport?: {
    latitudeNE: number;
    longitudeNE: number;
    latitudeSW: number;
    longitudeSW: number;
  };
  priceLevel?: number;
  rating?: number;
  openingHours?: Array<string>;
  plusCode?: {
    compoundCode?: string;
    globalCode?: string;
  };
  userRatingsTotal?: number;
  attributions?: Array<string>;
  // likelihood is specific to getCurrentPlace results
  likelihood?: number;
  // fullText, primaryText, secondaryText are specific to autocomplete predictions
  fullText?: string;
  primaryText?: string;
  secondaryText?: string;
}

// Define the structure for AutocompletePrediction
interface AutocompletePrediction {
  fullText: string;
  primaryText: string;
  secondaryText: string;
  placeID: string;
  types: Array<string>;
}


export interface Spec extends TurboModule {
  // Initializes the Places client.
  // sessionBasedAutocomplete is optional and defaults to false.
  initializePlaceClient(apiKey: string, sessionBasedAutocomplete?: boolean): void;

  // Refreshes the session token if session-based autocomplete is enabled.
  refreshSessionToken(): void;

  // Enables or disables session-based autocomplete.
  setSessionBasedAutocomplete(enabled: boolean): void;

  // Opens the autocomplete modal.
  // 'options' and 'fields' types are simplified here.
  // A more specific interface for 'options' should be defined based on actual usage.
  // 'fields' is an array of strings representing Place.Field keys.
  openAutocompleteModal(options: AutoCompleteOptions, fields: Array<string>): Promise<Place>;

  // Fetches autocomplete predictions.
  // 'options' type is simplified.
  getAutocompletePredictions(query: string, options: AutoCompleteOptions): Promise<Array<AutocompletePrediction>>;

  // Looks up a place by its ID.
  // 'fields' is an array of strings representing Place.Field keys.
  lookUpPlaceByID(placeID: string, fields: Array<string>): Promise<Place>;

  // Gets the current place.
  // 'fields' is an array of strings representing Place.Field keys.
  // The promise resolves to an array of Place objects, each with a 'likelihood'.
  getCurrentPlace(fields: Array<string>): Promise<Array<Place>>;
}

export default TurboModuleRegistry.get<Spec>('RNGooglePlacesCompat') as Spec | null;
