//
//  RNGooglePlacesViewController.swift
//  react-native-google-places-compat
//
//  Created by Gurupad Mamadapur on 01/03/24.
//

import Foundation
import UIKit
import GooglePlaces
import React

class RNGooglePlacesViewController: UIViewController, GMSAutocompleteViewControllerDelegate {
    var instance: RNGooglePlacesViewController?
    var resolve: RCTPromiseResolveBlock?
    var reject: RCTPromiseRejectBlock?
    
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        self.instance = self
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func openAutocompleteModal(autocompleteFilter: GMSAutocompleteFilter, placeFields selectedFields: GMSPlaceField, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        self.resolve = resolve
        self.reject = reject
        
        let viewController = GMSAutocompleteViewController()
        if #available(iOS 13.0, *), UIScreen.main.traitCollection.userInterfaceStyle == .dark {
            viewController.primaryTextColor = .white
            viewController.secondaryTextColor = .lightGray
            viewController.tableCellSeparatorColor = .lightGray
            viewController.tableCellBackgroundColor = .darkGray
        } else {
            viewController.primaryTextColor = .black
            viewController.secondaryTextColor = .lightGray
            viewController.tableCellSeparatorColor = .lightGray
            viewController.tableCellBackgroundColor = .white
        }
        viewController.autocompleteFilter = autocompleteFilter
        viewController.placeFields = selectedFields
        viewController.delegate = self
        
        if let topController = getTopController() {
            topController.present(viewController, animated: true, completion: nil)
        }
    }
    
    func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
        dismiss(animated: true) {
            self.resolve?(place.toDictionary()) // Assuming the extension to GMSPlace is implemented
        }
    }
    
    func viewController(_ viewController: GMSAutocompleteViewController, didFailAutocompleteWithError error: Error) {
        dismiss(animated: true) {
            self.reject?("E_AUTOCOMPLETE_ERROR", error.localizedDescription, nil)
        }
    }
    
    func wasCancelled(_ viewController: GMSAutocompleteViewController) {
        dismiss(animated: true) {
            self.reject?("E_USER_CANCELED", "Search cancelled", nil)
        }
    }
    
    func didRequestAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = true
    }
    
    func didUpdateAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
        UIApplication.shared.isNetworkActivityIndicatorVisible = false
    }
    
    func getTopController() -> UIViewController? {
        var topController = UIApplication.shared.delegate?.window??.rootViewController
        while let presentedViewController = topController?.presentedViewController {
            topController = presentedViewController
        }
        return topController
    }
}
