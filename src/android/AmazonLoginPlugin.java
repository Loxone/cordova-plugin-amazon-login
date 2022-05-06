/**
 */
package com.education;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.authorization.ScopeFactory;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.api.workflow.RequestContext;


public class AmazonLoginPlugin extends CordovaPlugin {
    private static final String TAG = "AmazonLoginPlugin";

    private static final String CODE_CHALLENGE_METHOD = "plain";

    private static final String ACTION_AUTHORIZE_AVS = "authorizeAVS";

    private static final String OPTION_KEY_PRODUCT_ID = "productID";
    private static final String OPTION_KEY_DEVICE_SERIAL_NUMBER = "deviceSerialNumber";
    private static final String OPTION_KEY_CODE_CHALLENGE = "codeChallenge";

    private static final String KEY_PRODUCT_INSTANCE_ATTRS = "productInstanceAttributes";
        private static final String SCOPE_AVS_PRE_AUTH = "alexa:voice_service:pre_auth profile";
    private static final String SCOPE_ALEXA_ALL = "alexa:all";

    private static final String FIELD_ACCESS_TOKEN = "accessToken";
    private static final String FIELD_AUTHORIZATION_CODE = "authorizationCode";
    private static final String FIELD_USER = "user";
    private static final String FIELD_CLIENT_ID = "clientId";
    private static final String FIELD_REDIRECT_URI = "redirectUri";

    private RequestContext requestContext;
    private CallbackContext savedCallbackContext;


    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        requestContext = RequestContext.create(cordova.getActivity());
        requestContext.registerListener(new AuthorizeListener() {

            @Override
            public void onSuccess(AuthorizeResult result) {
                Log.d(TAG, "Authorization was completed successfully.");
                /* Your app is now authorized for the requested scopes */
                sendAuthorizeResult(result);
            }

            @Override
            public void onError(AuthError ae) {
                Log.d(TAG, "There was an error during the attempt to authorize the application.");
            /* Inform the user of the error */
                savedCallbackContext.error("Trouble during the attempt to authorize the application");
            }

            @Override
            public void onCancel(AuthCancellation cancellation) {
                Log.d(TAG, "Authorization was cancelled before it could be completed. ");
            /* Reset the UI to a ready-to-login state */
                savedCallbackContext.error("Authorization was cancelled before it could be completed.");
            }
        });

        Log.d(TAG, "Initializing AmazonLoginPlugin");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        this.savedCallbackContext = callbackContext;

        if (ACTION_AUTHORIZE_AVS.equals(action)) {
            Log.i(TAG, "AVS Authorization started");

              cordova.getThreadPool().execute(new Runnable() {
                  public void run() {
                    startAvsAuthorization(args.optJSONObject(0), callbackContext);
                  }
              });
        }
        return true;
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        requestContext.onResume();
    }

    private void sendAuthorizeResult(AuthorizeResult result) {

        if (savedCallbackContext == null) {
            return;
        }

        if (result == null) {
            savedCallbackContext.error("Authorize result is null");
            return;
        }

        JSONObject authResult = new JSONObject();

        try {

            authResult.put(FIELD_ACCESS_TOKEN, result.getAccessToken());
            authResult.put(FIELD_AUTHORIZATION_CODE, result.getAuthorizationCode());
            authResult.put(FIELD_CLIENT_ID, result.getClientId());
            authResult.put(FIELD_REDIRECT_URI, result.getRedirectURI());

            User resultUser = result.getUser();

            if (resultUser != null) {
                authResult.put(FIELD_USER, new JSONObject(resultUser.getUserInfo()));
            }

            savedCallbackContext.success(authResult);

        } catch (Exception e) {
            savedCallbackContext.error("Trouble obtaining Authorize Result, error: " + e.getMessage());
        }
    }

    private void sendUserResult(User user) {
        if (savedCallbackContext == null) {
            return;
        }
        try {
            savedCallbackContext.success(new JSONObject(user.getUserInfo()));
        } catch (Exception e) {
            savedCallbackContext.error("Trouble obtaining user, error: " + e.getMessage());
        }
    }

    private void startAvsAuthorization(final JSONObject options, CallbackContext callbackContext) {

          if (callbackContext == null) {
            return;
          }

          if (options == null) {
            callbackContext.error("AVS Authorization options required");
            return;
          }

          final JSONObject scopeData = new JSONObject();
          final JSONObject productInstanceAttributes = new JSONObject();

          try {

              productInstanceAttributes.put(OPTION_KEY_DEVICE_SERIAL_NUMBER, options.getString(OPTION_KEY_DEVICE_SERIAL_NUMBER));
              scopeData.put(KEY_PRODUCT_INSTANCE_ATTRS, productInstanceAttributes);
              scopeData.put(OPTION_KEY_PRODUCT_ID, options.getString(OPTION_KEY_PRODUCT_ID));

              AuthorizationManager.authorize(new AuthorizeRequest.Builder(requestContext)
                  .addScopes(
                        ScopeFactory.scopeNamed(SCOPE_AVS_PRE_AUTH),
                        ScopeFactory.scopeNamed(SCOPE_ALEXA_ALL, scopeData)
                  )
                  .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
                  .withProofKeyParameters(options.getString(OPTION_KEY_CODE_CHALLENGE), CODE_CHALLENGE_METHOD)
                  .build());

          } catch (JSONException e) {
              callbackContext.error(e.toString());

          } catch (Exception e) {
              callbackContext.error(e.toString());
          }
    }
}