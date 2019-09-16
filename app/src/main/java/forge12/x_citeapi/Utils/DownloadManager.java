package forge12.x_citeapi.Utils;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.apache.commons.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import forge12.x_citeapi.EventListener;

public class DownloadManager extends AsyncTask<String, String, String> {
    String mDownloadLink;
    String mDownloadDestination;
    boolean mIsUpdate;
    AppCompatActivity mContext;

    private static URL get_url(String pURL) {
        try {
            URL url = new URL(pURL);
            return url;
        } catch (MalformedURLException e) {
            Log.v("MALFORMEDURLEXCEPTION", e.getMessage());
        }
        return null;
    }

    private static String get_file_name_from_url(URL pURL) {
        return FilenameUtils.getName(pURL.getPath());
    }

    private boolean create_dir(String pDirectory) {
        File dir = new File(pDirectory);

        if (dir.isDirectory()) {
            return true;
        }

        return dir.mkdirs();
    }

    public DownloadManager(AppCompatActivity pContext, String pDownloadLink, String pDownloadDestination, boolean isUpdate) {
        mContext = pContext;
        mDownloadLink = pDownloadLink;
        mDownloadDestination = pDownloadDestination;
        mIsUpdate = isUpdate;
    }

    /**
     * Return the total file size of the download file.
     * Necessary to calculate the maximum download size for status messages
     *
     * @param pHUC
     * @return
     */
    private int get_download_size(HttpURLConnection pHUC) {
        // Get File Size
        List values = pHUC.getHeaderFields().get("content-length");
        if (values != null && !values.isEmpty()) {
            String sLength = (String) values.get(0);

            if (sLength != null) {
                return Integer.parseInt(sLength);
            }
        }
        return 0;
    }

    /**
     * Background tasks
     *
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        if (!create_dir(mDownloadDestination)) {
            Log.v("APPEXCEPTION", "Couldn't create Download path");
            return "";
        }

        URL url = DownloadManager.get_url(mDownloadLink);

        if (url == null) {
            return "";
        }

        File DownloadFile = null;
        try {
            DownloadFile = new File(mDownloadDestination, DownloadManager.get_file_name_from_url(url));
        } catch (NullPointerException e) {
            Log.v("NULLPOINTEREXCEPTION", e.getMessage());
        }

        if (null == DownloadFile) {
            return "";
        }

        HttpURLConnection HUC = null;
        try {
            HUC = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.v("IOEXCEPTION", e.getMessage());
        }

        if (null == HUC) {
            return "";
        }

        try {
            HUC.setRequestMethod("GET");
        } catch (ProtocolException e) {
            Log.v("PROTOCOLEXCEPTION", e.getMessage());
        }

        HUC.setDoOutput(true);

        try {
            HUC.connect();
        } catch (IOException e) {
            Log.v("IOEXCEPTION", e.getMessage());
        }

        FileOutputStream f = null;
        try {
            f = new FileOutputStream(new File(mDownloadDestination, DownloadManager.get_file_name_from_url(url)));
        } catch (FileNotFoundException e) {
            Log.v("FileNotFoundException", e.getMessage());
        }

        if (null == f) {
            return "";
        }

        InputStream in = null;
        try {
            in = HUC.getInputStream();
        } catch (IOException e) {
            Log.v("IOEXCEPTION", e.getMessage());
        }

        if (null == in) {
            return "";
        }

        // Download the data
        byte[] buffer = new byte[1024];
        int len1 = 0;
        int downloaded = 0;
        int total = get_download_size(HUC);

        try {
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
                downloaded += len1;
                publishProgress("Lade Daten: " + convert(downloaded) + "/" + convert(total));
            }
            f.close();
            publishProgress("Download abgeschlossen", Integer.toString(CustomEventListener.DOWNLOAD_COMPLETE));
        } catch (IOException e) {
            Log.v("IOEXCEPTION", e.getMessage());
        }
        return "";
    }

    /**
     * Convert bytes to size
     *
     * @param value
     * @return
     */
    private String convert(float value) {
        long kb = 1024, mb = kb * 1024, gb = mb * 1024;

        if (value < kb) {
            String speed = (value + "");
            speed = speed.substring(0, speed.indexOf('.') + 2);
            return speed + " B";
        } else if (value < mb) {
            value = value / kb;
            String speed = (value + "");
            speed = speed.substring(0, speed.indexOf('.'));
            return (speed) + " KB";
        } else if (value < gb) {
            value = (value / mb);
            String speed = (value + "");
            speed = speed.substring(0, speed.indexOf('.'));
            return speed + " MB";
        }
        return "";
    }

    /**
     * OnProgress Update
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values.length != 2) {
            EventListener.do_action("download_manager_progress_update", mContext, values[0]);
            //mCustomEventListener.sendMessage(values[0], CustomEventListener.REPLACE);
        } else {
            EventListener.do_action("download_manager_complete", mContext, values[0], mIsUpdate);
            //mCustomEventListener.sendMessage(values[0], CustomEventListener.REPLACE, Integer.parseInt(values[1]));
        }
    }

    /**
     * Delete
     */
    public static void delete(String pPath) {
        File dir = new File(pPath);

        if (dir != null) {
            String[] children = dir.list();

            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    File f = new File(dir, children[i]);

                    if (f.isDirectory()) {
                        delete(f.getPath());
                    }
                    f.delete();
                }
            }

            dir.delete();
        }
    }
}