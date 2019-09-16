package forge12.x_citeapi.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import forge12.x_citeapi.EventListener;

public class SQLHelper extends SQLiteOpenHelper {
    /**
     * Store the Database pool
     */
    private SQLiteDatabase mWriteableDatabase = null;
    private SQLiteDatabase mReadableDatabase = null;
    /**
     * The Sync Object
     */
    private Context mContext;
    /**
     * Path to the Database Files
     */
    private String mPath;

    /**
     * Database Version
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * The Databasename which will be created.
     */
    public static final String DATABASE_NAME = "xcite.db";

    /**
     * Constructor
     *
     * @param context
     */
    public SQLHelper(Context context, String pPath) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mPath = pPath;
    }

    public SQLiteDatabase getmWriteableDatabase() {
        if (mWriteableDatabase == null)
            mWriteableDatabase = getWritableDatabase();

        return mWriteableDatabase;
    }

    private SQLiteDatabase getmReadableDatabase() {
        if (mReadableDatabase == null)
            mReadableDatabase = getReadableDatabase();

        return mReadableDatabase;
    }

    public void close() {
        if (mReadableDatabase != null) {
            mReadableDatabase.close();
        }
        if (mWriteableDatabase != null) {
            mWriteableDatabase.close();
        }
    }

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Delete something from the database
     *
     * @return the number of rows affected
     */
    public int delete(String table, String selector) {
        int res = 0;
        try {
            res = getmWriteableDatabase().delete(table, selector, null);
        } catch (NullPointerException e) {
            Log.v("NULLPOINTER", e.getMessage());
        }
        return res;
    }

    /**
     * Check if a database exists
     */
    public boolean exists() {
        return exists("suewag_tab_vertraege_container");
    }

    /**
     * Check if a database exists
     */
    public boolean exists(String table) {
        boolean res = false;
        try {
            Cursor cursor = getmWriteableDatabase().rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + table + "'", null);

            if (cursor.getCount() > 0) {
                res = true;
            }
            cursor.close();

        } catch (NullPointerException e) {
            Log.v("NULLPOINTER", e.getMessage());
        }
        return res;
    }

    /**
     * Override
     * <p>
     * Called whenever an upgrade of the database should be done.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Remove all tables if there is any upgrade and start the import process.
        drop_tables(db);
        onCreate(db);
    }

    /**
     * Publish message to the eventlistenr
     *
     * @param pMessage
     */
    private void sendMessage(String pMessage) {
        EventListener.do_action("database_progress_update", mContext, pMessage);
    }

    /**
     * Delete all Tables from the database
     *
     * @param db
     */
    private void drop_tables(SQLiteDatabase db) {
        sendMessage("Lösche Tabellen");
        db.execSQL("DROP TABLE IF EXISTS suewag_tab_produkte");
        db.execSQL("DROP TABLE IF EXISTS suewag_tab_produkte_gruppen");
        db.execSQL("DROP TABLE IF EXISTS suewag_tab_produkte_plz");
        db.execSQL("DROP TABLE IF EXISTS suewag_tab_produkte_to_gruppe");
        db.execSQL("DROP TABLE IF EXISTS suewag_tab_vertraege_container");
        db.execSQL("DROP TABLE IF EXISTS suewag_tab_vertraege_container_images");
        sendMessage("Alle Tabellen gelöscht");
    }

    /**
     * Convert an inputstream object into an String
     *
     * @param in
     * @return String
     */
    public String convertstreamtostring(InputStream in) {
        StringWriter writer = new StringWriter();
        try {
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (IOException io) {
            Log.d("IOEXCEPTION", "Error while reading the stream");
        }
        return writer.toString();
    }

    /**
     * Open an SQL File, load the content and return it as an string.
     *
     * @param name
     * @return String
     */
    public InputStream load_file(String name) {
        try {
            InputStream sql = new FileInputStream(name);
            return sql;
            //return convertstreamtostring(sql);

        } catch (FileNotFoundException io) {
            Log.d("IOEXCEPTION", "SQL File not found");
            sendMessage("Datei nicht gefunden: " + name);
            return null;
        }
    }

    /**
     * Loading the File from the assets and executes the sql query.     *
     *
     * @param db
     * @param name
     */
    public void create_table_by_name(SQLiteDatabase db, String name) {
        InputStream inputStream = load_file(name);

        if (inputStream == null) {
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        StringBuilder builder = new StringBuilder();
        int i = 0;
        try {
            while ((line = reader.readLine()) != null) {
                if (i >= 2) {
                    builder.append(line);
                }
                i++;
            }

            String rawSql = builder.toString();
            String[] sqls = rawSql.split(";");
            for (String sql : sqls) {
                db.execSQL(sql);
                Log.d("EXECUTED SQL", sql);
            }
        } catch (IOException e) {
            Log.v("EXCEPTION", e.getMessage());
        }
    }

    /**
     * Loading the File from the assets and executes the sql query.     *
     *
     * @param db
     * @param name
     */
    public void insert_data_by_name(SQLiteDatabase db, String name) {

    }

    /**
     * Use only for Insert / Updates
     *
     * @param statement
     */
    public void query(String statement) {
        try {
            getmWriteableDatabase().execSQL(statement);
        } catch (SQLException sqe) {
            Log.v("SQLEXCEPTION", sqe.getMessage());
            sendMessage(statement + " fehlerhaft");
        }
    }

    public Cursor rawQuery(String statement) {
        try {
            Cursor cursor = getmWriteableDatabase().rawQuery(statement, null);
            return cursor;
        } catch (SQLException sqe) {
            Log.v("SQLEXCEPTION", sqe.getMessage());
        }
        return null;
    }

    /**
     * returns the last insert id for the given id or 0 if an error occured
     *
     * @param table
     * @return
     */
    public int last_insert_id(String table) {
        int id = 0;
        try {
            Cursor cursor = getmWriteableDatabase().rawQuery(String.format("SELECT * FROM SQLITE_SEQUENCE WHERE name='%s'", table), null);

            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndex("seq"));
            }
            cursor.close();
        } catch (SQLException sqe) {
            Log.v("SQLEXCEPTION", sqe.getMessage());
        }
        return id;
    }

    /**
     * Use for selects
     *
     * @param table
     * @param fields
     * @return
     */
    public Cursor fetch(String table, String[] fields, String order) {
        return fetch(table, fields, null, order);
    }

    /**
     * Use for selects
     *
     * @param table
     * @param fields
     * @return
     */
    public Cursor fetch(String table, String[] fields, String selection, String order) {
        try {
            Cursor cursor = getmReadableDatabase().query(table, fields, selection, null, null, null, order);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            return cursor;
        } catch (SQLException sqe) {
            Log.v("SQLEXCEPTION", sqe.getMessage());
            sendMessage(table + " abfrage fehlerhaft");
        }

        return null;
    }

    public void create_tables(String pPath, boolean isUpdate) {
        // Create the tables
        create_table_by_name(getmWriteableDatabase(), pPath + "suewag_tab_produkte.sql");
        create_table_by_name(getmWriteableDatabase(), pPath + "plz.sql");
        create_table_by_name(getmWriteableDatabase(), pPath + "suewag_tab_produkt_plz.sql");
        create_table_by_name(getmWriteableDatabase(), pPath + "suewag_tab_produkte_gruppen.sql");
        create_table_by_name(getmWriteableDatabase(), pPath + "suewag_tab_produkt_to_gruppe.sql");
        create_table_by_name(getmWriteableDatabase(), pPath + "api_settings.sql");
        if (!isUpdate) {
            create_table_by_name(getmWriteableDatabase(), pPath + "suewag_tab_vertraege_container.sql");
            create_table_by_name(getmWriteableDatabase(), pPath + "suewag_tab_vertraege_container_images.sql");
        }

        EventListener.do_action("database_progress_update", mContext, "Datenbank erstellt");
    }

    /**
     * Called on creating the SQLite object.
     * Responsible to create the databases and the content of the
     * tables.
     *
     * @param db
     */
    public void onCreate(SQLiteDatabase db) {

    }
}
