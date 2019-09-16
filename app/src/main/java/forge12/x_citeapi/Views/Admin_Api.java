package forge12.x_citeapi.Views;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Handler.AdminHandler;
import forge12.x_citeapi.Handler.NavigationHandler;
import forge12.x_citeapi.Handler.ToolbarHandler;
import forge12.x_citeapi.Model.SettingsModel;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.Utils.HttpUtils;

/**
 * Handle the Admin View
 */
public class Admin_Api extends AppCompatActivity {

    /**
     * On Click listener for Delete all contracts
     */
    private View.OnClickListener mOnClickMaklerSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!Helper.is_wlan_connected(Admin_Api.this)){
                AlertHelper.showWLANError(Admin_Api.this);
                return;
            }

            EditText editEmail = findViewById(R.id.makler_name);
            EditText editPassword = findViewById(R.id.makler_password);

            final String email = editEmail.getText().toString();
            final String password = Helper.convertPassMd5(editPassword.getText().toString());

            // request makler information

            HttpUtils.get("makler/" + email + "/" + password, null, AdminHandler.getApiKey(Admin_Api.this), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    String makler_id = "0";
                    try {
                        makler_id = response.getString("id");
                    } catch (JSONException e) {
                        AlertHelper.showDataResponseError(Admin_Api.this);
                        return; // just stop
                    }

                    if (makler_id.length() == 0 || makler_id.equals("0")) {
                        AlertHelper.showMaklerResponseError(Admin_Api.this);
                        //AlertHelper.showDataResponseError(Admin_Api.this);
                        return;
                    }

                    EventListener.do_action("admin_on_makler_request_received", Admin_Api.this, makler_id, email, password);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                    Log.v("MAKLER SAVE", "Response: "+errorResponse);
                }
            });
        }
    };


    /**
     * Alert Popup to ensure that the user knows what he is doing
     */
    private View.OnClickListener OnClickSyncAlert() {
        final AppCompatActivity context = this;

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Helper.is_wlan_connected(Admin_Api.this)){
                    AlertHelper.showWLANError(Admin_Api.this);
                    return;
                }

                AlertHelper.showDatabaseResetConfirmation(context);
            }
        };
    }

    /**
     * Cleanup
     */
    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_api);

        /**
         * Add toolbar
         */
        ToolbarHandler.getInstance().setContext(Admin_Api.this);
        ToolbarHandler.getInstance().setSubtitle("Einstellungen");

        /**
         * Add ui
         */
        Button ButtonReset = findViewById(R.id.button_reset);
        ButtonReset.setOnClickListener(OnClickSyncAlert());


        ImageButton MaklerSave = findViewById(R.id.button_makler_save);
        MaklerSave.setOnClickListener(mOnClickMaklerSave);

        SettingsModel SettingsMaklerID = SettingsModel.load(Admin_Api.this, "makler_id", null);
        SettingsModel SettingsMaklerEmail = SettingsModel.load(Admin_Api.this, "makler_email", null);

        if (SettingsMaklerID != null && SettingsMaklerEmail != null) {
            EditText editEmail = findViewById(R.id.makler_name);
            editEmail.setText(SettingsMaklerEmail.get("meta_value"));
        }


        ImageButton Save = findViewById(R.id.button_admin_save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText api_key = findViewById(R.id.api_key);
                EventListener.do_action("admin_on_save_api_key", Admin_Api.this, api_key.getText().toString());
            }
        });

        SettingsModel ApiKey = SettingsModel.load(Admin_Api.this, "api_key", null);

        if (ApiKey != null) {
            EditText api_key = findViewById(R.id.api_key);
            api_key.setText(ApiKey.get("meta_value"));
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_admin);
        navigation.setOnNavigationItemSelectedListener(NavigationHandler.getBottomNavigation(Admin_Api.this));

        NavigationHandler.update(Admin_Api.this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Admin_Api.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}