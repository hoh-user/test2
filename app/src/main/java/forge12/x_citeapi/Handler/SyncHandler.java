package forge12.x_citeapi.Handler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Model.SettingsModel;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.DownloadManager;
import forge12.x_citeapi.Utils.HttpUtils;
import forge12.x_citeapi.Utils.SQLHelper;
import forge12.x_citeapi.Utils.Unzip;
import forge12.x_citeapi.Views.Admin_Api;
import forge12.x_citeapi.Views.FragmentLoading;
import forge12.x_citeapi.Views.Sync;
import forge12.x_citeapi.interfaces.CallableAction;

public class SyncHandler {
    /**
     * Saves the instance of the SyncHandler
     */
    private static SyncHandler _sInstance = null;

    public static int RESET_LOADING_WINDOW = 2000;
    public static int UPDATE_LOADING_WINDOW = 2001;

    /**
     * Returns the requested SyncHandler
     *
     * @return SyncHandler
     */
    public static SyncHandler getInstance() {
        if (_sInstance == null) {
            _sInstance = new SyncHandler();
        }

        return _sInstance;
    }


    /**
     * Called whenever the download completes
     */
    private CallableAction mOnDownloadComplete = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];
            String progress = (String) args[1];
            boolean isUpdate = (boolean) args[2];

            EventListener.do_action("download_manager_progress_update", context, progress);
            EventListener.do_action("sync_after_download_complete", context, isUpdate);
        }
    };

    /**
     * Called whenever the Sync should be resettet
     */
    private CallableAction mOnSyncReset = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];

            FragmentLoading.openDialog(context, RESET_LOADING_WINDOW);

            //context.mStatusMessage.setText("Starte Download");

            EventListener.do_action("sync_after_reset_called", context, false);
        }
    };

    /**
     * Called whenever the Sync should be resettet
     */
    private CallableAction mOnSyncUpdate = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];

            FragmentLoading.openDialog(context, UPDATE_LOADING_WINDOW);

            EventListener.do_action("sync_after_update_called", context, true);
        }
    };

    /**
     * Download the database.zip
     * from the webserver.
     */
    private CallableAction mStartDownload = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            final AppCompatActivity context = (AppCompatActivity) args[0];
            boolean isUpdate = (boolean) args[1];

            DownloadManager dm;

            //final String path = "http://localhost/www.x-cite-group.de/app/";
            //final String path = "http://192.168.178.26/www.x-cite-group.de/app/";
            final String path = "http://www.x-cite.de/api/app/";

            if (isUpdate == false) {
                dm = new DownloadManager(context, path + "database.zip", Environment.getExternalStorageDirectory() + "/Download/x_cite/", false);
                dm.execute();
            } else {
                if (AdminHandler.getApiKey(context).length() == 0) {
                    AlertHelper.showAPIKeyMissing(context);
                    FragmentLoading.closeDialog(UPDATE_LOADING_WINDOW);
                } else {
                    HttpUtils.get("sync", null, AdminHandler.getApiKey(context), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            //DownloadManager dm = new DownloadManager(context, path + "update.zip", Environment.getExternalStorageDirectory() + "/Download/x_cite/", true);
                            //dm.execute();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                            Log.v("DOWNLOAD DATABASE", "Response: "+errorResponse);
                        }
                    });
                }
            }
        }
    };

    /**
     * Starting the unzip progress
     */
    private CallableAction mStartUnzip = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof AppCompatActivity)) {
                return;
            }

            AppCompatActivity context = (AppCompatActivity) args[0];
            boolean isUpdate = (boolean) args[1];

            String path;

            if (isUpdate) {
                path = Environment.getExternalStorageDirectory() + "/Download/x_cite/update.zip";
            } else {
                path = Environment.getExternalStorageDirectory() + "/Download/x_cite/database.zip";
            }
            Unzip uz = new Unzip(context, path, Environment.getExternalStorageDirectory() + "/Download/x_cite/", isUpdate);
            uz.execute();
        }
    };

    /**
     * Called whenever the progress is updated
     */
    private CallableAction mStatusUpdate = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];
            String progress = (String) args[1];

            //context.mStatusMessage.setText(progress);
        }
    };

    /**
     * Remove all files that are not used anymore
     */
    private CallableAction mRemoveDownloadedFiles = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];

            DownloadManager.delete(Environment.getExternalStorageDirectory() + "/Download/x_cite/");
            //context.mStatusMessage.setText("Synchronisation abgeschlossen");
        }
    };


    /**
     * Called whenever the unzip progress is completed
     */
    private CallableAction mOnUnzipComplete = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];
            String progress = (String) args[1];
            boolean isUpdate = (boolean) args[2];

            EventListener.do_action("unzip_progress_update", context, progress);
            EventListener.do_action("sync_after_unzip_complete", context, isUpdate);
        }
    };

    /**
     * Inject the database
     */
    private CallableAction mStartDatabaseInjection = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof Context)) {
                return;
            }

            Context context = (Context) args[0];
            boolean isUpdate = (boolean) args[1];

            if (!isUpdate) {
                context.deleteDatabase("xcite.db");

                EventListener.do_action("sync_update_status", context, "Starte Datenbank aufbau");

                SQLHelper helper = new SQLHelper(context, Environment.getExternalStorageDirectory() + "/Download/x_cite/");
                helper.create_tables(Environment.getExternalStorageDirectory() + "/Download/x_cite/", false);

                // Adding a custom Column name (SYNC_READY) to the table.
                helper.query("ALTER TABLE suewag_tab_vertraege_container ADD sync_ready INTEGER DEFAULT 0");

                helper.close();

                SettingsModel.database_exists(context);
                EventListener.do_action("sync_after_reset_complete", context);
            } else {
                SQLHelper helper = new SQLHelper(context);
                SQLiteDatabase db = helper.getmWriteableDatabase();
                db.execSQL("DROP TABLE suewag_tab_produkt_to_gruppe");
                db.execSQL("DROP TABLE suewag_tab_produkt_plz");
                db.execSQL("DROP TABLE suewag_tab_produkte_gruppen");
                db.execSQL("DROP TABLE suewag_tab_produkte");
                db.execSQL("DROP TABLE plz");
                db.execSQL("DROP TABLE api_settings");

                helper.create_tables(Environment.getExternalStorageDirectory() + "/Download/x_cite/", true);

                db.close();
                EventListener.do_action("sync_after_update_complete", context);
            }
            EventListener.do_action("sync_after_database_injection_complete", context);
        }
    };

    /**
     * Default constructor
     */
    private SyncHandler() {
        // Actions
        EventListener.add_action("sync_update_status", mStatusUpdate, 10, 2);

        EventListener.add_action("sync_after_database_injection_complete", mRemoveDownloadedFiles, 10, 1);
        EventListener.add_action("database_progress_update", mStatusUpdate, 10, 2);

        EventListener.add_action("sync_after_unzip_complete", mStartDatabaseInjection, 10, 2);

        EventListener.add_action("sync_after_download_complete", mStartUnzip, 10, 2);
        EventListener.add_action("unzip_progress_update", mStatusUpdate, 10, 2);
        EventListener.add_action("unzip_complete", mOnUnzipComplete, 10, 3);

        EventListener.add_action("sync_after_reset_called", mStartDownload, 10, 2);
        EventListener.add_action("download_manager_progress_update", mStatusUpdate, 10, 2);
        EventListener.add_action("download_manager_complete", mOnDownloadComplete, 10, 3);

        EventListener.add_action("sync_do_reset", mOnSyncReset, 10, 1);

        EventListener.add_action("sync_after_update_called", mStartDownload, 10, 2);
        EventListener.add_action("sync_do_update", mOnSyncUpdate, 10, 1);

        EventListener.add_action("sync_after_database_injection_complete", new CallableAction() {
            @Override
            public void onCall(Object... args) {
                FragmentLoading.closeDialog(RESET_LOADING_WINDOW);
                FragmentLoading.closeDialog(UPDATE_LOADING_WINDOW);
            }
        }, 11, 1);

        EventListener.add_action("sync_after_update_complete", new CallableAction(){
            @Override
            public void onCall(Object... args){
                if(!(args[0] instanceof Sync)){
                    return;
                }

                Context context = (Context) args[0];

                AlertHelper.showDatabaseUpdated(context);
            }
        }, 11, 1);

        EventListener.add_action("sync_after_reset_complete", new CallableAction(){
            @Override
            public void onCall(Object... args){
                if(!(args[0] instanceof Admin_Api)){
                    return;
                }

                Context context = (Context) args[0];

                AlertHelper.showDatabaseReset(context);
            }
        }, 11, 1);
    }
}
