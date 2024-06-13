import React, { useState, useEffect } from 'react';
import {
  View,
  TextInput,
  ScrollView,
  Text,
  StyleSheet,
  Pressable,
} from 'react-native';
import { useDebounce } from './Utils';
import RNGooglePlacesCompat from 'react-native-google-places-compat';
import type { GMSTypes } from '../../src/types';
import { LOCATION_ONLY_FIELDS } from '../../src/';

export default function SearchScreen() {
  const [query, setQuery] = useState('');

  const [predictions, setPredictions] = useState<
    GMSTypes.AutocompletePrediction[]
  >([]);
  const [selectedPlace, setSelectedPlace] = useState<GMSTypes.Place>();

  const debouncedQuery = useDebounce(query, 500); // Debounce query with a 500ms delay

  useEffect(() => {
    RNGooglePlacesCompat.setSessionBasedAutocomplete(true);
  }, []);

  useEffect(() => {
    const fetchPredictions = async () => {
      if (debouncedQuery) {
        try {
          const results = await RNGooglePlacesCompat.getAutocompletePredictions(
            debouncedQuery,
            {
              countries: ['US', 'IN'],
            }
          );
          setPredictions(results);
        } catch (error) {
          console.error(error);
          setPredictions([]);
        }
      } else {
        setPredictions([]);
      }
    };

    fetchPredictions();
  }, [debouncedQuery]); // Effect depends on the debounced query

  const selectPlace = async (placeId: string) => {
    try {
      setPredictions([]);
      console.log('Searching place by id - ', placeId);
      const place = await RNGooglePlacesCompat.lookUpPlaceByID(
        placeId,
        LOCATION_ONLY_FIELDS
      );
      setSelectedPlace(place);
      console.log('Place found - ', place);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.searchBar}
        onChangeText={setQuery}
        value={query}
        placeholder="Search places"
        placeholderTextColor="#999"
      />
      <ScrollView>
        {predictions.map((prediction) => (
          <Pressable
            key={prediction.placeID}
            style={styles.predictionItem}
            onPress={() => selectPlace(prediction.placeID)}
          >
            <Text>{prediction.primaryText}</Text>
            <Text>{prediction.secondaryText}</Text>
          </Pressable>
        ))}
      </ScrollView>
      {selectedPlace && (
        <View style={styles.placeDetails}>
          <Text style={styles.placeName}>{selectedPlace.name}</Text>
          <Text>{selectedPlace.address}</Text>
          <Text>
            {selectedPlace.location.latitude},{selectedPlace.location.longitude}
          </Text>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 10,
    width: '100%',
  },
  searchBar: {
    height: 40,
    width: '100%',
    borderColor: 'gray',
    borderWidth: 1,
    paddingLeft: 10,
    borderRadius: 5,
  },
  predictionItem: {
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    color: '#777',
  },
  placeDetails: {
    marginTop: 20,
    alignItems: 'center',
  },
  placeName: {
    fontSize: 18,
    fontWeight: 'bold',
  },
});
