package forge12.x_citeapi.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.SQLHelper;

public class ApiSettingsModel extends AbstractModel {
    /**
     * Loading an specific contract by the given ID
     */
    public static ApiSettingsModel load(Context context, String meta_key, @Nullable CustomEventListener OnStatusChange) {
        ApiSettingsModel Settings = new ApiSettingsModel();
        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("api_settings", new String[]{"meta_key", "meta_value"}, "meta_key='" + meta_key + "'", "meta_key ASC");

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
}
