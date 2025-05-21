#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTTypedModuleConstants.h> // For JSI
#import <ReactCommon/RCTTurboModule.h> // For TurboModule

NS_ASSUME_NONNULL_BEGIN

// Forward declaration
@class JSCRuntime;

// Options type (simplified as NSDictionary for now, as in the existing .mm)
using RNGooglePlacesCompatAutoCompleteOptions = NSDictionary *;
// Place type (simplified)
using RNGooglePlacesCompatPlace = NSDictionary *;
// AutocompletePrediction type (simplified)
using RNGooglePlacesCompatAutocompletePrediction = NSDictionary *;


// This is the protocol that the NativeModule an iOS TurboModule must comply with.
// The name of the protocol is <MODULE_NAME>Spec.
@protocol RNGooglePlacesCompatSpec <RCTTurboModule>

// Initializes the Places client.
// sessionBasedAutocomplete is optional and defaults to false.
- (void)initializePlaceClient:(NSString *)apiKey sessionBasedAutocomplete:(@Nullable NSNumber *)sessionBasedAutocomplete;

// Refreshes the session token if session-based autocomplete is enabled.
- (void)refreshSessionToken;

// Enables or disables session-based autocomplete.
- (void)setSessionBasedAutocomplete:(BOOL)enabled;

// Opens the autocomplete modal.
// 'options' and 'fields' types are simplified here.
// 'fields' is an array of strings representing Place.Field keys.
- (void)openAutocompleteModal:(RNGooglePlacesCompatAutoCompleteOptions)options
                       fields:(NSArray<NSString *> *)fields
                     resolver:(RCTPromiseResolveBlock)resolve
                     rejecter:(RCTPromiseRejectBlock)reject;

// Fetches autocomplete predictions.
- (void)getAutocompletePredictions:(NSString *)query
                           options:(RNGooglePlacesCompatAutoCompleteOptions)options
                          resolver:(RCTPromiseResolveBlock)resolve
                          rejecter:(RCTPromiseRejectBlock)reject;

// Looks up a place by its ID.
// 'fields' is an array of strings representing Place.Field keys.
- (void)lookUpPlaceByID:(NSString *)placeID
                 fields:(NSArray<NSString *> *)fields
               resolver:(RCTPromiseResolveBlock)resolve
               rejecter:(RCTPromiseRejectBlock)reject;

// Gets the current place.
// 'fields' is an array of strings representing Place.Field keys.
// The promise resolves to an array of Place objects, each with a 'likelihood'.
- (void)getCurrentPlace:(NSArray<NSString *> *)fields
               resolver:(RCTPromiseResolveBlock)resolve
               rejecter:(RCTPromiseRejectBlock)reject;

@end

NS_ASSUME_NONNULL_END
