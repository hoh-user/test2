package forge12.x_citeapi.Handler;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Model.ApiSettingsModel;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.Utils.HttpUtils;
import forge12.x_citeapi.interfaces.CallableAction;

public class ApiSettingsHandler {
    /**
     * Instance of the ContractHandler
     */
    private static ApiSettingsHandler _sInstance = null;

    /**
     * Return an Instance of the Handler
     *
     * @return ApiSettingsHandler
     */
    public static ApiSettingsHandler getInstance() {
        if (_sInstance == null) {
            _sInstance = new ApiSettingsHandler();
        }
        return _sInstance;
    }

    /**
     * The constructor
     */
    public ApiSettingsHandler() {
        EventListener.add_action("sync_all_contracts_now", this.validate_api_version(), 11, 1);
    }

    /**
     * Returns the Version of the API
     *
     * @param context
     * @return
     */
    public static String getVersion(Context context) {
        ApiSettingsModel Setting = ApiSettingsModel.load(context, "version", null);
        return Setting.get("meta_value");
    }

    public CallableAction validate_api_version() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                final Context context = (Context) args[0];

                // Cancel settings ceck if wlan is not connected
                if (!Helper.is_wlan_connected(context)) {
                    AlertHelper.showWLANError(context);
                    return;
                }

                HttpUtils.get("api_setting", null, AdminHandler.getApiKey(context), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String RemoteVersion = "";
                        String LocalVersion = ApiSettingsHandler.getInstance().getVersion(context);
                        try {
                            RemoteVersion = response.getString("meta_value");
                        } catch (JSONException e) {
                            AlertHelper.showDataResponseError(context);
                            return; // just stop
                        }

                        if (RemoteVersion.length() == 0 || RemoteVersion.equals("")) {
                            AlertHelper.showDataResponseError(context);
                            return;
                        }

                        if (!RemoteVersion.equals(LocalVersion)) {
                            AlertHelper.showUpdateNotification(context);
                        } else {
                            EventListener.do_action("api_validation_complete", context);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                        Log.v("API_SETTING", "Response: "+errorResponse);
                    }
                });
            }
        };
    }
}
