# react-native-google-places-compat

An update of [react-native-google-places](https://github.com/tolu360/react-native-google-places) library, brings compatibility with newer React Native version (0.71.x). It addresses dependency and compatibility issues, ensuring seamless integration with Google Places API for React Native projects.

## Installation

```sh
npm install react-native-google-places-compat
```

## Usage

* First initialize the place client with your places SDK API key. Read [Places SDK docs](https://developers.google.com/maps/documentation/places/android-sdk/overview) for setup. Make sure to restrict your key usage as shown [here](https://developers.google.com/maps/documentation/places/android-sdk/get-api-key#restrict_key).
```js
  RNGooglePlacesCompat.initializePlaceClient(
    Platform.OS === 'ios'
      ? "your-ios-api-key"
      : "your-android-api-key"
  );
```

* Then you can use the following methods to interact with the places API:
```js
  RNGooglePlacesCompat.openAutocompleteModal()
  RNGooglePlacesCompat.getAutocompletePredictions()
  RNGooglePlacesCompat.getCurrentPlace()
  RNGooglePlacesCompat.lookUpPlaceByID()
```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
