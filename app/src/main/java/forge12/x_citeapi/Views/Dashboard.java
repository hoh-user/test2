package forge12.x_citeapi.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Handler.ApiSettingsHandler;
import forge12.x_citeapi.Handler.ToolbarHandler;
import forge12.x_citeapi.Handler.NavigationHandler;
import forge12.x_citeapi.Model.TarifModel;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.interfaces.CallableAction;

public class Dashboard extends AppCompatActivity {

    /**
     * Shows the ZIP Dialog window
     */
    protected void show_zip_dialog(final String type){
        final Dialog ZIP = new Dialog(Dashboard.this);
        ZIP.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ZIP.setContentView(getLayoutInflater().inflate(R.layout.dialog_contract_zip, null));

        /**
         * Set the focus to the input
         */
        EditText zipEditText = ZIP.findViewById(R.id.dialog_contract_zip_input_zip);
        zipEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(zipEditText, InputMethodManager.SHOW_IMPLICIT);


        /**
         * On Click Handler for the close button which will be used to cancel the
         * current process
         */
        ZIP.findViewById(R.id.dialog_contract_zip_btn_close).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ZIP.dismiss();
            }
        });


        /**
         * On Click Handler for the Next button which will be used to continue
         * with the process. Udpdates the ZIP
         */
        ZIP.findViewById(R.id.dialog_contract_zip_btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText field_zip = ZIP.findViewById(R.id.dialog_contract_zip_input_zip);
                String text_zip = field_zip.getText().toString();

                TarifModel TM = TarifModel.load(Dashboard.this, text_zip, type, null);

                if(null == TM){
                    // Print error message
                    ZIP.findViewById(R.id.dialog_contract_zip_error).setVisibility(View.VISIBLE);
                }else{
                    ZIP.findViewById(R.id.dialog_contract_zip_error).setVisibility(View.GONE);

                    ZIP.dismiss();
                    Intent intent = new Intent(Dashboard.this, ContractPersonal.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("type",type);
                    intent.putExtra("zip",text_zip);

                    startActivity(intent);
                }
            }
        });
        ZIP.show();
    }

    /**
     * Handles the clicks on the Buttons
     * @param context
     * @return
     */
    protected View.OnClickListener OnClickListener(final AppCompatActivity context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (v.getId()) {
                    case R.id.imageButtonAddContract_3:
                    case R.id.btn_dashboard_add_contract_3:
                        intent = new Intent(Dashboard.this, ContractPersonal.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("type","Kaution");
                        intent.putExtra("zip","");

                        startActivity(intent);
                        break;
                    case R.id.imageButtonAddContract_2:
                    case R.id.btn_dashboard_add_contract_2:
                        show_zip_dialog("Gas");
                        break;
                    case R.id.btn_dashboard_add_contract:
                    case R.id.imageButtonAddContract:
                        show_zip_dialog("Strom");
                        break;
                    case R.id.btn_dashboard_list_contract:
                    case R.id.imageButtonListContract:
                        intent = new Intent(context, ContractList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        break;
                    case R.id.btn_dashboard_sync:
                    case R.id.imageButtonSync:
                        EventListener.do_action("sync_all_contracts_now", context);
                        break;
                    case R.id.btn_dashboard_search:
                    case R.id.imageButtonSearch:
                        intent = new Intent(context, ProductInformation.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        break;
                    case R.id.btn_dashboard_settings:
                    case R.id.imageButtonSettings:
                        intent = new Intent(context, Admin.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        break;
                }
            }
        };
    };

    public CallableAction mDownloadContracts = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];
            EventListener.do_action("download_all_contracts_now", context);
        }
    };

    @Override
    protected void onPause() {
        EventListener.remove_action("contract_sync_all_complete", mDownloadContracts);
        super.onPause();
    }

    /**
     * @override
     */
    public void onStart() {
        super.onStart();

        /**
         * Add actions
         */
        EventListener.add_action("contract_sync_all_complete", mDownloadContracts, 11, 4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (!Helper.is_database_available(Dashboard.this)) {
            Intent i = new Intent(Dashboard.this, Admin_Api.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
        }

        ApiSettingsHandler.getInstance();

        /**
         * Add toolbar
         */
        ToolbarHandler.getInstance().setContext(Dashboard.this);
        ToolbarHandler.getInstance().setSubtitle("Dashboard");

        /**
         * Add ui
         */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(NavigationHandler.getBottomNavigation(Dashboard.this));

        ImageButton button_add_contract = findViewById(R.id.imageButtonAddContract);
        button_add_contract.setOnClickListener(OnClickListener(Dashboard.this));

        Button btn_add_contract = findViewById(R.id.btn_dashboard_add_contract);
        btn_add_contract.setOnClickListener(OnClickListener(Dashboard.this));

        Button btn_add_contract_2 = findViewById(R.id.btn_dashboard_add_contract_2);
        btn_add_contract_2.setOnClickListener(OnClickListener(Dashboard.this));

        ImageButton button_add_contract_2 = findViewById(R.id.imageButtonAddContract_2);
        button_add_contract_2.setOnClickListener(OnClickListener(Dashboard.this));

        Button btn_add_contract_3 = findViewById(R.id.btn_dashboard_add_contract_3);
        btn_add_contract_3.setOnClickListener(OnClickListener(Dashboard.this));

        ImageButton button_add_contract_3 = findViewById(R.id.imageButtonAddContract_3);
        button_add_contract_3.setOnClickListener(OnClickListener(Dashboard.this));

        ImageButton button_list_contract = findViewById(R.id.imageButtonListContract);
        button_list_contract.setOnClickListener(OnClickListener(Dashboard.this));

        Button btn_list_contract = findViewById(R.id.btn_dashboard_list_contract);
        btn_list_contract.setOnClickListener(OnClickListener(Dashboard.this));

        ImageButton button_sync = findViewById(R.id.imageButtonSync);
        button_sync.setOnClickListener(OnClickListener(Dashboard.this));

        Button btn_sync = findViewById(R.id.btn_dashboard_sync);
        btn_sync.setOnClickListener(OnClickListener(Dashboard.this));

        ImageButton button_search = findViewById(R.id.imageButtonSearch);
        button_search.setOnClickListener(OnClickListener(Dashboard.this));

        Button btn_search = findViewById(R.id.btn_dashboard_search);
        btn_search.setOnClickListener(OnClickListener(Dashboard.this));

        ImageButton button_settings = findViewById(R.id.imageButtonSettings);
        button_settings.setOnClickListener(OnClickListener(Dashboard.this));

        Button btn_settings = findViewById(R.id.btn_dashboard_settings);
        btn_settings.setOnClickListener(OnClickListener(Dashboard.this));

        NavigationHandler.update(Dashboard.this);
    }

}