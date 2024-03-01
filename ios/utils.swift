//
//  utils.swift
//  react-native-google-places-compat
//
//  Created by Gurupad Mamadapur on 28/02/24.
//

import Foundation
import GooglePlaces

// Define a typealias for the return type of GMSPlaceRectangularLocationOption for clarity
typealias LocationOption = GMSPlaceLocationBias & GMSPlaceLocationRestriction

extension GMSPlace {

    func toDictionary() -> NSMutableDictionary {
        let placeData = NSMutableDictionary()

        if let name = self.name {
            placeData["name"] = name
        }

        if let formattedAddress = self.formattedAddress {
            placeData["address"] = formattedAddress
        }

        placeData["attributions"] = self.attributions?.string

        if CLLocationCoordinate2DIsValid(self.coordinate) {
            let locationMap: [String: Double] = [
                "latitude": self.coordinate.latitude,
                "longitude": self.coordinate.longitude
            ]
            placeData["location"] = locationMap
        }

        if let phoneNumber = self.phoneNumber {
            placeData["phoneNumber"] = phoneNumber
        }

        if let website = self.website {
            placeData["website"] = website.absoluteString
        }

        if let placeID = self.placeID {
            placeData["placeID"] = placeID
        }

        if let types = self.types {
            placeData["types"] = types
        }

        if self.rating > 0 {
            placeData["rating"] = self.rating
        }

        if let viewport = self.viewportInfo {
            let viewportMap: [String: Double] = [
                "latitudeNE": viewport.northEast.latitude,
                "longitudeNE": viewport.northEast.longitude,
                "latitudeSW": viewport.southWest.latitude,
                "longitudeSW": viewport.southWest.longitude
            ]
            placeData["viewport"] = viewportMap
        }

        if let plusCode = self.plusCode {
            let plusCodeMap: [String: String?] = [
                "globalCode": plusCode.globalCode,
                "compoundCode": plusCode.compoundCode
            ]
            placeData["plusCode"] = plusCodeMap
        }

        if let addressComponents = self.addressComponents {
            var componentsArray = [[String: Any]]()

            for component in addressComponents {
                var componentDict = [String: Any]()
                componentDict["types"] = component.types
                componentDict["name"] = component.name
                componentDict["shortName"] = component.shortName

                componentsArray.append(componentDict)
            }

            placeData["addressComponents"] = componentsArray
        }

        if let openingHours = self.openingHours {
            placeData["openingHours"] = openingHours.weekdayText
        }

        placeData["userRatingsTotal"] = self.userRatingsTotal

        return placeData
    }
}
