package forge12.x_citeapi.Views;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Handler.NavigationHandler;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.Helper;

public class Sync extends AppCompatActivity {

    public TextView mStatusMessage;

    /**
     * Custom Event Listener
     *
     * @param message
     */
    public void sendMessage(String message) {
        sendMessage(message, CustomEventListener.APPEND);
    }

    /**
     * Update the status message
     *
     * @param message
     * @param option
     */
    public void sendMessage(String message, int option) {
        switch (option) {
            case CustomEventListener.REPLACE:
                mStatusMessage.setText(message);
                break;
            case CustomEventListener.APPEND:
            default:
                mStatusMessage.append("\n" + message);
                break;
        }
    }

    /**
     * Alert Popup to ensure that the user knows what he is doing
     */
    private View.OnClickListener OnClickUpdateAlert() {
        final AppCompatActivity context = this;

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.is_wlan_connected(context)) {
                    AlertHelper.showWLANError(context);
                } else {
                    EventListener.do_action("sync_do_update", context);
                }
            }
        };
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_admin);
        navigation.setOnNavigationItemSelectedListener(NavigationHandler.getBottomNavigation(this));

        // Button Sync Plz
        Button ButtonUpdate = findViewById(R.id.button_update_db);
        ButtonUpdate.setOnClickListener(OnClickUpdateAlert());

        NavigationHandler.update(Sync.this);
        mStatusMessage = findViewById(R.id.statusMessage);

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
                    Toast.makeText(Sync.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
