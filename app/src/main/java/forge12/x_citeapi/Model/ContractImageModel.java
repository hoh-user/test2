package forge12.x_citeapi.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import forge12.x_citeapi.Handler.AdminHandler;
import forge12.x_citeapi.Views.Admin;
import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.Utils.HttpUtils;
import forge12.x_citeapi.Utils.SQLHelper;

public class ContractImageModel extends AbstractModel {
    /**
     * The Synchandler for the Success Message
     *
     * @param OnStatusChange
     * @return
     */
    private JsonHttpResponseHandler OnSyncResponseHandler(final Context context, @Nullable final CustomEventListener OnStatusChange) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("CONTRACT IMAGE SYNC", "Response: " + response);
                try {
                    JSONObject server_response = new JSONObject(response.toString());

                    if (!server_response.isNull("status") && server_response.getInt("status") == 0) {
                        String error_message = "";
                        JSONObject error_response = server_response.getJSONObject("error");

                        Iterator<String> keys = error_response.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (error_response.get(key) instanceof JSONObject) {
                                JSONObject error_field = error_response.getJSONObject(key);

                                if (error_field.get("status") instanceof String && error_field.get("field") instanceof String) {
                                    error_message = error_message + Helper.parse_json_field_name(error_field.getString("field")) + ": " + Helper.parse_json_error_status(error_field.getString("status")) + "\n";
                                }
                            }
                        }

                        if(null != OnStatusChange) {
                            OnStatusChange.sendMessage("Daten konnten nicht Synchronisiert werden.\n" + error_message, 0);
                        }
                        return;
                    } else if (server_response.isNull("status") && !server_response.isNull("error") && server_response.getInt("error") == 401) {
                        if(null != OnStatusChange) {
                            OnStatusChange.sendMessage("Zugriff verweigert, bitte prüfen Sie den API Key.", 1);
                        }
                        return;
                    }

                    if(null != OnStatusChange) {
                        OnStatusChange.sendMessage("Daten erfolgreich übertragen.", 1);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    if(null != OnStatusChange) {
                        OnStatusChange.sendMessage("Es ist ein Fehler beim Aktualisieren der Datenbank aufgetreten.", 1);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                Log.v("CONTRACT IMAGE SYNC", "Response: "+errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

            }
        };
    }

    /**
     * Validates if the image is initialized from the database or not.
     * @return boolean returns true if the object contains an image. returns false if it is a new object
     */
    public boolean isValid(){
        if(has("id") && Integer.parseInt(get("id")) != 0){
            return true;
        }
        return false;
    }

    /**
     * Responsible to sync the data with the live server
     */
    public void sync(final Context context, final CustomEventListener OnStatusChange) {
        // Check if the image already exists
        HttpUtils.get("image/" + get("contract_hash") + "/" + get("image_type"), null, AdminHandler.getApiKey(context), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                RequestParams rp = new RequestParams();
                rp.add("contract_id", get("contract_id"));
                rp.add("contract_hash", get("contract_hash"));
                rp.add("image", get("image"));
                rp.add("image_type", get("image_type"));

                HttpUtils.post("image/", rp, AdminHandler.getApiKey(context), OnSyncResponseHandler(context, OnStatusChange));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                Log.v("CONTRACT IMAGE SYNC", "Response: "+errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                /**
                 * Image not available
                 */
                RequestParams rp = new RequestParams();
                rp.add("contract_id", get("contract_id"));
                rp.add("contract_hash", get("contract_hash"));
                rp.add("image", get("image"));
                rp.add("image_type", get("image_type"));

                HttpUtils.put("image/" + get("contract_hash") + "/"+get("image_type"), rp, AdminHandler.getApiKey(context), OnSyncResponseHandler(context, OnStatusChange));
            }
        });

        /*
        if (get("contract_hash") != null && get("contract_hash").length() > 0) {
            HttpUtils.put("image/" + get("contract_hash"), rp, Admin.getApiKey(context), OnSyncResponseHandler(context, OnStatusChange));
        } else {
            HttpUtils.post("image/", rp, Admin.getApiKey(context), OnSyncResponseHandler(context, OnStatusChange));
        }
        */
    }

    /**
     * Loading an specific contract by the given ID
     */
    public static ContractImageModel load(Context context, int contract_id, String image_type, @Nullable CustomEventListener OnStatusChange) {
        ContractImageModel CIM = new ContractImageModel();

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("suewag_tab_vertraege_container_images", new String[]{"id", "contract_id", "contract_hash", "image", "image_type"}, "contract_id='" + contract_id + "' AND image_type='" + image_type + "'", "id DESC");

        if(cursor == null){
            return CIM;
        }

        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return CIM;
        }

        while (!cursor.isAfterLast() && cursor != null) {
            CIM.add("id", cursor.getString(cursor.getColumnIndex("id")));
            CIM.add("contract_id", cursor.getString(cursor.getColumnIndex("contract_id")));
            CIM.add("contract_hash", cursor.getString(cursor.getColumnIndex("contract_hash")));
            CIM.add("image", cursor.getString(cursor.getColumnIndex("image")));
            CIM.add("image_type", cursor.getString(cursor.getColumnIndex("image_type")));

            cursor.moveToNext();
        }

        db.close();
        return CIM;
    }


    /**
     * Loading an specific contract by the given ID
     */
    public static ContractImageModel load(Context context, int ID, @Nullable CustomEventListener OnStatusChange) {
        ContractImageModel CIM = new ContractImageModel();

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("suewag_tab_vertraege_container_images", new String[]{"id", "contract_id", "contract_hash", "image", "image_type"}, "id=" + ID, "id DESC");

        while (!cursor.isAfterLast() && cursor != null) {
            CIM.add("id", cursor.getString(cursor.getColumnIndex("id")));
            CIM.add("contract_id", cursor.getString(cursor.getColumnIndex("contract_id")));
            CIM.add("contract_hash", cursor.getString(cursor.getColumnIndex("contract_hash")));
            CIM.add("image", cursor.getString(cursor.getColumnIndex("image")));
            CIM.add("image_type", cursor.getString(cursor.getColumnIndex("image_type")));

            cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return CIM;
    }

    /**
     * Loading an array of Data from the Database.
     *
     * @param context
     * @param OnStatusChange
     * @return
     */
    public static ArrayList<ContractImageModel> load(Context context, @Nullable CustomEventListener OnStatusChange) {
        ArrayList<ContractImageModel> Dataset = new ArrayList<ContractImageModel>();

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("suewag_tab_vertraege_container_images", new String[]{"id", "contract_id", "contract_hash", "image", "image_type"}, "id DESC");

        while (cursor != null && !cursor.isAfterLast()) {
            ContractImageModel CIM = new ContractImageModel();
            CIM.add("id", cursor.getString(cursor.getColumnIndex("id")));
            CIM.add("contract_id", cursor.getString(cursor.getColumnIndex("contract_id")));
            CIM.add("contract_hash", cursor.getString(cursor.getColumnIndex("contract_hash")));
            CIM.add("image", cursor.getString(cursor.getColumnIndex("image")));
            CIM.add("image_type", cursor.getString(cursor.getColumnIndex("image_type")));

            Dataset.add(CIM);
            cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return Dataset;
    }

    /**
     * Delete data from the database.
     *
     * @param context
     * @param ID
     * @param OnStatusChange
     * @return bool
     */
    public static boolean delete(Context context, int ID, @Nullable CustomEventListener OnStatusChange) {
        SQLHelper db = new SQLHelper(context);
        boolean del = db.delete("suewag_tab_vertraege_container_images", "id=" + ID) > 0;
        db.close();
        return del;
    }

    public void logLargeString(String TAG, String veryLongString) {
        int maxLogSize = 1000;
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Log.v(TAG, veryLongString.substring(start, end));
        }
    }

    /**
     * Insert if new entry
     *
     * @param context
     * @param OnStatusChange
     * @return boolean
     */
    private boolean insert(Context context, @Nullable CustomEventListener OnStatusChange) {
        SQLHelper db = new SQLHelper(context);

        String Statement = "INSERT INTO `suewag_tab_vertraege_container_images` " +
                "(`contract_id`,`contract_hash`, `image`, `image_type`) VALUES (" +
                "'" + get("contract_id") + "'," +
                "'" + get("contract_hash") + "'," +
                "'" + get("image") + "'," +
                "'" + get("image_type") + "'" +
                ")";

        //logLargeString("SQL",Statement);
        db.query(Statement);

        int last_insert_id = db.last_insert_id("suewag_tab_vertraege_container_images");

        Log.v("LastInsertID", Integer.toString(last_insert_id));
        if (last_insert_id == 0) {
            db.close();
            return false;
        }

        ContractImageModel CIM = ContractImageModel.load(context, last_insert_id, null);
        if (CIM == null) {
            db.close();
            return false;
        }

        db.close();
        return compare(CIM);
    }

    /**
     * Compares to Contract Models
     *
     * @param CM
     * @return
     */
    private boolean compare(ContractImageModel CM) {
        if (CM.get("contract_id").equals(this.get("contract_id")) && CM.get("image_type").equals(this.get("image_type"))) {
            return true;
        }
        return false;
    }

    /**
     * Update if existing already
     *
     * @param context
     * @param OnStatusChange
     * @return int 1 if successfully updated
     */
    private int update(Context context, @Nullable CustomEventListener OnStatusChange) {
        SQLHelper db = new SQLHelper(context);

        ContentValues cv = new ContentValues();
        cv.put("contract_id", get("contract_id"));
        cv.put("contract_hash", get("contract_hash"));
        cv.put("image", get("image"));
        cv.put("image_type", get("image_type"));

        int ret = db.getmWriteableDatabase().update("suewag_tab_vertraege_container_images", cv, "id=" + get("id"), null);
        db.close();
        return ret;
    }

    /**
     * Saving the Data to the Database
     *
     * @param context
     * @param OnStatusChange
     */
    public void save(Context context, @Nullable CustomEventListener OnStatusChange) {
        if (get("id").length() > 0) {
            if (update(context, OnStatusChange) == 1) {
                if (null != OnStatusChange) {
                    OnStatusChange.sendMessage("Daten erfolgreich aktualisiert", 0, 200);
                }
            } else {
                if (null != OnStatusChange) {
                    OnStatusChange.sendMessage("Daten konnte nicht aktualisiert werden", 0, 500);
                }
            }
        } else {
            if (insert(context, OnStatusChange)) {
                if (null != OnStatusChange) {
                    OnStatusChange.sendMessage("Daten erfolgreich gespeichert", 0, 200);
                }
            } else {
                if (null != OnStatusChange) {
                    OnStatusChange.sendMessage("Daten konnten nicht gespeichert werden", 0, 200);
                }
            }
        }
    }
}
