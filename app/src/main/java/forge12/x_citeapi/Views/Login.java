package forge12.x_citeapi.Views;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import forge12.x_citeapi.Handler.AdminHandler;
import forge12.x_citeapi.Handler.ApiSettingsHandler;
import forge12.x_citeapi.Handler.ContractListHandler;
import forge12.x_citeapi.Handler.ContractSyncHandler;
import forge12.x_citeapi.Handler.SyncHandler;
import forge12.x_citeapi.Handler.ToolbarHandler;
import forge12.x_citeapi.Model.ContractModel;
import forge12.x_citeapi.Model.SettingsModel;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.Helper;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add the necessary permissions
        ActivityCompat.requestPermissions(Login.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.CAMERA},
                1);

        getSupportActionBar().hide();

        /**
         * Init all dependencies once at startup to ensure its not loaded multiple times
         */
        AlertHelper.getInstance();
        SyncHandler.getInstance();
        ToolbarHandler.getInstance();
        ApiSettingsHandler.getInstance();
        AdminHandler.getInstance();
        ContractSyncHandler.getInstance();
        ContractListHandler.getInstance();// Init everything neccessary
        ContractModel.init();

        SettingsModel.database_exists(Login.this);

        setContentView(R.layout.activity_login);

        TextView LoginFailed = findViewById(R.id.text_login_failed);
        LoginFailed.setVisibility(View.GONE);

        Button Login = findViewById(R.id.button_login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView username = findViewById(R.id.username);
                TextView password = findViewById(R.id.password);

                if (Helper.validate_user(Login.this, username.getText().toString(), password.getText().toString())) {
                    Intent i = new Intent(Login.this, Dashboard.class);
                    startActivity(i);
                } else {
                    TextView LoginFailed = findViewById(R.id.text_login_failed);
                    LoginFailed.setVisibility(View.VISIBLE);
                }
            }
        });

        /* Only for Debug
        Intent i = new Intent(Login.this, ContractPersonal.class);
        startActivity(i);*/
    }

}