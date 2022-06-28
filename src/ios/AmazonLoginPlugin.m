#import "AmazonLoginPlugin.h"
#import "AppDelegate.h"

#import <Cordova/CDVAvailability.h>
#import <LoginWithAmazon/LoginWithAmazon.h>

@implementation AppDelegate (AmazonLogin)

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)
            url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    

    if ([url.absoluteString rangeOfString:@"amzn-"].location == NSNotFound) {
        return [super application:application openURL:url options:options];
    } else {
        return [AMZNAuthorizationManager handleOpenURL:url
                                sourceApplication:options[UIApplicationOpenURLOptionsSourceApplicationKey]];
    }
}

@end

@implementation AmazonLoginPlugin

- (void)authorizeAVS:(CDVInvokedUrlCommand *)command {

        NSDictionary* options = [[NSDictionary alloc]init];

        if ([command.arguments count] > 0) {
            options = [command argumentAtIndex:0];
        }

        NSDictionary *scopeData = @{@"productID": [options objectForKey:@"productID"],
                              @"productInstanceAttributes": @{@"deviceSerialNumber": [options objectForKey:@"deviceSerialNumber"]}};

          id alexaAllScope = [AMZNScopeFactory scopeWithName:@"alexa:all" data:scopeData];
          id alexaSplashScope = [AMZNScopeFactory scopeWithName:@"alexa:voice_service:pre_auth profile"];

          AMZNAuthorizeRequest *request = [[AMZNAuthorizeRequest alloc] init];
          request.scopes = @[alexaSplashScope, alexaAllScope];
          request.codeChallenge = [options objectForKey:@"codeChallenge"];
          request.codeChallengeMethod = @"plain";
          request.grantType = AMZNAuthorizationGrantTypeCode;

        // Make an Authorize call to the Login with Amazon SDK.
        [[AMZNAuthorizationManager sharedManager] authorize:request
                                                withHandler:^(AMZNAuthorizeResult *result, BOOL
                                                              userDidCancel, NSError *error) {

                                                    if (error) {
                                                        // Handle errors from the SDK or authorization server.
                                                        if(error.code == kAMZNLWAApplicationNotAuthorized) {
                                                            // Show authorize user button.
                                                            // NSLog(@"AmazonLoginPlugin authorize request NotAuthorized");

                                                            NSString* payload =@"authorize request NotAuthorized";

                                                            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:payload];

                                                            // The sendPluginResult method is thread-safe.
                                                            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

                                                        } else {
                                                            // NSLog(@"AmazonLoginPlugin authorize request failed");
                                                            NSString* payload = error.userInfo[@"AMZNLWAErrorNonLocalizedDescription"];

                                                            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:payload];

                                                            // The sendPluginResult method is thread-safe.
                                                            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                                                        }

                                                    } else if (userDidCancel) {
                                                        // Handle errors caused when user cancels login.
                                                        // NSLog(@"AmazonLoginPlugin authorize request canceled");
                                                       NSString* payload = @"authorize request canceled";

                                                        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:payload];

                                                        // The sendPluginResult method is thread-safe.
                                                        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

                                                    } else {

                                                        // NSLog(@"AmazonLoginPlugin authorize success");
                                                        // Authentication was successful.

                                                        NSDictionary *dictionary = @{
                                                                                     @"authorizationCode": result.authorizationCode,
                                                                                     @"clientId": result.clientId,
                                                                                     @"redirectUri": result.redirectUri
                                                                                     };


                                                        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dictionary];

                                                        // The sendPluginResult method is thread-safe.
                                                        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                                                    }
                                                }];
}

- (void)fetchUserProfile:(CDVInvokedUrlCommand *)command {
    //NSLog(@"AmazonLoginPlugin fetchUserProfile");

    [AMZNUser fetch:^(AMZNUser *user, NSError *error) {
        if (error) {
            // Error from the SDK, or no user has authorized to the app.
            NSString* payload = error.userInfo[@"AMZNLWAErrorNonLocalizedDescription"];

            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:payload];

            // The sendPluginResult method is thread-safe.
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

        } else if (user) {
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:user.profileData];

            // The sendPluginResult method is thread-safe.
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

        }
    }];
}
@end
