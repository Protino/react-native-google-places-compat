import { AppRegistry, Platform } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';
//fixme: incorrect import
import RNGooglePlacesCompat from '../lib/module';
import Config from './config.json';

RNGooglePlacesCompat.initializePlaceClient(
  Platform.OS === 'ios'
    ? Config.IOS_PLACES_API_KEY
    : Config.ANDROID_PLACES_API_KEY,
  false
);

AppRegistry.registerComponent(appName, () => App);
