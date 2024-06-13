import React, { useEffect } from 'react';

import { Platform, SafeAreaView, StyleSheet } from 'react-native';
import RNGooglePlacesCompat from 'react-native-google-places-compat';
import SearchScreen from './AutoCompletePredictions';
import Config from '../config.json';

export default function App() {
  useEffect(() => {
    RNGooglePlacesCompat.initializePlaceClient(
      Platform.OS === 'ios'
        ? Config.IOS_PLACES_API_KEY
        : Config.ANDROID_PLACES_API_KEY,
      false
    );
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <SearchScreen />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
