#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNGooglePlaces, NSObject)

RCT_EXTERN_METHOD(initializePlaceClient:(NSString *)apiKey
                  sessionBasedAutocomplete:(nonnull NSNumber *)enabled)


RCT_EXTERN_METHOD(openAutocompleteModal:(NSDictionary *)options
                  withFields:(NSArray<NSString *> *)fields
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getAutocompletePredictions:(NSString *)query
                  filterOptions:(NSDictionary *)filterOptions
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(lookUpPlaceByID:(NSString *)placeID
                  withFields:(NSArray<NSString *> *)fields
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getCurrentPlace:(NSArray<NSString *> *)fields
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(setSessionBasedAutocomplete:(nonnull NSNumber *)enabled)

RCT_EXTERN_METHOD(refreshSessionToken)


+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
