package forge12.x_citeapi.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.R;

/**
 * Handling all error Messages
 */
public class AlertHelper {
    private static AlertHelper _sInstance = null;

    public static AlertHelper getInstance() {
        if (_sInstance == null) {
            _sInstance = new AlertHelper();
        }
        return _sInstance;
    }

    /**
     * @param pContext
     * @param pTitle
     * @param pMessage
     * @param pButtonLabelPositive
     * @param pPositiveCallback
     * @param pButtonLabelNegative
     * @param pNegativeCallback
     */
    public static void show(Context pContext, String pTitle, String pMessage,
                            String pButtonLabelPositive, @Nullable DialogInterface.OnClickListener pPositiveCallback,
                            @Nullable String pButtonLabelNegative, @Nullable DialogInterface.OnClickListener pNegativeCallback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(pContext);
        alert.setTitle(pTitle);
        alert.setMessage(pMessage);

        if (pPositiveCallback == null) {
            alert.setPositiveButton(pButtonLabelPositive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
        } else {
            alert.setPositiveButton(pButtonLabelPositive, pPositiveCallback);
        }

        if (pNegativeCallback == null) {
            if (pButtonLabelNegative != null) {
                alert.setNegativeButton(pButtonLabelNegative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
            }
        } else {
            alert.setNegativeButton(pButtonLabelNegative, pNegativeCallback);
        }

        alert.show();
    }

    /**
     * Display the positive Button only without callback
     *
     * @param pContext
     * @param pTitle
     * @param pMessage
     * @param pButtonLabelPositive
     */
    public static void show(Context pContext, String pTitle, String pMessage, String pButtonLabelPositive) {
        AlertHelper.show(pContext, pTitle, pMessage, pButtonLabelPositive, null, null, null);
    }

    /**
     * Display the positive Button only with callback
     *
     * @param pContext
     * @param pTitle
     * @param pMessage
     * @param pButtonLabelPositive
     * @param pPositiveCallback
     */
    public static void show(Context pContext, String pTitle, String pMessage, String pButtonLabelPositive, DialogInterface.OnClickListener pPositiveCallback) {
        AlertHelper.show(pContext, pTitle, pMessage, pButtonLabelPositive, pPositiveCallback, null, null);
    }

    /**
     * Display the negative Button only without callback
     *
     * @param pContext
     * @param pTitle
     * @param pMessage
     * @param pButtonLabelPositive
     * @param pPositiveCallback
     */
    public static void show(Context pContext, String pTitle, String pMessage, String pButtonLabelPositive, DialogInterface.OnClickListener pPositiveCallback, String pButtonLabelNegative) {
        AlertHelper.show(pContext, pTitle, pMessage, pButtonLabelPositive, pPositiveCallback, pButtonLabelNegative, null);
    }

    /**
     * Datenbank updates
     *
     * @param pContext
     */
    public static void showDatabaseUpdated(Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_database_update_confirmed),
                pContext.getResources().getString(R.string.dialog_database_update_confirmed_text),
                pContext.getResources().getString(R.string.dialog_next)
        );
    }

    public static void showDatabaseReset(Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_database_reset_confirmed),
                pContext.getResources().getString(R.string.dialog_database_reset_confirmed_text),
                pContext.getResources().getString(R.string.dialog_next)
        );
    }

    /**
     * Show Message data saved
     *
     * @param pContext
     */
    public static void showDataSaved(Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_data_saved),
                pContext.getResources().getString(R.string.dialog_data_saved_text),
                pContext.getResources().getString(R.string.dialog_next)
        );
    }

    public static void showMaklerResponseError(Context pContext) {
        showDataError(pContext,
                pContext.getResources().getString(R.string.dialog_error_access_data)
        );
    }

    public static void showDataResponseError(Context pContext) {
        showDataError(pContext,
                pContext.getResources().getString(R.string.dialog_error_response)
        );
    }

    public static void showDataError(Context pContext) {
        showDataError(pContext,
                pContext.getResources().getString(R.string.dialog_error_save));
    }

    public static void showDataError(Context pContext, String pMessage) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_error),
                pMessage,
                pContext.getResources().getString(R.string.dialog_next)
        );
    }

    /**
     * Show the API Missing Confirmation box.
     *
     * @param pContext
     */
    public static void showAPIKeyMissing(Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_error),
                pContext.getResources().getString(R.string.dialog_error_api_key),
                pContext.getResources().getString(R.string.dialog_next)
        );
    }

    /**
     * Update notification - will be used to inform the user that it is required to do a database update before continue
     *
     * @param pContext
     */
    public static void showUpdateNotification(final Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_database_update),
                pContext.getResources().getString(R.string.dialog_database_update_text),
                pContext.getResources().getString(R.string.dialog_next), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                        EventListener.do_action("sync_do_update", pContext);
                    }
                },
                pContext.getResources().getString(R.string.dialog_cancel));
    }


    /**
     * Show the WLAN Confirmation Message Box
     *
     * @param pContext
     */
    public static void showWLANConfirmation(final Context pContext) {
        showWLANConfirmation(pContext, "sync_do_update");
    }

    public static void showWLANConfirmation(final Context pContext, final String Action) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_wlan_confirmation),
                pContext.getResources().getString(R.string.dialog_wlan_confirmation_text),
                pContext.getResources().getString(R.string.dialog_next), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        EventListener.do_action(Action, pContext);
                    }
                },
                pContext.getResources().getString(R.string.dialog_cancel));
    }

    public static void showDatabaseResetConfirmation(final Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_database_reset),
                pContext.getResources().getString(R.string.dialog_database_reset_text),
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        EventListener.do_action("sync_do_reset", pContext);
                    }
                },
                pContext.getResources().getString(R.string.dialog_cancel));
    }

    public static void showDatabaseRecordDeleteConfirmation(final Context pContext, final int id) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_delete_contract_confirmation),
                pContext.getResources().getString(R.string.dialog_delete_contract_confirmation_text),
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        EventListener.do_action("contract_list_delete_contract", pContext, id);
                    }
                },
                pContext.getResources().getString(R.string.dialog_cancel)
        );
    }

    public static void showDatabaseRecordDeletedError(Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_error),
                pContext.getResources().getString(R.string.dialog_error_delete_text),
                pContext.getResources().getString(R.string.dialog_next));
    }

    public static void showDatabaseRecordDeletedConfirmation(final AppCompatActivity pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_delete_single_contract),
                pContext.getResources().getString(R.string.dialog_delete_single_contract_text),
                pContext.getResources().getString(R.string.dialog_next), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = pContext.getIntent();
                        pContext.finish();
                        pContext.startActivity(i);
                    }
                });
    }

    public static void showDatabaseRecordDeleteContractsConfirmation(final Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_delete_contract),
                pContext.getResources().getString(R.string.dialog_delete_contract_all),
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        EventListener.do_action("delete_contracts_all", pContext);
                    }
                },
                pContext.getResources().getString(R.string.dialog_cancel)
        );
    }

    public static void showDatabaseRecordDeletedContractsConfirmation(final Context pContext, final int Counter) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_delete_contract_synced),
                "Es wurden " + Counter + " Verträge aus der Datenbank gelöscht.",
                pContext.getResources().getString(R.string.dialog_delete_contract_confirmed)
        );
    }

    public static void showDatabaseRecordDeleteContractsSynchedConfirmation(final Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_delete_contract),
                pContext.getResources().getString(R.string.dialog_delete_contract_synced),
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        EventListener.do_action("delete_contracts_synced", pContext);
                    }
                },
                pContext.getResources().getString(R.string.dialog_cancel)

        );
    }

    public static void showDatabaseRecordDeleteContracts30DaysConfirmation(final Context pContext) {
        AlertHelper.show(pContext,

                pContext.getResources().getString(R.string.dialog_delete_contract),
                pContext.getResources().getString(R.string.dialog_delete_contract_30),
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        EventListener.do_action("delete_contracts_30", pContext);
                    }
                },
                pContext.getResources().getString(R.string.dialog_cancel)

        );
    }

    public static void showWLANError(Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_wlan_error),
                pContext.getResources().getString(R.string.dialog_wlan_error_text),
                pContext.getResources().getString(R.string.dialog_next));
    }

    public static void showSyncError(Context pContext) {
        showSyncError(pContext,
                pContext.getResources().getString(R.string.dialog_sync_error)
        );
    }

    public static void showSyncError(Context pContext, String pMessage) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_error),
                pMessage,
                "Weiter");
    }

    public static void showSyncConfirmation(final AppCompatActivity pContext, String pMessage) {
        show(pContext,
                pContext.getResources().getString(R.string.dialog_sync_complete_confirmation),
                pMessage,
                "Weiter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = pContext.getIntent();
                        dialog.dismiss();
                        pContext.finish();
                        pContext.startActivity(i);
                    }
                });
    }

    public static void showSyncInformation(final AppCompatActivity pContext, DialogInterface.OnClickListener onConfirm) {
        show(pContext,
                pContext.getResources().getString(R.string.dialog_sync_information),
                pContext.getResources().getString(R.string.dialog_sync_information_text),
                pContext.getResources().getString(R.string.dialog_next),
                onConfirm,
                pContext.getResources().getString(R.string.dialog_cancel)
        );
    }

    public static void showDownloadInformation(final AppCompatActivity pContext, DialogInterface.OnClickListener onConfirm) {
        show(pContext,
                pContext.getResources().getString(R.string.dialog_download_information),
                pContext.getResources().getString(R.string.dialog_download_information_text),
                pContext.getResources().getString(R.string.dialog_next),
                onConfirm,
                pContext.getResources().getString(R.string.dialog_cancel)
        );
    }

    public static void showContractDuplicateConfirmation(final Context pContext, DialogInterface.OnClickListener onConfirm) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_duplication_confirmation),
                pContext.getResources().getString(R.string.dialog_duplication_confirmation_text),
                pContext.getResources().getString(R.string.dialog_next),
                onConfirm,
                pContext.getResources().getString(R.string.dialog_cancel));
    }

    public static void showContractDuplicateConfirmed(final AppCompatActivity pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_duplication_confirmed),
                pContext.getResources().getString(R.string.dialog_duplication_confirmed_text),
                pContext.getResources().getString(R.string.dialog_next), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = pContext.getIntent();
                        dialog.dismiss();
                        pContext.finish();
                        pContext.startActivity(i);
                    }
                });
    }

    public static void showContractDuplicateFailed(final Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_duplication_failed),
                pContext.getResources().getString(R.string.dialog_duplication_failed_text),
                pContext.getResources().getString(R.string.dialog_cancel));
    }


    public static void showSyncSingleContractConfirmation(final Context pContext, DialogInterface.OnClickListener onConfirm) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_sync_confirmation),
                pContext.getResources().getString(R.string.dialog_sync_confirmation_text),
                pContext.getResources().getString(R.string.dialog_next),
                onConfirm,
                pContext.getResources().getString(R.string.dialog_cancel));
    }

    public static void showSyncNowConfirmation(final Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_do_sync),
                pContext.getResources().getString(R.string.dialog_do_sync_text),
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!Helper.is_wlan_connected(pContext)) {
                            AlertHelper.showWLANError(pContext);
                        } else {
                            EventListener.do_action("sync_all_contracts_now", pContext);
                        }
                    }
                },
                pContext.getResources().getString(R.string.dialog_cancel));
    }

    public static void showSyncComplete(final Context pContext, final int pCount, final int pCountConfirmed, final int pCountFailed) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_sync_complete),
                pCount + " Verträge gesendet," + pCountConfirmed + " erfolgreich, " + pCountFailed + " fehlerhaft",
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * Event Listener
                         */
                        EventListener.do_action("contract_sync_all_complete", pContext, pCount, pCountConfirmed, pCountFailed);

                        dialog.dismiss();
                    }
                }
        );
    }

    /**
     * Download complete
     */
    public static void showDownloadContractNoDataFound(final Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_download_contract_complete_no_data_found),
                pContext.getResources().getString(R.string.dialog_download_contract_complete_no_data_found_text),
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
    }

    public static void showDownloadContractComplete(final Context pContext, final int pCount, final int pCountCreated, final int pCountUpdated, final int pCountUploaded, DialogInterface.OnClickListener onComplete) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_download_contract_complete),
                pCount + " Verträge abgefragt, " + pCountCreated + " angelegt, " + pCountUpdated + " aktualisiert, " + pCountUploaded + " hochgeladen.",
                pContext.getResources().getString(R.string.dialog_next),
                onComplete
        );
    }

    /**
     * General Errors
     *
     * @param pContext
     */
    public static void showInternError(final Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_error),
                pContext.getResources().getString(R.string.dialog_error_text),
                pContext.getResources().getString(R.string.dialog_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
    }

    /**
     * Show Makler ID error
     *
     * @param pContext
     */
    public static void showMaklerIDError(Context pContext) {
        AlertHelper.show(pContext,
                pContext.getResources().getString(R.string.dialog_makler),
                pContext.getResources().getString(R.string.dialog_makler_text),
                pContext.getResources().getString(R.string.dialog_next)
        );
    }

    /**
     * Use this function to add a confirmation dialog for the back
     * button.
     *
     * @param pContext
     * @param onConfirm
     */
    public static void showOnBackPressedConfirmation(final AppCompatActivity pContext, DialogInterface.OnClickListener onConfirm) {
        //pContext.getResources().getString()
        show(pContext,
                pContext.getResources().getString(R.string.dialog_back),
                pContext.getResources().getString(R.string.dialog_back_text),
                pContext.getResources().getString(R.string.dialog_next),
                onConfirm,
                pContext.getResources().getString(R.string.dialog_cancel)
        );
    }
}
