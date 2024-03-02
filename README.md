# react-native-google-places-compat

An update of [react-native-google-places](https://github.com/tolu360/react-native-google-places) library, brings compatibility with newer React Native version (0.71.x). It addresses dependency and compatibility issues, ensuring seamless integration with Google Places API for React Native projects.

## Installation

```sh
npm install react-native-google-places-compat
```

## Usage

### Initialize the Google Places Client

Start by initializing the Google Places client with your Places SDK API key. Refer to the [Places SDK documentation](https://developers.google.com/maps/documentation/places/android-sdk/overview) for initial setup instructions. It's important to restrict your key usage as detailed [here](https://developers.google.com/maps/documentation/places/android-sdk/get-api-key#restrict_key). Replace `"your-ios-api-key"` and `"your-android-api-key"` with your actual API keys.
```js
  RNGooglePlacesCompat.initializePlaceClient(
    Platform.OS === 'ios'
      ? "your-ios-api-key"
      : "your-android-api-key"
  );
```

### Interacting with the Google Places API
Once initialized, you can use the following methods to interact with the Google Places API:

```js
  RNGooglePlacesCompat.openAutocompleteModal()
  RNGooglePlacesCompat.getAutocompletePredictions()
  RNGooglePlacesCompat.getCurrentPlace()
  RNGooglePlacesCompat.lookUpPlaceByID()
```
### Managing API Costs with Session Tokens
If you use `getAutocompletePredictions` utilize session tokens to optimize API costs. Start a new session before making autocomplete predictions and end it when you're finished to avoid unnecessary charges. Only fetching place details will incur a cost. Learn more about session tokens [here](https://developers.google.com/maps/documentation/places/android-sdk/session-tokens), and for details on usage and billing, read [here](https://developers.google.com/maps/documentation/places/android-sdk/usage-and-billing).

```js
  RNGooglePlacesCompat.beginAutocompleteSession();
  // RNGooglePlacesCompat.getAutocompletePredictions();
  // ... more prediction calls
  // Session is terminated if lookUpPlaceByID() is called
  // or you can end the session manually by calling
  RNGooglePlacesCompat.endAutocompleteSession();
```
Refer examples in the [example](/example/src/AutoCompletePredictions.tsx) directory for more details.
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
