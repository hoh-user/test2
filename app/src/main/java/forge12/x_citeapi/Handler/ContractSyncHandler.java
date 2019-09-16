package forge12.x_citeapi.Handler;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Model.SettingsModel;
import forge12.x_citeapi.Views.Dashboard;
import forge12.x_citeapi.Views.FragmentLoading;
import forge12.x_citeapi.Model.ContractImageModel;
import forge12.x_citeapi.Model.ContractModel;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.HttpUtils;
import forge12.x_citeapi.Views.Sync;
import forge12.x_citeapi.interfaces.CallableAction;

public class ContractSyncHandler {
    /**
     * SyncHandler Instance
     */
    private static ContractSyncHandler _sInstance = null;

    /**
     * The amount of failed sync records
     */
    private int _SyncRecordsFailed = 0;
    /**
     * The amount of completed sync records
     */
    private int _SyncRecords = 0;

    /**
     * The count all Sync records
     */
    private int _Size = 0;

    /**
     * Dialog ID Unique
     */
    private int LOADING_DIALOG = 1000;
    private int LOADING_DIALOG_DOWNLOAD = 1001;

    /**
     * Return an Instance of the List Handler
     *
     * @return
     */
    public static ContractSyncHandler getInstance() {
        if (_sInstance == null) {
            _sInstance = new ContractSyncHandler();
        }
        return _sInstance;
    }

    /**
     * Private constructor
     */
    private ContractSyncHandler() {
        EventListener.add_action("sync_after_update_complete", this.onSyncAll(), 10, 1);
        EventListener.add_action("api_validation_complete", this.onSyncAll(), 10, 1);
        EventListener.add_action("download_all_contracts_now", this.downloadContracts(), 10, 1);

        EventListener.add_action("sync_on_sync_record_failed", this.onSyncRecordFailed(), 10, 2);
        EventListener.add_action("sync_on_sync_record_confirmed", this.onSyncRecordConfirmed(), 10, 2);
        EventListener.add_action("sync_on_sync_record_confirmed", this.doSyncImages(), 10, 2);
        EventListener.add_action("contract_sync_all_complete", this.onSyncComplete(), 10, 4);
        EventListener.add_action("sync_after_sync_record", this.onSingleRecordSynced(), 10, 1);
    }

    /**
     * Call Sync images for all syncs that are confirmed
     *
     * @return
     */
    public CallableAction doSyncImages() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                if (!(args[1] instanceof ContractModel)) {
                    return;
                }

                final Context context = (Context) args[0];
                final ContractModel Contract = (ContractModel) args[1];

                ContractImageModel Image = ContractImageModel.load(context, Integer.parseInt(Contract.get("ID")), "image", null);
                ContractImageModel Signature = ContractImageModel.load(context, Integer.parseInt(Contract.get("ID")), "signature", null);

                // Sync Image
                if (Image.isValid()) {
                    Image.set("contract_hash", Contract.get("hash"));
                    Image.sync(context, new CustomEventListener());
                }

                // Sync Signature
                if (Signature.isValid()) {
                    Signature.set("contract_hash", Contract.get("hash"));
                    Signature.sync(context, new CustomEventListener() {
                        @Override
                        public void sendMessage(String message) {
                            // Create PDF
                            HttpUtils.get("mail/contract/live/" + Contract.get("hash"), null, AdminHandler.getApiKey(context), OnMailSend());
                        }
                    });
                } else {
                    HttpUtils.get("mail/contract/live/" + Contract.get("hash"), null, AdminHandler.getApiKey(context), OnMailSend());
                }
            }
        };
    }

    /**
     * Response handler for mails.
     *
     * @return
     */
    public JsonHttpResponseHandler OnMailSend() {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("MAIL SEND", "Response: " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                Log.v("MAIL SEND", "Response: " + errorResponse);
            }
        };
    }

    /**
     * Called whenever the sync has been completed
     *
     * @return
     */
    public CallableAction onSyncComplete() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                ContractSyncHandler.getInstance().resetSyncRecordCounter();
            }
        };
    }

    /**
     * Reset the counters.
     */
    public void resetSyncRecordCounter() {
        this._SyncRecords = 0;
        this._SyncRecordsFailed = 0;
    }

    /**
     * Called whenever a record failed to synchronize
     *
     * @return
     */
    public CallableAction onSyncRecordFailed() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                ContractSyncHandler.getInstance().increaseSyncRecordsFailed();
            }
        };
    }

    /**
     * Increase  the amount of data that has been failed to sync.
     */
    public void increaseSyncRecordsFailed() {
        this._SyncRecordsFailed = this._SyncRecordsFailed + 1;
    }

    /**
     * Get the amount of data that failed to sync.
     *
     * @return
     */
    public int getSyncRecordsFailed() {
        return this._SyncRecordsFailed;
    }

    /**
     * Return the amount of data tried to sync.
     *
     * @return
     */
    public int getSyncSize() {
        return this._Size;
    }

    /**
     * Called whenever a record synchronize
     *
     * @return
     */
    public CallableAction onSyncRecordConfirmed() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                ContractSyncHandler.getInstance().increaseSyncRecords();
            }
        };
    }

    /**
     * Increase the counter for records successfully synced.
     */
    public void increaseSyncRecords() {
        this._SyncRecords = this._SyncRecords + 1;
    }

    /**
     * Get counter for all records that have been sucessfully synced.
     *
     * @return
     */
    public int getSyncRecords() {
        return this._SyncRecords;
    }

    /**
     * Download the signature for the contract
     *
     * @param context
     * @param Contract
     */
    private void downloadSignature(final Context context, final ContractModel Contract) {
        downloadMedia(context, Contract, "signature");
    }

    /**
     * Download the Image for the contract
     *
     * @param context
     * @param Contract
     */
    private void downloadImage(final Context context, final ContractModel Contract) {
        downloadMedia(context, Contract, "image");
    }

    /**
     * download the signature for the given Contract model
     *
     * @param context
     * @param Contract
     */
    private void downloadMedia(final Context context, final ContractModel Contract, final String type) {
        /**
         * Check for Image
         */
        HttpUtils.get("image/" + Contract.get("hash") + "/" + type, null, AdminHandler.getApiKey(context), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response.has("error")) {
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                Log.v("DOWNLOAD MEDIA", "Response: " + errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    JSONObject image = response.getJSONObject(0);

                    // no data available
                    ContractImageModel CIM = ContractImageModel.load(context, Integer.parseInt(Contract.get("ID")), null);
                    CIM.set("contract_id", Contract.get("ID"));
                    CIM.set("image", image.getString("image"));
                    CIM.set("image_type", type);
                    CIM.set("contract_hash", Contract.get("hash"));
                    CIM.save(context, null);
                } catch (JSONException e) {
                    Log.v("JSONException", e.getMessage());
                }
            }
        });
    }

    /**
     * Called to download and upload Contracts within the list of
     * contracts. This function will request all Contracts within the
     * current Makler and download all contracts with status 0.
     * <p>
     * After receiving all contracts they will be compared with the local
     * version.
     * <p>
     * case 1: Local version not available, create a local version
     * case 2: Local version available, server version newer, overwrite local version
     * case 3: Local version available, server version older, overwrite server version
     *
     * @return
     */
    public CallableAction downloadContracts() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof AppCompatActivity)) {
                    return;
                }

                final AppCompatActivity context = (AppCompatActivity) args[0];

                /**
                 * Get Makler ID
                 */
                SettingsModel SM = SettingsModel.load(context, "makler_id", null);
                if (SM == null) {
                    AlertHelper.showMaklerIDError(context);
                    return;
                }

                /**
                 * Create progress bar
                 */
                FragmentLoading.openDialog(context, LOADING_DIALOG_DOWNLOAD);

                // Check if the image already exists
                HttpUtils.get("order/makler/" + SM.get("meta_value") + "/0", null, AdminHandler.getApiKey(context), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // no data available
                        FragmentLoading.closeDialog(LOADING_DIALOG_DOWNLOAD);
                        AlertHelper.showDownloadContractNoDataFound(context);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                        Log.v("DOWNLOAD CONTRACTS", "Response: " + errorResponse);
                        FragmentLoading.closeDialog(LOADING_DIALOG_DOWNLOAD);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        int updated = 0, created = 0, count = response.length(), uploaded = 0;
                        /**
                         * Saves the contracts which are newer on the local version than on the
                         * live server version. These contracts need to be safed on the server.
                         */
                        ArrayList<ContractModel> ListContracts = new ArrayList<ContractModel>();

                        /**
                         * ADd not exsiting local contracts
                         */
                        ArrayList<ContractModel> LocalContracts = ContractModel.load(context);
                        for (int i = 0; i < LocalContracts.size(); i++) {
                            if (LocalContracts.get(i).get("hash").length() == 0) {
                                ListContracts.add(LocalContracts.get(i));
                            }
                        }

                        /**
                         * Add existing live contracts
                         * looping through live contracts
                         */
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                final ContractModel CMServer = new ContractModel(obj);
                                ContractModel CMLocal = ContractModel.load(context, (String) obj.getString("hash"), null);

                                if (CMLocal == null) {
                                    // Save to local version
                                    CMServer.save(context, null, true);

                                    downloadSignature(context, CMServer);
                                    downloadImage(context, CMServer);

                                    created++;
                                } else {
                                    if (CMLocal.isNewerThan(CMServer) == -1) {
                                        CMServer.set("ID", CMLocal.get("ID"));
                                        CMServer.set("sync_ready", CMLocal.get("sync_ready"));
                                        CMServer.save(context, null);

                                        downloadSignature(context, CMServer);
                                        downloadImage(context, CMServer);

                                        updated++;
                                    } else if (CMLocal.isNewerThan(CMServer) == 1) {
                                        ListContracts.add(CMLocal);
                                    }
                                }
                            } catch (JSONException e) {
                                FragmentLoading.closeDialog(LOADING_DIALOG_DOWNLOAD);
                                AlertHelper.showInternError(context);
                                return;
                            }
                        }

                        for (int i = 0; i < ListContracts.size(); i++) {
                            //ListContracts.get(i).save(context, null);
                            final ContractModel CM = ListContracts.get(i);
                            CM.sync(context, new CustomEventListener() {
                                @Override
                                public void sendMessage(String message) {
                                    // Sync images
                                    ContractImageModel Signature = ContractImageModel.load(context, Integer.parseInt(CM.get("ID")), "signature", null);
                                    if (Signature.isValid()) {
                                        Signature.sync(context, null);
                                    }

                                    ContractImageModel Image = ContractImageModel.load(context, Integer.parseInt(CM.get("ID")), "image", null);
                                    if (Image.isValid()) {
                                        Image.sync(context, null);
                                    }
                                }
                            });
                            uploaded++;

                        }
                        FragmentLoading.closeDialog(LOADING_DIALOG_DOWNLOAD);
                        AlertHelper.showDownloadContractComplete(context, count, created, updated, uploaded, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if (context instanceof Dashboard) {
                                    return;
                                }

                                context.finish();
                                context.startActivity(context.getIntent());
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * Called to sync all contracts which are placed in the system
     *
     * @return
     */
    public CallableAction onSyncAll() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                /**
                 * Cancel the contract sync if the updated has been called via the admin/Sync interface
                 */
                if (args[0] instanceof Sync) {
                    return;
                }

                Context context = (Context) args[0];
                sync_all(context);
            }
        };
    }

    /**
     * Sync all marked
     */
    public void sync_all(final Context context) {
        /**
         * Create progress bar
         */
        FragmentLoading.openDialog(context, LOADING_DIALOG);

        /**
         * Load all contracts which are marked as sync ready
         */
        ArrayList<ContractModel> Data = ContractModel.load(context, true);
        _Size = Data.size();

        for (final ContractModel Contract : Data) {
            Contract.sync_and_lock(context, new CustomEventListener() {
                @Override
                public void sendMessage(String message) {
                    EventListener.do_action("sync_on_sync_record_failed", context, Contract);
                    super.sendMessage(message);
                }

                @Override
                public void sendMessage(String message, int option) {
                    switch (option) {
                        case 1:
                            EventListener.do_action("sync_on_sync_record_confirmed", context, Contract);
                            break;
                        default:
                        case 0:
                            sendMessage(message);
                            break;
                    }
                    EventListener.do_action("sync_after_sync_record", context);
                }
            });
        }
        // Ensure that the dialog is hidden even if there are zero contracts to sync
        EventListener.do_action("sync_after_sync_record", context);
    }

    /**
     * Called everytime a single record has been synched.
     *
     * @return
     */
    public CallableAction onSingleRecordSynced() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                Context context = (Context) args[0];
                ContractSyncHandler.getInstance().check_if_synced(context);
            }
        };
    }


    /**
     * Called whenever an sync is finished
     */
    public void check_if_synced(Context pContext) {
        if (_Size == (_SyncRecords + _SyncRecordsFailed)) {
            FragmentLoading.closeDialog(LOADING_DIALOG);

            AlertHelper.showSyncComplete(pContext, getSyncSize(), getSyncRecords(), getSyncRecordsFailed());
        } else {
            EventListener.do_action("contract_sync_all_update", pContext, getSyncSize(), getSyncRecords(), getSyncRecordsFailed());
        }
    }
}
