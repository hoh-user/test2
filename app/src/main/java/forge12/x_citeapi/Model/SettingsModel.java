package forge12.x_citeapi.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.SQLHelper;

public class SettingsModel extends AbstractModel {
    @Override
    public String get(String key) {
        String result = super.get(key);

        if (key.equals("vertragsdatum") || key.equals("birthday") || key.equals("moving_in_date") || key.equals("distributor_change_name") || key.equals("account_sepa_date")) {
            if (result.length() == 0) {
                return "1970-01-01";
            }
        }
        return result;
    }

    /**
     * Loading an specific contract by the given ID
     */
    public static SettingsModel load(Context context, String meta_key, @Nullable CustomEventListener OnStatusChange) {
        SettingsModel Settings = new SettingsModel();
        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("settings", new String[]{"meta_key", "meta_value"}, "meta_key='" + meta_key + "'", "meta_key ASC");

        while (cursor != null && !cursor.isAfterLast()) {
            Settings.add("meta_key", cursor.getString(cursor.getColumnIndex("meta_key")));
            Settings.add("meta_value", cursor.getString(cursor.getColumnIndex("meta_value")));

            cursor.moveToNext();
        }

        if (cursor != null) {
            int size = cursor.getCount();
            cursor.close();
            db.close();
            if (size > 0) {
                return Settings;
            }
        }

        db.close();
        return null;
    }

    /**
     * Loading an array of Data from the Database.
     *
     * @param context
     * @param OnStatusChange
     * @return
     */
    public static ArrayList<SettingsModel> load(Context context, @Nullable CustomEventListener OnStatusChange) {
        ArrayList<SettingsModel> Dataset = new ArrayList<SettingsModel>();

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("settings", new String[]{"meta_key", "meta_value"}, "meta_key ASC");

        while (cursor != null && !cursor.isAfterLast()) {
            SettingsModel CM = new SettingsModel();
            CM.add("meta_key", cursor.getString(cursor.getColumnIndex("meta_key")));
            CM.add("meta_value", cursor.getString(cursor.getColumnIndex("meta_value")));

            Dataset.add(CM);
            cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return Dataset;
    }

    /**
     * Delete an contract model from the database and returns true if it worked.
     *
     * @param context
     * @param meta_key
     * @param OnStatusChange
     * @return
     */
    public static boolean delete(Context context, int meta_key, @Nullable CustomEventListener OnStatusChange) {
        SQLHelper db = new SQLHelper(context);
        boolean res = db.delete("settings", "meta_key=" + meta_key) > 0;
        db.close();
        return res;
    }

    /**
     * Insert if new entry
     *
     * @param context
     * @param OnStatusChange
     * @return the row id or -1 if an error occured
     */
    private long insert(Context context, @Nullable CustomEventListener OnStatusChange) {
        SQLHelper db = new SQLHelper(context);

        ContentValues cv = new ContentValues();
        cv.put("meta_key", get("meta_key"));
        cv.put("meta_value", get("meta_value"));

        long ret = db.getmWriteableDatabase().insert("settings", null, cv);
        db.close();
        return ret;
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
        cv.put("meta_key", get("meta_key"));
        cv.put("meta_value", get("meta_value"));

        int ret = db.getmWriteableDatabase().update("settings", cv, "meta_key='" + get("meta_key") + "'", null);
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
        SettingsModel SM = load(context, this.get("meta_key"), null);

        if (null != SM) {
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
            if (insert(context, OnStatusChange) != -1) {
                if (null != OnStatusChange) {
                    OnStatusChange.sendMessage("Daten erfolgreich gespeichert", 0, 200);
                }
            } else {
                if (null != OnStatusChange) {
                    OnStatusChange.sendMessage("Daten konnten nicht gespeichert werden", 0, 500);
                }
            }
        }
    }

    /**
     * Validate if the database exists
     */
    public static boolean database_exists(Context context) {
        SQLHelper sqlh = new SQLHelper(context, null);
        if (sqlh.exists("settings")) {
            sqlh.close();
            return true;
        }
        sqlh.query("CREATE TABLE settings (meta_key text NOT NULL UNIQUE, meta_value text NOT NULL)");
        sqlh.close();
        return true;
    }
}
