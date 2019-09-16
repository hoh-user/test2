package forge12.x_citeapi.Utils;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import forge12.x_citeapi.EventListener;

public class Unzip extends AsyncTask<String, String, String> {
    String mArchive;
    String mDestination;
    boolean mIsUpdate;
    AppCompatActivity mContext;

    /**
     * Unzip a File
     *
     * @param pArchive
     * @param pDestination
     */
    public Unzip(AppCompatActivity pContext, String pArchive, String pDestination, boolean isUpdate) {
        mArchive = pArchive;
        mDestination = pDestination;
        mContext = pContext;
        mIsUpdate = isUpdate;
    }

    /**
     * Background Function
     *
     * @param strings
     * @return
     */
    @Override
    protected String doInBackground(String... strings) {
        publishProgress("Öffne Archiv");
        unpackZip(mDestination, mArchive);
        return null;
    }

    /**
     * Unpack files
     *
     * @param path
     * @param zipname
     * @return
     */
    private boolean unpackZip(String path, String zipname) {
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                // zapis do souboru
                filename = ze.getName();
                publishProgress("Entpacke " + filename);

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
            publishProgress("Entpacken Abgeschlossen", Integer.toString(CustomEventListener.UNZIP_COMPLETE));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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
            EventListener.do_action("unzip_progress_update", this.mContext, values[0]);
            //mCustomEventListener.sendMessage(values[0], CustomEventListener.REPLACE);
        } else {
            EventListener.do_action("unzip_complete", this.mContext, values[0], mIsUpdate);
            //mCustomEventListener.sendMessage(values[0], CustomEventListener.REPLACE, Integer.parseInt(values[1]));
        }
    }
}
