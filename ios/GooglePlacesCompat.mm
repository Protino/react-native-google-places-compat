#import "RNGooglePlacesCompatSpec.h" // Import the generated spec header
#import <React/RCTLog.h>
#import <React/RCTUtils.h> // For RCTPromise*

// Import the Swift bridging header. The name is typically <ProductName>-Swift.h
// Assuming the product name for the library is "GooglePlacesCompat"
// If the example app is the one building, it might be "GooglePlacesCompatExample-Swift.h"
// For a library, it's common to have its own bridging header if Swift calls ObjC,
// but for ObjC calling Swift, this import in the .mm file is key.
// Let's assume the library's product module name is GooglePlacesCompat.
#import "GooglePlacesCompat-Swift.h"


NS_ASSUME_NONNULL_BEGIN

// Use the same name as in package.json's codegenConfig and the JS spec.
@implementation RNGooglePlaces (RNGooglePlacesCompatSpec)

RCT_EXPORT_MODULE(RNGooglePlacesCompat)

// Make sure the Swift class is also named RNGooglePlaces
// Or get a handle to the Swift instance.
// The Swift class has `static var instance: RNGooglePlaces?`
// We'll rely on the Swift class to manage its instance.

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    // This is a simplified way to get the Swift instance.
    // In a more complex setup, you might manage the instance differently.
    // The key is that the Swift class `RNGooglePlaces` should handle the actual logic.
    // We assume the Swift `RNGooglePlaces.instance` is initialized appropriately (e.g. in its init or via an app delegate call).
    // If `RNGooglePlaces.instance` is nil, this will fail.
    // A more robust approach might involve ensuring the instance is created if nil,
    // or that the module is initialized in a way that guarantees the instance exists.
    
    // For TurboModules, the native module instance is often created here.
    // Let's ensure we have a Swift instance. The Swift `init` creates one.
    RNGooglePlaces *swiftModule = [RNGooglePlaces new]; // This will set RNGooglePlaces.instance via Swift's init()
    
    return std::make_shared<facebook::react::CommTurboModule>(swiftModule, params.jsCallInvoker);
}

- (void)initializePlaceClient:(NSString *)apiKey sessionBasedAutocomplete:(@Nullable NSNumber *)sessionBasedAutocomplete {
    [[RNGooglePlaces instance] initializePlaceClient:apiKey sessionBasedAutocomplete:sessionBasedAutocomplete ?: @(NO)];
}

- (void)refreshSessionToken {
    [[RNGooglePlaces instance] refreshSessionToken];
}

- (void)setSessionBasedAutocomplete:(BOOL)enabled {
    [[RNGooglePlaces instance] setSessionBasedAutocomplete:@(enabled)];
}

- (void)openAutocompleteModal:(NSDictionary *)options
                       fields:(NSArray<NSString *> *)fields
                     resolver:(RCTPromiseResolveBlock)resolve
                     rejecter:(RCTPromiseRejectBlock)reject {
    [[RNGooglePlaces instance] openAutocompleteModal:options withFields:fields resolver:resolve rejecter:reject];
}

- (void)getAutocompletePredictions:(NSString *)query
                           options:(NSDictionary *)options
                          resolver:(RCTPromiseResolveBlock)resolve
                          rejecter:(RCTPromiseRejectBlock)reject {
    [[RNGooglePlaces instance] getAutocompletePredictions:query filterOptions:options resolver:resolve rejecter:reject];
}

- (void)lookUpPlaceByID:(NSString *)placeID
                 fields:(NSArray<NSString *> *)fields
               resolver:(RCTPromiseResolveBlock)resolve
               rejecter:(RCTPromiseRejectBlock)reject {
    [[RNGooglePlaces instance] lookUpPlaceByID:placeID withFields:fields resolver:resolve rejecter:reject];
}

- (void)getCurrentPlace:(NSArray<NSString *> *)fields
               resolver:(RCTPromiseResolveBlock)resolve
               rejecter:(RCTPromiseRejectBlock)reject {
    [[RNGooglePlaces instance] getCurrentPlace:fields resolver:resolve rejecter:reject];
}

// Required for main queue setup if not overridden in Swift.
// The Swift class has `static func requiresMainQueueSetup() -> Bool { return false }`
// and `func methodQueue() -> DispatchQueue { return DispatchQueue.main }`
// For TurboModules, methodQueue is usually respected.
// If specific queue setup is needed beyond what RCTCxxModule/CommTurboModule provides by default
// (which is background for native methods, main for view managers), it's handled differently.
// For now, relying on the Swift implementation and default TurboModule behavior.
// The `requiresMainQueueSetup` from the old arch is less relevant here.

@end

NS_ASSUME_NONNULL_END
