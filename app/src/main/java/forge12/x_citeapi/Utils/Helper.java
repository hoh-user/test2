package forge12.x_citeapi.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import forge12.x_citeapi.Model.SettingsModel;

public class Helper {
    /**
     * Check for wlan connection
     */
    public static boolean is_wlan_connected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if (wifiInfo == null || wifiInfo.getNetworkId() == -1) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Validate that the databases exist, otherwise we need to do a sync before
     * any data can be saved.
     *
     * @param context
     * @return
     */
    public static boolean is_database_available(Context context) {
        SQLHelper sqlh = new SQLHelper(context, null);
        if (sqlh.exists()) {
            sqlh.close();
            return true;
        }
        sqlh.close();
        return false;
    }

    /**
     * Convert string to md5
     *
     * @param pass
     * @return {string}
     */
    public static String convertPassMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
/*
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(pass.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for(int i = 0; i < messageDigest.length; i++){
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            password = hexString.toString();*/
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    /**
     * Convert Bitmap to Base64 Encoded String
     *
     * @param {Bitmap} bm
     * @return string
     */
    public static String convertBitmapToBase64(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * Convert Base64 Encoded string to bitmap
     *
     * @param {String} base
     * @return Bitmap
     */
    public static Bitmap convertBase64ToBitmap(String base) {
        byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Parse a date to the given format
     *
     * @param pDate
     * @param pOutput
     * @return string
     */
    public static String convertDate(String pDate, String pOutput) {
        return convertDate(pDate, pOutput, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Parse a date to the given format by defining the input format
     *
     * @param pDate
     * @param pOutput
     * @return string
     */
    public static String convertDate(String pDate, String pOutput, String pInput) {
        SimpleDateFormat input = new SimpleDateFormat(pInput);
        SimpleDateFormat output = new SimpleDateFormat(pOutput);

        try {
            Date parsedDate = input.parse(pDate);
            return output.format(parsedDate);
        } catch (ParseException e) {
            return pDate;
        }
    }

    /**
     * returns true if the user and password matches
     *
     * @param username
     * @param password
     * @return
     */
    public static boolean validate_user(Context pContext, String username, String password) {
        // Save email makler
        SettingsModel SMEmail = SettingsModel.load(pContext, "makler_email", null);
        SettingsModel SMPassword = SettingsModel.load(pContext, "makler_password", null);

        String md5password = Helper.convertPassMd5(password);

        if (SMEmail != null && SMPassword != null) {
            if (SMEmail.get("meta_value").equals(username) && SMPassword.get("meta_value").equals(md5password)) {
                return true;
            }
        } else {
            if (username.equals("admin") && password.equals("admin")) {
                return true;
            }
        }
        return false;
    }

    public static String getSpinnerText(AppCompatActivity context, int view) {
        Spinner s = context.findViewById(view);

        if (s == null) {
            return "";
        }

        Object selectedItem = s.getSelectedItem();

        if (selectedItem == null) {
            return "";
        }

        return s.getSelectedItem().toString();
    }

    public static int getBool(AppCompatActivity context, int view) {
        CheckBox chk = context.findViewById(view);

        if (chk == null) {
            return 0;
        }

        if (chk.isChecked()) {
            return 1;
        }
        return 0;
    }

    public static String getText(AppCompatActivity context, int view) {
        return getText(context, view, "");
    }

    public static String getText(AppCompatActivity context, int view, String pDefault) {
        EditText t = context.findViewById(view);

        if (t == null) {
            return pDefault;
        }

        String val = t.getText().toString();
        if (val.length() == 0) {
            return pDefault;
        }
        return val;
    }

    public static String get_textfield_value(EditText text) {
        String ret = text.getText().toString();
        return ret;
    }

    public static String get_textfield_value(EditText text, String pDefault) {
        String ret = get_textfield_value(text);

        if (ret.length() == 0) {
            return pDefault;
        }
        return ret;
    }

    public static int get_checkbox_value(CheckBox checkbox) {
        if (checkbox.isChecked()) {
            return 1;
        }
        return 0;
    }

    public static String parse_json_field_name(final String name) {
        switch (name) {
            case "strasse":
                return "Straße";
            case "hausnummer":
                return "Hausnummer";
            case "ort":
                return "Ort";
            case "iban":
                return "IBAN";
            case "bic":
                return "BIC";
            case "sepa_ermaaechtigung":
                return "SEPA Einzugsverfahren";
            case "agb":
                return "Allgemeine Geschäftsbedingungen";
            case "e_mail_adresse":
                return "E-Mail";
            case "postleitzahl":
                return "Postleitzahl";
        }
        return name;
    }

    public static String parse_json_error_status(final String status) {
        switch (status) {
            case "invalid_length":
                return "Feld nicht ausgefüllt";
            case "not true":
                return "Sie müssen den Bedingungen zustimmen";
            default:
                return "";
        }
    }

    /**
     * Not sure if this works on API 24
     * https://stackoverflow.com/questions/5054132/how-to-change-the-decimal-separator-of-decimalformat-from-comma-to-dot-point
     *
     * @param number
     * @return
     */
    public static String number_format(String number) {
        double amount = Double.parseDouble(number);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat("#,###.00", symbols);
        return formatter.format(amount);
    }
}
