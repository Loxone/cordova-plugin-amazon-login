#import <Cordova/CDVPlugin.h>

@interface AmazonLoginPlugin : CDVPlugin {
}

- (void)authorizeAVS:(CDVInvokedUrlCommand *)command;

@end