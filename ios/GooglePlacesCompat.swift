import Foundation
import GooglePlaces
import CoreLocation

@objc(RNGooglePlaces)
class RNGooglePlaces: NSObject, CLLocationManagerDelegate {

    static var instance: RNGooglePlaces?
    var token: GMSAutocompleteSessionToken?
    var locationManager: CLLocationManager!

    override init() {
        super.init()
        RNGooglePlaces.instance = self
        locationManager = CLLocationManager()
        locationManager.delegate = self
    }

    deinit {
        locationManager.delegate = nil
    }

    @objc static func moduleName() -> String {
        return "RNGooglePlaces"
    }

    @objc static func requiresMainQueueSetup() -> Bool {
        return false
    }

    @objc func methodQueue() -> DispatchQueue {
        return DispatchQueue.main
    }

    @objc func initializePlaceClient(_ apiKey: String) {
        GMSPlacesClient.provideAPIKey(apiKey)
    }

    @objc func beginAutocompleteSession() {
        token = GMSAutocompleteSessionToken.init()
    }

    @objc func endAutocompleteSession() {
        token = nil
    }

    @objc func isAutoCompleteSessionStarted(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        resolve(token != nil)
    }

    @objc func openAutocompleteModal(_ options: NSDictionary, withFields fields: [String], resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        do {
            // Assume RNGooglePlacesViewController has been translated to Swift
            let acController = RNGooglePlacesViewController()

            let selectedFields = self.getSelectedFields(fields, isCurrentOrFetchPlace: false)
            let autocompleteFilter = GMSAutocompleteFilter()
            autocompleteFilter.types = options["types"] as? [String] ?? nil
            autocompleteFilter.countries = options["countries"] as? [String] ?? nil
            autocompleteFilter.locationBias = getLocationOption(from: options, forKey: "locationBias")
            autocompleteFilter.locationRestriction = getLocationOption(from: options, forKey: "locationRestriction")

            acController.openAutocompleteModal(autocompleteFilter: autocompleteFilter,
                                               placeFields: selectedFields,
                                               resolver: resolve,
                                               rejecter: reject)
        } catch let error as NSError {
            reject("E_OPEN_FAILED", "Could not open modal", error)
        }
    }

    @objc func getAutocompletePredictions(_ query: String, filterOptions: NSDictionary, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        var autoCompleteSuggestionsList = [[String: Any]]()
        let autocompleteFilter = GMSAutocompleteFilter()
        autocompleteFilter.types = filterOptions["types"] as? [String]
        autocompleteFilter.countries = filterOptions["countries"] as? [String]
        autocompleteFilter.locationBias = getLocationOption(from: filterOptions, forKey: "locationBias")
        autocompleteFilter.locationRestriction = getLocationOption(from: filterOptions, forKey: "locationRestriction")

        GMSPlacesClient.shared().findAutocompletePredictions(fromQuery: query, filter: autocompleteFilter, sessionToken: token, callback: { results, error in
            if let error = error {
                reject("E_AUTOCOMPLETE_ERROR", error.localizedDescription, error)
                return
            }

            if let results = results {
                for result in results {
                    var placeData = [String: Any]()

                    placeData["fullText"] = result.attributedFullText.string
                    placeData["primaryText"] = result.attributedPrimaryText.string
                    placeData["secondaryText"] = result.attributedSecondaryText?.string
                    placeData["placeID"] = result.placeID
                    placeData["types"] = result.types

                    autoCompleteSuggestionsList.append(placeData)
                }

                resolve(autoCompleteSuggestionsList)
            } else {
                reject("E_AUTOCOMPLETE_ERROR", "No results found", nil)
            }
        })
    }

    @objc func lookUpPlaceByID(_ placeID: String, withFields fields: [String], resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        let selectedFields = getSelectedFields(fields, isCurrentOrFetchPlace: false)

        GMSPlacesClient.shared().fetchPlace(fromPlaceID: placeID, placeFields: selectedFields, sessionToken: nil) { (place, error) in
            if let error = error {
                reject("E_PLACE_DETAILS_ERROR", error.localizedDescription, nil)
                return
            }

            if let place = place {
                resolve(place.toDictionary())
            } else {
                resolve([:])
            }
        }
    }

    @objc func getCurrentPlace(_ fields: [String], resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        self.locationManager.requestAlwaysAuthorization()

        let selectedFields = getSelectedFields(fields, isCurrentOrFetchPlace: true)

        var likelyPlacesList = [NSDictionary]()

        GMSPlacesClient.shared().findPlaceLikelihoodsFromCurrentLocation(withPlaceFields: selectedFields) { (likelihoods, error) in
            if let error = error {
                reject("E_CURRENT_PLACE_ERROR", error.localizedDescription, nil)
                return
            }

            if let likelihoods = likelihoods {
                for likelihood in likelihoods {
                    var placeData = likelihood.place.toDictionary()
                    placeData["likelihood"] = NSNumber(value: likelihood.likelihood)

                    likelyPlacesList.append(placeData)
                }
            }

            resolve(likelyPlacesList)
        }
    }

    private func getSelectedFields(_ fields: [String], isCurrentOrFetchPlace currentOrFetch: Bool) -> GMSPlaceField {
        let fieldsMapping: [String: GMSPlaceField] = [
            "name": .name,
            "placeID": .placeID,
            "plusCode": .plusCode,
            "location": .coordinate,
            "openingHours": .openingHours,
            "phoneNumber": .phoneNumber,
            "address": .formattedAddress,
            "rating": .rating,
            "userRatingsTotal": .userRatingsTotal,
            "priceLevel": .priceLevel,
            "types": .types,
            "website": .website,
            "viewport": .viewport,
            "addressComponents": .addressComponents,
            "photos": .photos
        ]

        // Default to all fields if not filtering based on currentOrFetch and fields are empty
        guard !fields.isEmpty else {
            return currentOrFetch ? .all.subtracting([.openingHours, .phoneNumber, .website, .addressComponents]) : .all
        }

        // Compute the fields for non-empty input
        let selectedFields: GMSPlaceField = fields.reduce(into: GMSPlaceField()) { result, fieldName in
            if let fieldValue = fieldsMapping[fieldName] {
                // If currentOrFetch is true, exclude specific fields
                if currentOrFetch && [.openingHours, .phoneNumber, .website, .addressComponents].contains(fieldValue) {
                    return
                }
                result.insert(fieldValue)
            }
        }

        return selectedFields
    }


    private func errorFromException(exception: NSException) -> NSError {
        let exceptionInfo = [
            "name": exception.name,
            "reason": exception.reason ?? "",
            "callStackReturnAddresses": exception.callStackReturnAddresses,
            "callStackSymbols": exception.callStackSymbols,
            "userInfo": exception.userInfo ?? [:]
        ] as [String : Any]

        return NSError(domain: "RNGooglePlaces", code: 0, userInfo: exceptionInfo)
    }

    private func getLocationOption(from filterOptions: NSDictionary, forKey key: String) -> LocationOption? {
        guard let locationData = filterOptions[key] as? [String: Double] else {
            return nil
        }

        let latitudeNE = locationData["latitudeNE"] ?? 0.0
        let longitudeNE = locationData["longitudeNE"] ?? 0.0
        let latitudeSW = locationData["latitudeSW"] ?? 0.0
        let longitudeSW = locationData["longitudeSW"] ?? 0.0

        // Check that the coordinates are valid before creating the bounds
        if latitudeNE != 0, longitudeNE != 0, latitudeSW != 0, longitudeSW != 0 {
            let neBoundsCorner = CLLocationCoordinate2D(latitude: latitudeNE, longitude: longitudeNE)
            let swBoundsCorner = CLLocationCoordinate2D(latitude: latitudeSW, longitude: longitudeSW)

            return GMSPlaceRectangularLocationOption(neBoundsCorner, swBoundsCorner)
        }

        return nil
    }
}
