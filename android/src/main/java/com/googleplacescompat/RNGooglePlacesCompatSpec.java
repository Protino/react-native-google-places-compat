package com.googleplacescompat;

import androidx.annotation.Nullable;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;

public abstract class RNGooglePlacesCompatSpec extends ReactContextBaseJavaModule implements TurboModule {
  public static final String NAME = "RNGooglePlacesCompat";

  public RNGooglePlacesCompatSpec(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public abstract void initializePlaceClient(String apiKey, @Nullable Boolean sessionBasedAutocomplete);

  @ReactMethod
  public abstract void refreshSessionToken();

  @ReactMethod
  public abstract void setSessionBasedAutocomplete(boolean enabled);

  @ReactMethod
  public abstract void openAutocompleteModal(ReadableMap options, ReadableArray fields, Promise promise);

  @ReactMethod
  public abstract void getAutocompletePredictions(String query, ReadableMap options, Promise promise);

  @ReactMethod
  public abstract void lookUpPlaceByID(String placeID, ReadableArray fields, Promise promise);

  @ReactMethod
  public abstract void getCurrentPlace(ReadableArray fields, Promise promise);
}
