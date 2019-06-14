# Fork Notes (Preface)

This fork aims to simplify Amazon's [AVS Authorization Mechanism](https://developer.amazon.com/docs/alexa-voice-service/authorize-companion-app.html#sdk).

The setup from the original plugin has also been greatly simplified by:

- baking in both Android and iOS LWA libraries
- updating the plugin config to include automatable steps
- adding cordova hooks for copying API Keys at the prepare step

*NOTE:* The android APIs are targeted for cordova-android 7+

# Cordova Plugin Login with Amazon

A Cordova Plugin for Login with Amazon. Use your Amazon account to authenticate with the app.
This plugin is a wrapper around native android and iOS libraries developed by Amazon.
 
## Prerequisites

- Amazon API keys for both iOS and Android 

- Configuration file for Amazon API keys at the root of your 
project named `amazon-login.config.json`, which should have this format:

```
{
  "android": {
    "debug": {
      "api_key": "eyJhbGc...NHGGqug=="
    },
    "release": {
      "api_key": "eyJhbGc...DFthdeg=="
    }
  },
  "ios": {
    "api_key": "eyJhbGc...WyRg=="
  }
}
```

*NOTE:*
By default, the value of API key from `debug` section will be used for Android.
In order to use API key from `release` section

```
TARGET=release cordova prepare
```
 
## Installation

```
cordova plugin add https://github.com/jospete/cordova-plugin-amazon-login.git#release/3.0.2
```

## API

### Authorize

`window.AmazonLogin.authorize(Object options, Function success, Function failure)`

Success function returns an Object like:

	{
		accessToken: "...",
		user: {
		    name: "Full Name",
                email: "email@example.com",
                user_id: "634565435",
                postal_code: '12345'
		}
	}

Failure function returns an error String.

### AuthorizeAVS

`window.AmazonLogin.authorizeAVS(Object options, Function success, Function failure)`

Success function returns an Object like:

	{
		accessToken: "...",
		authorizationCode: "...",
		clientId: "...",
		redirectUri: "...",
		user: {
		    name: "Full Name",
                email: "email@example.com",
                user_id: "634565435",
                postal_code: '12345'
		}
	}

Failure function returns an error String.

### FetchUserProfile

`window.AmazonLogin.authorizeAVS(Function success, Function failure)`

Success function returns an Object like:

	{
        name: "Full Name",
        email: "email@example.com",
        user_id: "634565435",
        postal_code: '12345'
    }

Failure function returns an error String.

### GetToken

`window.AmazonLoginPlugin.getToken(Object options,Function success, Function failure)`

Success function returns an Object like:

	{
		accessToken: "...",
		user: {
		    name: "Full Name",
                email: "email@example.com",
                user_id: "634565435",
                postal_code: '12345'
		}
	}

Failure function returns an error String.


### SignOut

`window.AmazonLogin.signOut(Function success, Function failure)`

## Resources

- [Authorization For AVS](https://developer.amazon.com/docs/alexa-voice-service/authorize-companion-app.html#sdk)
- [Login With Amazon Docs](https://developer.amazon.com/docs/login-with-amazon/minitoc-lwa-overview.html)
- [Amazon Mobile App SDK](https://developer.amazon.com/public/resources/development-tools/sdk)

*NOTE:* API Key registration is defined at step 3 in each of the iOS and Android LWA Docs