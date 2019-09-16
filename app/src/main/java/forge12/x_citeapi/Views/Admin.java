package forge12.x_citeapi.Views;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.Callable;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Handler.ToolbarHandler;
import forge12.x_citeapi.Model.ContractModel;
import forge12.x_citeapi.Handler.NavigationHandler;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.interfaces.CallableAction;

/**
 * Handle the Admin View
 */
public class Adminnn extends AppCompatActivity {
    /**
     * Goto Sync View
     */
    private View.OnClickListener mOnClickSync = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EventListener.do_action("admin_on_click_sync", Admin.this, Sync.class);
        }
    };

    /**
     * Goto Sync View
     */
    private View.OnClickListener mOnClickAPI = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PasswordDialog.show(Admin.this, Admin_Api.class, "admin_on_click_api");
            //EventListener.do_action("admin_on_click_api", Admin.this, Admin_Api.class);
        }
    };

    /**
     * On Click listener for Delete all contracts
     */
    private View.OnClickListener mOnClickDeleteAll = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertHelper.showDatabaseRecordDeleteContractsConfirmation(Admin.this);
        }
    };

    /**
     * Delete all entries from the database that are synced
     */
    private CallableAction mOnDeleteContractsAll = new CallableAction() {
        @Override
        public void onCall(Object... argss) {
            if (!(args[0] instanceof AppCompatActivity)) {
                return;
            }

            AppCompatActivity context = (AppCompatActivity) args[0];

            int res = ContractModel.delete_contracts_all(context);
            if (res >= 0) {
                AlertHelper.showDatabaseRecordDeletedContractsConfirmation(context, res);
            } else {
                AlertHelper.showDatabaseRecordDeletedError(context);
            }
        }
    };

    /**
     * On Click listener for Delete contracts that have been synced
     */
    private View.OnClickListener mOnClickDeleteSync = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertHelper.showDatabaseRecordDeleteContractsSynchedConfirmation(Admin.this);
        }
    };

    /**
     * Delete all entries from the database that are synced
     */
    private CallableAction mOnDeleteContractsSynced = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof AppCompatActivity)) {
                return;
            }

            AppCompatActivity context = (AppCompatActivity) args[0];

            int res = ContractModel.delete_contracts_synced(context);
            if (res >= 0) {
                AlertHelper.showDatabaseRecordDeletedContractsConfirmation(context, res);
            } else {
                AlertHelper.showDatabaseRecordDeletedError(context);
            }
        }
    };

    /**
     * Delete all entries from the database that are synced and older than 30 days
     */
    private CallableAction mOnDeleteContracts30 = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof AppCompatActivity)) {
                return;
            }

            AppCompatActivity context = (AppCompatActivity) args[0];

            int res = ContractModel.delete_contracts_older_than_days(context, 30);
            if (res >= 0) {
                AlertHelper.showDatabaseRecordDeletedContractsConfirmation(context, res);
            } else {
                AlertHelper.showDatabaseRecordDeletedError(context);
            }
        }
    };

    /**
     * On Click listener for Delete contracts last 30 days
     */
    private View.OnClickListener mOnClickDelete30 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertHelper.showDatabaseRecordDeleteContracts30DaysConfirmation(Admin.this);
        }
    };

    /**
     * Cleanup
     */
    @Override
    protected void onPause() {

        EventListener.remove_action("delete_contracts_all", mOnDeleteContractsAll);
        EventListener.remove_action("delete_contracts_30", mOnDeleteContracts30);
        EventListener.remove_action("delete_contracts_synced", mOnDeleteContractsSynced);

        super.onPause();
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        /**
         * Add toolbar
         */
        ToolbarHandler.getInstance().setContext(Admin.this);
        ToolbarHandler.getInstance().setSubtitle("Einstellungen");

        /**
         * Add actions
         */
        EventListener.add_action("delete_contracts_all", mOnDeleteContractsAll, 10, 1);
        EventListener.add_action("delete_contracts_30", mOnDeleteContracts30, 10, 1);
        EventListener.add_action("delete_contracts_synced", mOnDeleteContractsSynced, 10, 1);

        /**
         * Add ui
         */
        Button ButtonSync = findViewById(R.id.button_goto_sync);
        ButtonSync.setOnClickListener(mOnClickSync);

        Button ButtonAPI = findViewById(R.id.button_goto_api_settings);
        ButtonAPI.setOnClickListener(mOnClickAPI);

        Button ButtonDelete30 = findViewById(R.id.button_goto_delete_30_days);
        ButtonDelete30.setOnClickListener(mOnClickDelete30);

        Button ButtonDeleteSync = findViewById(R.id.button_goto_delete_synced);
        ButtonDeleteSync.setOnClickListener(mOnClickDeleteSync);

        Button ButtonDeleteAll = findViewById(R.id.button_goto_delete_all);
        ButtonDeleteAll.setOnClickListener(mOnClickDeleteAll);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_admin);
        navigation.setOnNavigationItemSelectedListener(NavigationHandler.getBottomNavigation(Admin.this));

        NavigationHandler.update(Admin.this);
    }

}
