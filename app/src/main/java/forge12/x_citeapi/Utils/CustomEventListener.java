package forge12.x_citeapi.Utils;

import android.util.Log;

public class CustomEventListener {
    public static final int INIT = 1, APPEND = 100, REPLACE = 101, DOWNLOAD_RUN = 109, DOWNLOAD_COMPLETE = 110, UNZIP_COMPLETE = 120, DATABASE_DELETED = 128, DATABASE_CREATED = 129, DATABASE_COMPLETE = 130, COMPLETE = 900;

    public void sendMessage(String message) {
        Log.v("Message", message);
    }

    public void sendMessage(String message, int option) {
        sendMessage(message);
    }

    public void sendMessage(String message, int option, int code) {
        sendMessage(message, option);
    }

    public void sendMessage(String message, int option, int code, Object data) {
        sendMessage(message, option, code);
    }
}