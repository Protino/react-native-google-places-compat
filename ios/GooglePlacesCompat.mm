#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNGooglePlaces, NSObject)

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

RCT_EXTERN_METHOD(beginAutocompleteSession)

RCT_EXTERN_METHOD(endAutocompleteSession)

RCT_EXTERN_METHOD(isAutoCompleteSessionStarted:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)


+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
