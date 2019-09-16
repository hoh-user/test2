package forge12.x_citeapi.Views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Handler.ContractHandler;
import forge12.x_citeapi.Handler.ContractListHandler;
import forge12.x_citeapi.Handler.ToolbarHandler;
import forge12.x_citeapi.Model.ContractImageModel;
import forge12.x_citeapi.Model.ContractModel;
import forge12.x_citeapi.Handler.NavigationHandler;
import forge12.x_citeapi.Model.TarifModel;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.interfaces.CallableAction;

public class ContractList extends AppCompatActivity {
    /**
     * Call Back Results
     */
    private final int BUTTON_EDIT_RESPONSE = 200;
    private final int LOADING_SCREEN = 300;
    private final int REQUEST_IMAGE_CAPTURE = 500;

    /**
     * Dialog for the Menu Items
     */
    private Dialog _Menu;

    /**
     * Select the contract type for the duplication
     */
    private Dialog _Menu_Duplicate_Type;

    /**
     * Image Dialog to pick the image for the contract
     */
    private Dialog _ImageDialog;

    /**
     * Adding a custom sync button on the Action bar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.contract_list_menu, menu);
        return true;
    }

    /**
     * On Click handler for the buttons on the action bar in the header Bar of the application.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (!Helper.is_wlan_connected(ContractList.this)) {
                    AlertHelper.showWLANError(ContractList.this);
                } else {
                    AlertHelper.showSyncInformation(ContractList.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            EventListener.do_action("sync_all_contracts_now", ContractList.this);
                        }
                    });
                }
                return true;
            case R.id.action_download:
                if (!Helper.is_wlan_connected(ContractList.this)) {
                    AlertHelper.showWLANError(ContractList.this);
                } else {
                    AlertHelper.showDownloadInformation(ContractList.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            EventListener.do_action("download_all_contracts_now", ContractList.this);
                        }
                    });
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Reload the current page
     */
    private CallableAction reloadView = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof AppCompatActivity)) {
                return;
            }

            final AppCompatActivity context = (AppCompatActivity) args[0];
            finish();
            startActivity(context.getIntent());
        }
    };

    /**
     * Callback for the contract handler if the contract sync has failed
     */
    public CallableAction onSyncFailed = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            FragmentLoading.closeDialog(LOADING_SCREEN);
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];

            if (!(args[1] instanceof String)) {
                AlertHelper.showSyncError(context);
            } else {
                String message = (String) args[1];
                AlertHelper.showSyncError(context, message);
            }
        }
    };

    /**
     * Callback for the contract handler if the contract sync has failed
     */
    public CallableAction onSyncCompleted = new CallableAction() {
        /**
         * Default onCall with strings only
         * @param arg
         * @param args
         * @return {String}
         */
        public String onCall(String arg, String... args) {
            Log.i("Wrong","Wrong");
            return arg;
        }

        /**
         * @param args
         */
        @Override
        public void onCall(Object... args) {
            FragmentLoading.closeDialog(LOADING_SCREEN);
            //AlertHelper.removeLoadingScreen(ContractList.this, LOADING_SCREEN);

            if (!(args[0] instanceof AppCompatActivity)) {
                return;
            }

            if (!(args[2] instanceof String)) {
                return;
            }

            AppCompatActivity context = (AppCompatActivity) args[0];
            String message = (String) args[2];

            AlertHelper.showSyncConfirmation(context, message);
        }
    };

    /**
     * Removing all event listener
     */
    @Override
    protected void onPause() {
        EventListener.remove_action("contract_list_delete_contract", mOnDeleteContract);
        EventListener.remove_action("contract_sync_all_complete", reloadView);
        EventListener.remove_action("contract_handler_sync_failed", onSyncFailed);
        EventListener.remove_action("contract_handler_sync_completed", onSyncCompleted);
        EventListener.remove_filter("text_strip_list_name", onTextStrip);

        super.onPause();
    }

    /**
     * Called whenever the text should be stripped.
     */
    public CallableAction onTextStrip = new CallableAction() {
        @Override
        public String onCall(String arg, String... args) {
            int len = 50;

            if (arg.length() >= len) {
                return arg.substring(0, len) + "...";
            }

            return arg;
        }
    };

    /**
     * @override
     */
    public void onStart() {
        // Actions
        EventListener.add_action("contract_list_delete_contract", mOnDeleteContract, 10, 2);
        EventListener.add_action("contract_sync_all_complete", reloadView, 10, 4);
        EventListener.add_action("contract_handler_sync_failed", onSyncFailed, 10, 2);
        EventListener.add_action("contract_handler_sync_completed", onSyncCompleted, 5, 3);
        EventListener.add_filter("text_strip_list_name", onTextStrip, 20, 1);

        super.onStart();
    }

    /**
     * Creating all event listener and view data
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * load the contract handler if not already loaded
         */
        ContractHandler.getInstance();

        setContentView(R.layout.activity_contract_list);

        /**
         * Add toolbar
         */
        ToolbarHandler.getInstance().setContext(ContractList.this);
        ToolbarHandler.getInstance().setSubtitle("Verträge Übersicht");

        //getSupportActionBar().setIcon(R.drawable.logo);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_contract_list);
        navigation.setOnNavigationItemSelectedListener(NavigationHandler.getBottomNavigation(ContractList.this));

        displayList();
        NavigationHandler.update(ContractList.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            /**
             * Close the edit screen.
             */
            case BUTTON_EDIT_RESPONSE:
                finish();
                startActivity(getIntent());
                break;
            /**
             * Reqest the image capture
             */
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    ContractListHandler.getInstance().saveImage(ContractList.this);
                }
                break;
        }
    }

    /**
     * Close Image Dialog
     *
     * @param view
     */
    public void OnClickCameraDismissImageListener(View view) {
        removeImageDialog();
    }

    /**
     * Close Image Dialog
     */
    private void removeImageDialog() {
        if (_ImageDialog != null) {
            _ImageDialog.dismiss();
            _ImageDialog = null;
        }
    }

    /**
     * Start Camera view
     *
     * @param view
     */
    public void OnClickCameraReplaceImageListener(View view) {
        removeImageDialog();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ContractListHandler.getInstance().createImageFile(ContractList.this);
            } catch (IOException ex) {
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(ContractList.this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Called whenever a contract has been deleted.
     */
    private CallableAction mOnDeleteContract = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof AppCompatActivity)) {
                return;
            }

            if (!(args[1] instanceof Integer)) {
                return;
            }

            AppCompatActivity context = (AppCompatActivity) args[0];
            int id = (int) args[1];

            boolean res = ContractHandler.getInstance().deleteContractById(ContractList.this, id);

            if (res) {
                AlertHelper.showDatabaseRecordDeletedConfirmation(ContractList.this);
            } else {
                AlertHelper.showDatabaseRecordDeletedError(ContractList.this);
            }
        }
    };

    private View.OnClickListener onclickMenuDuplicateType(final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_menu_add_power_image:
                    case R.id.btn_menu_add_power:
                        AlertHelper.showContractDuplicateConfirmation(ContractList.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _Menu_Duplicate_Type.dismiss();
                                ContractModel CM = ContractModel.load(ContractList.this, id, null);
                                if (CM != null) {
                                    CM.set("vertragsart", "Strom");

                                    // Reset informations
                                    CM.set("distributor_name", "");
                                    CM.set("power_consumption", "");
                                    CM.set("electric_meter_number", "");

                                    CM.set("stromlieferung_strasse", "");
                                    CM.set("stromlieferung_hausnummer", "");
                                    CM.set("stromlieferung_ort", "");
                                    CM.set("stromlieferung_plz", "");

                                    if (CM.duplicate(ContractList.this)) {
                                        AlertHelper.showContractDuplicateConfirmed(ContractList.this);
                                    } else {
                                        AlertHelper.showContractDuplicateFailed(ContractList.this);
                                    }
                                }
                            }
                        });
                        break;
                    case R.id.btn_menu_add_gas:
                    case R.id.btn_menu_add_gas_image:
                        AlertHelper.showContractDuplicateConfirmation(ContractList.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _Menu_Duplicate_Type.dismiss();
                                ContractModel CM = ContractModel.load(ContractList.this, id, null);
                                if (CM != null) {

                                    CM.set("vertragsart", "Gas");

                                    // Reset informations
                                    CM.set("distributor_name", "");
                                    CM.set("power_consumption", "");
                                    CM.set("electric_meter_number", "");

                                    CM.set("stromlieferung_strasse", "");
                                    CM.set("stromlieferung_hausnummer", "");
                                    CM.set("stromlieferung_ort", "");
                                    CM.set("stromlieferung_plz", "");

                                    /**
                                     * Duplicate the Contract
                                     */
                                    if (CM.duplicate(ContractList.this)) {
                                        AlertHelper.showContractDuplicateConfirmed(ContractList.this);
                                    } else {
                                        AlertHelper.showContractDuplicateFailed(ContractList.this);
                                    }
                                }
                            }
                        });
                        break;
                    case R.id.btn_menu_add_kaution:
                    case R.id.btn_menu_add_kaution_image:
                        AlertHelper.showContractDuplicateConfirmation(ContractList.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _Menu_Duplicate_Type.dismiss();
                                ContractModel CM = ContractModel.load(ContractList.this, id, null);
                                if (CM != null) {
                                    CM.set("vertragsart", "Kaution");

                                    /**
                                     * Duplicate the Contract
                                     */
                                    if (CM.duplicate(ContractList.this)) {
                                        AlertHelper.showContractDuplicateConfirmed(ContractList.this);
                                    } else {
                                        AlertHelper.showContractDuplicateFailed(ContractList.this);
                                    }
                                }
                            }
                        });
                        break;
                    case R.id.btn_menu_close:
                        _Menu_Duplicate_Type.dismiss();
                        break;
                }
            }
        };
    }

    /**
     * Callback Function for the onclick on the table row menu item
     */
    private View.OnClickListener onclickTableRowMenuItem(final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    /**
                     * TODO: REMOVE AFTER DEBUG
                     */
                    case R.id.btn_menu_unlock:
                        ContractModel CM = ContractModel.load(ContractList.this, id, null);
                        CM.set("hash", "");
                        CM.setStatusUnsynced(ContractList.this);
                        _Menu.dismiss();
                        break;
                    case R.id.btn_menu_duplicate:
                    case R.id.btn_menu_icon_duplicate:
                        /**
                         * Show duplicate window
                         */
                        _Menu_Duplicate_Type = new Dialog(ContractList.this);
                        _Menu_Duplicate_Type.setContentView(R.layout.dialog_contract_list_selector);

                        _Menu_Duplicate_Type.findViewById(R.id.btn_menu_add_power).setOnClickListener(onclickMenuDuplicateType(id));
                        _Menu_Duplicate_Type.findViewById(R.id.btn_menu_add_power_image).setOnClickListener(onclickMenuDuplicateType(id));
                        _Menu_Duplicate_Type.findViewById(R.id.btn_menu_add_gas).setOnClickListener(onclickMenuDuplicateType(id));
                        _Menu_Duplicate_Type.findViewById(R.id.btn_menu_add_gas_image).setOnClickListener(onclickMenuDuplicateType(id));
                        _Menu_Duplicate_Type.findViewById(R.id.btn_menu_add_kaution).setOnClickListener(onclickMenuDuplicateType(id));
                        _Menu_Duplicate_Type.findViewById(R.id.btn_menu_add_kaution_image).setOnClickListener(onclickMenuDuplicateType(id));
                        _Menu_Duplicate_Type.findViewById(R.id.btn_menu_close).setOnClickListener(onclickMenuDuplicateType(id));


                        _Menu_Duplicate_Type.show();

                        _Menu.dismiss();
                        break;
                    case R.id.btn_menu_icon_sync:
                    case R.id.btn_menu_sync:
                        /**
                         * Do a WLAN check before sync.
                         */
                        if (!Helper.is_wlan_connected(ContractList.this)) {
                            AlertHelper.showWLANError(ContractList.this);
                        } else {
                            AlertHelper.showSyncSingleContractConfirmation(ContractList.this, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    _Menu.dismiss();
                                    FragmentLoading.openDialog(ContractList.this, LOADING_SCREEN);
                                    ContractHandler.getInstance().syncContractById(ContractList.this, id);
                                }
                            });
                        }
                        break;
                    case R.id.btn_menu_icon_add_image:
                    case R.id.btn_menu_add_image:
                        _Menu.dismiss();
                        if (!ContractListHandler.getInstance().imageExist(ContractList.this, id)) {
                            OnClickCameraReplaceImageListener(findViewById(R.id.container_list));
                        } else {
                            _ImageDialog = new Dialog(ContractList.this);
                            _ImageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            _ImageDialog.setContentView(getLayoutInflater().inflate(R.layout.activity_image_dialog, null));
                            _ImageDialog.show();
                            ImageView image = _ImageDialog.findViewById(R.id.image_power_counter);
                            image.setImageBitmap(Helper.convertBase64ToBitmap(ContractListHandler.getInstance().getImageByID(ContractList.this, id)));
                        }
                        break;
                    case R.id.btn_menu_icon_delete:
                    case R.id.btn_menu_delete:
                        _Menu.dismiss();
                        AlertHelper.showDatabaseRecordDeleteConfirmation(ContractList.this, id);
                        break;
                    case R.id.btn_menu_icon_edit:
                    case R.id.btn_menu_edit:
                        Intent intent = new Intent(ContractList.this, ContractPersonalEdit.class);
                        intent.putExtra("ID", Integer.toString(id));
                        startActivityForResult(intent, BUTTON_EDIT_RESPONSE);
                        break;
                    case R.id.btn_menu_close:
                        _Menu.dismiss();
                        break;
                }
            }
        };
    }

    /**
     * Default function for the onclick on the table row
     *
     * @param id
     * @return
     */
    private View.OnClickListener onClickTableRow(final int id) {
        return onClickTableRow(id, false);
    }

    /**
     * Callback Function for the onclick on the table row
     */
    private View.OnClickListener onClickTableRow(final int id, final boolean locked) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _Menu = new Dialog(ContractList.this);

                if (locked == false) {
                    _Menu.setContentView(R.layout.dialog_contract_list);

                    _Menu.findViewById(R.id.btn_menu_sync).setOnClickListener(onclickTableRowMenuItem(id));
                    _Menu.findViewById(R.id.btn_menu_icon_sync).setOnClickListener(onclickTableRowMenuItem(id));
                } else {
                    _Menu.setContentView(R.layout.dialog_contract_list_locked);

                    ContractImageModel CIM = ContractImageModel.load(ContractList.this, id, "image", null);
                    if (CIM == null || !CIM.isValid()) {
                        _Menu.findViewById(R.id.image_container).setVisibility(View.GONE);
                    }
                }

                ContractModel CM = ContractModel.load(ContractList.this, id, null);
                String Vertragsart = CM.get("vertragsart");

                if(Vertragsart.equals("Kaution")){
                    _Menu.findViewById(R.id.image_container).setVisibility(View.GONE);
                }

                _Menu.findViewById(R.id.btn_menu_close).setOnClickListener(onclickTableRowMenuItem(id));
                _Menu.findViewById(R.id.btn_menu_edit).setOnClickListener(onclickTableRowMenuItem(id));
                _Menu.findViewById(R.id.btn_menu_icon_edit).setOnClickListener(onclickTableRowMenuItem(id));
                _Menu.findViewById(R.id.btn_menu_add_image).setOnClickListener(onclickTableRowMenuItem(id));
                _Menu.findViewById(R.id.btn_menu_icon_add_image).setOnClickListener(onclickTableRowMenuItem(id));
                _Menu.findViewById(R.id.btn_menu_delete).setOnClickListener(onclickTableRowMenuItem(id));
                _Menu.findViewById(R.id.btn_menu_icon_delete).setOnClickListener(onclickTableRowMenuItem(id));
                _Menu.findViewById(R.id.btn_menu_duplicate).setOnClickListener(onclickTableRowMenuItem(id));
                _Menu.findViewById(R.id.btn_menu_icon_duplicate).setOnClickListener(onclickTableRowMenuItem(id));

                /**
                 * TODO: REMOVE AFTER DEBUGGING
                 */
                _Menu.findViewById(R.id.btn_menu_unlock).setOnClickListener(onclickTableRowMenuItem(id));

                _Menu.show();
            }
        };
    }


    /**
     * Displaying the List of all contract models not yet synched with the live server.
     */
    private void displayList() {
        ArrayList<ContractModel> Dataset = ContractModel.loadWithTarif(ContractList.this);

        // The layout to add the fields to
        TableLayout table = (TableLayout) findViewById(R.id.wrapper);
        table.setPadding(table.getPaddingLeft(), table.getPaddingTop(), table.getPaddingRight(), 56);
        table.setColumnStretchable(1, true);

        for (int i = 0; i < Dataset.size(); i++) {

            View tr = getLayoutInflater().inflate(R.layout.tablerow_contract_list, null, false);

            /**
             * Add custom values
             */
            TextView txt_id = tr.findViewById(R.id.textView_ID_Value);
            String id = Dataset.get(i).get("ID");

            if (null != id) {
                txt_id.setText(id);
            }

            TextView txt_name = tr.findViewById(R.id.textView_Name_Value);
            String firstname = Dataset.get(i).get("firstname");
            String lastname = Dataset.get(i).get("lastname");

            if (null != firstname && null != lastname) {
                txt_name.setText(EventListener.apply_filters("text_strip_list_name", lastname + ", " + firstname));
            }

            TextView txt_date = tr.findViewById(R.id.textView_date_value);
            String timestamp = Dataset.get(i).get("timestamp");

            if (null != timestamp) {
                String date = Helper.convertDate(timestamp, "dd.MM.yyyy");

                if (null != date) {
                    txt_date.setText(date);
                }
            }

            /**
             * Update the view for the type
             */
            TextView txt_type = tr.findViewById(R.id.textView_type_value);
            String type = Dataset.get(i).get("vertragsart");

            if (!(type instanceof String)) {
                type = "--";
            }

            if (type.equals("Gas")) {
                type = "Gas";
            } else if (type.equals("Strom")) {
                type = "Strom";
            } else {
                type = "Kaution";
            }

            txt_type.setText(type);

            tr.setId(View.generateViewId());

            /**
             * Differentiate between synced and unsynced contracts
             */
            if (Dataset.get(i).get("status") != null && Dataset.get(i).get("status").length() != 0 && !Dataset.get(i).get("status").equals("0")) {

                /**
                 * Update Image Status
                 */
                ImageView Image_Status = tr.findViewById(R.id.imageView_Status);
                Image_Status.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_close_grey_24dp, getTheme()));

                tr.setOnClickListener(onClickTableRow(Integer.parseInt(Dataset.get(i).get("ID")), true));
            } else {
                tr.setOnClickListener(onClickTableRow(Integer.parseInt(Dataset.get(i).get("ID")), false));
            }

            /**
             * Add tr to the table
             */
            table.addView(tr);
        }
    }

}
