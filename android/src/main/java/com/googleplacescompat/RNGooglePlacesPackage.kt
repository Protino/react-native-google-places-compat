package com.googleplacescompat

import com.facebook.react.ReactPackage
import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager

class RNGooglePlacesPackage : TurboReactPackage() {
    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
        return if (name == RNGooglePlacesCompatSpec.NAME) {
            RNGooglePlacesModule(reactContext)
        } else {
            null
        }
    }

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
        return ReactModuleInfoProvider {
            val moduleInfos: MutableMap<String, ReactModuleInfo> = HashMap()
            val isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
            moduleInfos[RNGooglePlacesCompatSpec.NAME] = ReactModuleInfo(
                RNGooglePlacesCompatSpec.NAME,
                RNGooglePlacesCompatSpec.NAME,
                false,  // canOverrideExistingModule
                false,  // needsEagerInit
                true,  // hasConstants
                false, // isCxxModule
                isTurboModule // isTurboModule
            )
            moduleInfos
        }
    }

    // Below methods are for backward compatibility with the old architecture.
    // They are called only if the New Architecture is not enabled.
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
      // For backward compatibility, when NewArch is disabled.
      // Otherwise, modules are provided via getModule().
      // Check if newArchEnabled is false if possible, or rely on RN to call this appropriately.
      // For now, providing the module here for old arch.
      // In a pure New Arch setup, this might return emptyList() if getModule handles all cases.
      return listOf(RNGooglePlacesModule(reactContext))
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}
