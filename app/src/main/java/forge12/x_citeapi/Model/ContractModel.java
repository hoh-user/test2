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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import forge12.x_citeapi.Handler.AdminHandler;
import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.Utils.HttpUtils;
import forge12.x_citeapi.Utils.SQLHelper;
import forge12.x_citeapi.Views.Admin;
import forge12.x_citeapi.interfaces.CallableAction;

public class ContractModel extends AbstractModel {

    /**
     * Adding custom filter & actions
     */
    public static void init() {
        EventListener.add_filter("field_filter", field_filter(), 10, 2);
    }

    /**
     * Filter that will be used to overwrite contract data.
     *
     * @return {CallableAction}
     */
    public static CallableAction field_filter() {
        return new CallableAction() {
            @Override
            public String onCall(String arg, String... args) {


                switch (args[0]) {
                    case "sepa_datum":
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                        arg = sdf.format(new Date());
                        break;
                    case "moving_in_date":
                    case "distributor_change_date":
                        if (arg.equals("1970-01-01") || arg.equals("0000-00-00")) {
                            return "";
                        }
                        break;
                    case "timestamp":
                        if (arg.length() == 0) {
                            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            timestamp.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                            arg = timestamp.format(new Date());
                        }
                        break;
                    case "status":
                        if (arg.length() == 0) {
                            arg = "0";
                        }
                        break;
                }
                return arg;
            }
        };
    }

    public ContractModel() {

    }

    /**
     * The ContractModel created / initialized by the json object from the server
     *
     * @param cursor JSONObject
     */
    public ContractModel(JSONObject cursor) {
        try {
            add("makler_id", cursor.getString("makler_id"));
            add("partner_id_auftrag", cursor.getString("partner_id_auftrag"));
            add("xcite_vertrag_id", cursor.getString("xcite_vertrag_id"));
            add("produkt_id", cursor.getString("produkt_id"));
            add("vertragsdatum", cursor.getString("vertragsdatum"));
            add("salutation", cursor.getString("Anredetext"));
            add("lastname", cursor.getString("Name"));
            add("firstname", cursor.getString("Vorname"));
            add("street", cursor.getString("strasse"));
            add("zip", cursor.getString("postleitzahl"));
            add("housenumber", cursor.getString("hausnummer"));
            add("city", cursor.getString("ort"));
            add("phone", cursor.getString("Telefonnummer"));
            add("mobil", cursor.getString("Mobil"));
            add("email", cursor.getString("e_mail_adresse"));
            add("birthday", cursor.getString("Geburtsdatum"));
            add("electric_meter_number", cursor.getString("zaehlernummer"));
            add("company_active", cursor.getString("Gewerbe"));
            add("company", cursor.getString("Firma"));
            add("company_register_court", cursor.getString("Registergericht"));
            add("company_register_number", cursor.getString("Registernummer"));
            add("electric_meter_reading", cursor.getString("zaehlerstand"));
            add("invoice_recipient", cursor.getString("rea_name"));
            add("invoice_recipient_street", cursor.getString("rea_street"));
            add("invoice_recipient_housenumber", cursor.getString("rea_house_num"));
            add("invoice_recipient_zip", cursor.getString("rea_post"));
            add("invoice_recipient_city", cursor.getString("rea_city"));
            add("account_iban", cursor.getString("iban"));
            add("account_holder", cursor.getString("Kontoinhaber"));
            add("account_sepa_date", cursor.getString("sepa_datum"));
            add("account_sepa", cursor.getString("sepa_ermaaechtigung"));
            add("ordering", cursor.getString("agb"));
            add("marketing", cursor.getString("optin"));
            add("moving_in", cursor.getString("stromlieferung_neu"));
            add("moving_in_date", cursor.getString("stromlieferung_einzug"));
            add("distributor_change", cursor.getString("stromlieferung_wechsel"));
            add("distributor_change_date", cursor.getString("stromlieferung_wunsch"));
            add("distributor_name", cursor.getString("stromlieferung_alt_name"));
            add("distributor_number", cursor.getString("stromlieferung_alt_kundennummer"));
            add("power_consumption", cursor.getString("stromlieferung_alt_verbrauch"));
            add("hash", cursor.getString("hash"));
            add("timestamp", cursor.getString("timestamp"));
            add("status", cursor.getString("status"));
            add("vertragsart", cursor.getString("vertragsart"));
            add("anrede_titel", cursor.getString("anrede_titel"));
            add("mieter_1_titel", cursor.getString("mieter_1_titel"));
            add("mieter_1_anrede", cursor.getString("mieter_1_anrede"));
            add("mieter_1_vorname", cursor.getString("mieter_1_vorname"));
            add("mieter_1_nachname", cursor.getString("mieter_1_nachname"));
            add("mieter_1_strasse", cursor.getString("mieter_1_strasse"));
            add("mieter_1_hnr", cursor.getString("mieter_1_hnr"));
            add("mieter_1_plz", cursor.getString("mieter_1_plz"));
            add("mieter_1_ort", cursor.getString("mieter_1_ort"));
            add("mieter_1_email", cursor.getString("mieter_1_email"));
            add("mieter_1_tel", cursor.getString("mieter_1_tel"));
            add("mieter_1_gebdat", cursor.getString("mieter_1_gebdat"));
            add("vermieter_anrede", cursor.getString("vermieter_anrede"));
            add("vermieter_vorname", cursor.getString("vermieter_vorname"));
            add("vermieter_nachname", cursor.getString("vermieter_nachname"));
            add("vermieter_strasse", cursor.getString("vermieter_strasse"));
            add("vermieter_hnr", cursor.getString("vermieter_hnr"));
            add("vermieter_plz", cursor.getString("vermieter_plz"));
            add("vermiter_ort", cursor.getString("vermiter_ort"));
            add("whg_miete", cursor.getString("whg_miete"));
            add("whg_kaution", cursor.getString("whg_kaution"));
            add("whg_barkaution", cursor.getString("whg_barkaution"));
            add("whg_temp", cursor.getString("whg_temp"));
            add("whg_kuendigung", cursor.getString("whg_kuendigung"));
            add("whg_mietverzug", cursor.getString("whg_mietverzug"));
            add("whg_schaeden", cursor.getString("whg_schaeden"));
            add("whg_kaution_no", cursor.getString("whg_kaution_no"));
            add("whg_kaution_bes_no", cursor.getString("whg_kaution_bes_no"));
            add("whg_versand", cursor.getString("whg_versand"));
            add("stromlieferung_strasse", cursor.getString("stromlieferung_strasse"));
            add("stromlieferung_hausnummer", cursor.getString("stromlieferung_hausnummer"));
            add("stromlieferung_plz", cursor.getString("stromlieferung_plz"));
            add("stromlieferung_ort", cursor.getString("stromlieferung_ort"));
            //add("sync_ready", cursor.getString("sync_ready"));
        } catch (JSONException e) {
            Log.v("JSONException", e.getMessage());
        }
    }

    /**
     * Create a new Object of the Contract Model
     *
     * @param cursor
     */
    public ContractModel(Cursor cursor) {
        if (cursor == null) {
            return;
        }

        add("ID", cursor.getString(cursor.getColumnIndex("ID")));
        add("vertragsart", cursor.getString(cursor.getColumnIndex("vertragsart")));
        add("makler_id", cursor.getString(cursor.getColumnIndex("makler_id")));
        add("xcite_vertrag_id", cursor.getString(cursor.getColumnIndex("xcite_vertrag_id")));
        add("produkt_id", cursor.getString(cursor.getColumnIndex("produkt_id")));
        add("partner_id_auftrag", cursor.getString(cursor.getColumnIndex("partner_id_auftrag")));
        add("vertragsdatum", cursor.getString(cursor.getColumnIndex("vertragsdatum")));
        add("salutation", cursor.getString(cursor.getColumnIndex("Anredetext")));
        add("lastname", cursor.getString(cursor.getColumnIndex("Name")));
        add("firstname", cursor.getString(cursor.getColumnIndex("Vorname")));
        add("street", cursor.getString(cursor.getColumnIndex("strasse")));
        add("zip", cursor.getString(cursor.getColumnIndex("postleitzahl")));
        add("housenumber", cursor.getString(cursor.getColumnIndex("hausnummer")));
        add("city", cursor.getString(cursor.getColumnIndex("ort")));
        add("phone", cursor.getString(cursor.getColumnIndex("Telefonnummer")));
        add("mobil", cursor.getString(cursor.getColumnIndex("Mobil")));
        add("company_active", cursor.getString(cursor.getColumnIndex("Gewerbe")));
        add("company", cursor.getString(cursor.getColumnIndex("Firma")));
        add("company_register_court", cursor.getString(cursor.getColumnIndex("Registergericht")));
        add("company_register_number", cursor.getString(cursor.getColumnIndex("Registernummer")));
        add("email", cursor.getString(cursor.getColumnIndex("e_mail_adresse")));
        add("birthday", cursor.getString(cursor.getColumnIndex("Geburtsdatum")));
        add("electric_meter_number", cursor.getString(cursor.getColumnIndex("zaehlernummer")));
        add("versorgungstermin", cursor.getString(cursor.getColumnIndex("versorgungstermin")));
        add("electric_meter_reading", cursor.getString(cursor.getColumnIndex("zaehlerstand")));
        add("account_iban", cursor.getString(cursor.getColumnIndex("iban")));
        add("account_bic", cursor.getString(cursor.getColumnIndex("bic")));
        add("account_holder", cursor.getString(cursor.getColumnIndex("Kontoinhaber")));
        add("account_sepa_date", cursor.getString(cursor.getColumnIndex("sepa_datum")));
        add("account_sepa", cursor.getString(cursor.getColumnIndex("sepa_ermaaechtigung")));
        add("ordering", cursor.getString(cursor.getColumnIndex("agb")));
        add("marketing", cursor.getString(cursor.getColumnIndex("optin")));
        add("freigabe_kunde", cursor.getString(cursor.getColumnIndex("freigabe_kunde")));
        add("status", cursor.getString(cursor.getColumnIndex("status")));
        add("prov_vp", cursor.getString(cursor.getColumnIndex("prov_vp")));
        add("invoice_recipient", cursor.getString(cursor.getColumnIndex("rea_name")));
        add("invoice_recipient_street", cursor.getString(cursor.getColumnIndex("rea_street")));
        add("invoice_recipient_housenumber", cursor.getString(cursor.getColumnIndex("rea_house_num")));
        add("invoice_recipient_zip", cursor.getString(cursor.getColumnIndex("rea_post")));
        add("invoice_recipient_city", cursor.getString(cursor.getColumnIndex("rea_city")));
        add("hash", cursor.getString(cursor.getColumnIndex("hash")));
        add("timestamp", cursor.getString(cursor.getColumnIndex("timestamp")));
        add("moving_in", cursor.getString(cursor.getColumnIndex("stromlieferung_neu")));
        add("moving_in_date", cursor.getString(cursor.getColumnIndex("stromlieferung_einzug")));
        add("distributor_change", cursor.getString(cursor.getColumnIndex("stromlieferung_wechsel")));
        add("distributor_change_date", cursor.getString(cursor.getColumnIndex("stromlieferung_wunsch")));
        add("distributor_name", cursor.getString(cursor.getColumnIndex("stromlieferung_alt_name")));
        add("distributor_number", cursor.getString(cursor.getColumnIndex("stromlieferung_alt_kundennummer")));
        add("power_consumption", cursor.getString(cursor.getColumnIndex("stromlieferung_alt_verbrauch")));

        add("sync_ready", cursor.getString(cursor.getColumnIndex("sync_ready")));

        // new
        add("anrede_titel", cursor.getString(cursor.getColumnIndex("anrede_titel")));
        add("mieter_1_titel", cursor.getString(cursor.getColumnIndex("mieter_1_titel")));
        add("mieter_1_anrede", cursor.getString(cursor.getColumnIndex("mieter_1_anrede")));
        add("mieter_1_vorname", cursor.getString(cursor.getColumnIndex("mieter_1_vorname")));
        add("mieter_1_nachname", cursor.getString(cursor.getColumnIndex("mieter_1_nachname")));
        add("mieter_1_strasse", cursor.getString(cursor.getColumnIndex("mieter_1_nachname")));
        add("mieter_1_hnr", cursor.getString(cursor.getColumnIndex("mieter_1_hnr")));
        add("mieter_1_plz", cursor.getString(cursor.getColumnIndex("mieter_1_plz")));
        add("mieter_1_ort", cursor.getString(cursor.getColumnIndex("mieter_1_ort")));
        add("mieter_1_email", cursor.getString(cursor.getColumnIndex("mieter_1_email")));
        add("mieter_1_tel", cursor.getString(cursor.getColumnIndex("mieter_1_tel")));
        add("mieter_1_gebdat", cursor.getString(cursor.getColumnIndex("mieter_1_gebdat")));
        add("vermieter_anrede", cursor.getString(cursor.getColumnIndex("vermieter_anrede")));
        add("vermieter_vorname", cursor.getString(cursor.getColumnIndex("vermieter_vorname")));
        add("vermieter_nachname", cursor.getString(cursor.getColumnIndex("vermieter_nachname")));
        add("vermieter_strasse", cursor.getString(cursor.getColumnIndex("vermieter_strasse")));
        add("vermieter_hnr", cursor.getString(cursor.getColumnIndex("vermieter_hnr")));
        add("vermieter_plz", cursor.getString(cursor.getColumnIndex("vermieter_plz")));
        add("vermiter_ort", cursor.getString(cursor.getColumnIndex("vermiter_ort")));
        add("whg_miete", cursor.getString(cursor.getColumnIndex("whg_miete")));
        add("whg_kaution", cursor.getString(cursor.getColumnIndex("whg_kaution")));
        add("whg_barkaution", cursor.getString(cursor.getColumnIndex("whg_barkaution")));
        add("whg_temp", cursor.getString(cursor.getColumnIndex("whg_temp")));
        add("whg_kuendigung", cursor.getString(cursor.getColumnIndex("whg_kuendigung")));
        add("whg_mietverzug", cursor.getString(cursor.getColumnIndex("whg_mietverzug")));
        add("whg_schaeden", cursor.getString(cursor.getColumnIndex("whg_schaeden")));
        add("whg_kaution_no", cursor.getString(cursor.getColumnIndex("whg_kaution_no")));
        add("whg_kaution_bes_no", cursor.getString(cursor.getColumnIndex("whg_kaution_bes_no")));
        add("whg_versand", cursor.getString(cursor.getColumnIndex("whg_versand")));
        add("stromlieferung_strasse", cursor.getString(cursor.getColumnIndex("stromlieferung_strasse")));
        add("stromlieferung_hausnummer", cursor.getString(cursor.getColumnIndex("stromlieferung_hausnummer")));
        add("stromlieferung_plz", cursor.getString(cursor.getColumnIndex("stromlieferung_plz")));
        add("stromlieferung_ort", cursor.getString(cursor.getColumnIndex("stromlieferung_ort")));
    }

    /**
     * The Synchandler for the Success Message
     *
     * @param OnStatusChange
     * @return
     */
    private JsonHttpResponseHandler OnSyncResponseHandler(final Context context, @Nullable final CustomEventListener OnStatusChange) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                Log.v("CONTRACT SYNC", "Response: " + errorResponse);

                if (OnStatusChange == null) {
                    return;
                }

                OnStatusChange.sendMessage("Daten konnten nicht Synchronisiert werden.", 0, 503, ContractModel.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                //Log.d("asd", "---------------- this is response : " + response);
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

                        if (OnStatusChange != null) {
                            OnStatusChange.sendMessage("Daten konnten nicht Synchronisiert werden.\n" + error_message, 0, 503, ContractModel.this);
                        }

                        return;
                    } else if (server_response.isNull("status") && !server_response.isNull("error") && server_response.getInt("error") == 401) {
                        if (OnStatusChange != null) {
                            OnStatusChange.sendMessage("Zugriff verweigert, bitte prüfen Sie den API Key.", 1, 401, ContractModel.this);
                        }
                        return;
                    }

                    if (server_response.getString("hash") != null) {
                        //if ((get("hash") == null || get("hash").length() == 0) && server_response.getString("hash") != null) {
                        // Set the Hash value for the object
                        set("hash", server_response.getString("hash"));
                        set("sync_ready", 0); // reset sync ready to ensure it is not synced again.

                        ContractImageModel Image = ContractImageModel.load(context, Integer.parseInt(get("ID")), "image", null);
                        ContractImageModel Signature = ContractImageModel.load(context, Integer.parseInt(get("ID")), "signature", null);

                        // Updating the images
                        if (Image != null) {
                            Image.set("contract_hash", get("hash"));
                            Image.save(context, null);
                        }

                        if (Signature != null) {
                            Signature.set("contract_hash", get("hash"));
                            Signature.save(context, null);
                        }
                    }

                    if (update_internal(context, OnStatusChange) == 1) {
                        if (OnStatusChange != null) {
                            OnStatusChange.sendMessage("Daten erfolgreich übertragen.", 1, 200, ContractModel.this); // Changed from 1 to 200
                        }
                    } else {
                        if (OnStatusChange != null) {
                            OnStatusChange.sendMessage("Es ist ein Fehler beim Aktualisieren der Datenbank aufgetreten.", 1, 503, ContractModel.this);
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    if (OnStatusChange != null) {
                        OnStatusChange.sendMessage("Es ist ein Fehler beim Aktualisieren der Datenbank aufgetreten.", 1, 503, ContractModel.this);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

            }
        };
    }

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
     * Sync the contract with lock
     *
     * @param context
     * @param OnStatusChange
     */
    public void sync_and_lock(final Context context, @Nullable final CustomEventListener OnStatusChange) {
        sync(context, true, new CustomEventListener() {
            @Override
            public void sendMessage(String message, int option, int code, Object data) {
                if (code == 503) {
                    if (data instanceof ContractModel) {
                        ContractModel CM = (ContractModel) data;
                        CM.setStatusUnsynced(context);

                        if (null != OnStatusChange) {
                            OnStatusChange.sendMessage(message);
                        }
                    }
                } else {
                    if (null != OnStatusChange) {
                        OnStatusChange.sendMessage(message, option);
                    }
                }
            }
        });
    }

    /**
     * Sync the contract without locking
     *
     * @param context
     * @param OnStatusChange
     */
    public void sync(final Context context, @Nullable final CustomEventListener OnStatusChange) {
        sync(context, false, OnStatusChange);
    }

    public String getSalutation(String salutation) {
        if (!salutation.equals("Herr") && !salutation.equals("Frau")) {
            salutation = "Herr";
        }
        return salutation;
    }

    /**
     * Responsible to sync the data with the live server
     */
    public void sync(final Context context, boolean lock, @Nullable final CustomEventListener OnStatusChange) {
        RequestParams rp = new RequestParams();

        rp.add("vertragsart", get("vertragsart"));
        rp.add("makler_id", get("makler_id"));
        rp.add("xcite_vertrag_id", get("xcite_vertrag_id"));
        rp.add("produkt_id", get("produkt_id"));
        rp.add("partner_id_auftrag", get("partner_id_auftrag"));
        rp.add("vertragsdatum", get("vertragsdatum"));
        rp.add("Anredetext", getSalutation(get("salutation")));
        rp.add("Name", get("lastname"));
        rp.add("Vorname", get("firstname"));
        rp.add("strasse", get("street"));
        rp.add("postleitzahl", get("zip"));
        rp.add("hausnummer", get("housenumber"));
        rp.add("ort", get("city"));
        rp.add("Telefonnummer", get("phone"));
        rp.add("Mobil", get("mobil"));
        rp.add("e_mail_adresse", get("email"));
        rp.add("Geburtsdatum", get("birthday"));
        rp.add("zaehlernummer", get("electric_meter_number"));
        rp.add("Gewerbe", "0");
        rp.add("Firma", get("company"));
        rp.add("Registergericht", get("company_register_court"));
        rp.add("Registernummer", get("company_register_number"));
        rp.add("zaehlerstand", get("electric_meter_reading"));
        rp.add("rea_name", get("invoice_recipient"));
        rp.add("rea_street", get("invoice_recipient_street"));
        rp.add("rea_house_num", get("invoice_recipient_housenumber"));
        rp.add("rea_post", get("invoice_recipient_zip"));
        rp.add("rea_city", get("invoice_recipient_city"));
        rp.add("iban", get("account_iban"));
        rp.add("Kontoinhaber", get("account_holder"));
        rp.add("sepa_datum", EventListener.apply_filters("field_filter", get("sepa_datum"), "sepa_datum"));
        rp.add("sepa_ermaaechtigung", get("account_sepa"));
        rp.add("agb", get("ordering"));
        rp.add("optin", get("marketing"));
        rp.add("stromlieferung_neu", get("moving_in"));
        rp.add("stromlieferung_einzug", EventListener.apply_filters("field_filter", get("moving_in_date"), "moving_in_date"));
        rp.add("stromlieferung_wechsel", get("distributor_change"));
        rp.add("stromlieferung_wunsch", EventListener.apply_filters("field_filter", get("distributor_change_date"), "distributor_change_date"));
        rp.add("stromlieferung_alt_name", get("distributor_name"));
        rp.add("stromlieferung_alt_kundennummer", get("distributor_number"));
        rp.add("stromlieferung_alt_verbrauch", get("power_consumption"));
        rp.add("timestamp", EventListener.apply_filters("field_filter", get("timestamp"), "timestamp"));
        rp.add("validation", "app");
        rp.add("status", get("status"));

        rp.add("anrede_titel", get("anrede_titel"));
        rp.add("mieter_1_anrede", get("mieter_1_anrede"));
        rp.add("mieter_1_titel", get("mieter_1_titel"));
        rp.add("mieter_1_vorname", get("mieter_1_vorname"));
        rp.add("mieter_1_nachname", get("mieter_1_nachname"));
        rp.add("mieter_1_strasse", get("mieter_1_strasse"));
        rp.add("mieter_1_hnr", get("mieter_1_hnr"));
        rp.add("mieter_1_plz", get("mieter_1_plz"));
        rp.add("mieter_1_ort", get("mieter_1_ort"));
        rp.add("mieter_1_email", get("mieter_1_email"));
        rp.add("mieter_1_tel", get("mieter_1_tel"));
        rp.add("mieter_1_gebdat", get("mieter_1_gebdat"));
        rp.add("vermieter_anrede", get("vermieter_anrede"));
        rp.add("vermieter_vorname", get("vermieter_vorname"));
        rp.add("vermieter_nachname", get("vermieter_nachname"));
        rp.add("vermieter_strasse", get("vermieter_strasse"));
        rp.add("vermieter_hnr", get("vermieter_hnr"));
        rp.add("vermieter_plz", get("vermieter_plz"));
        rp.add("vermiter_ort", get("vermiter_ort"));
        rp.add("whg_miete", get("whg_miete"));
        rp.add("whg_kaution", get("whg_kaution"));
        rp.add("whg_barkaution", get("whg_barkaution"));
        rp.add("whg_temp", get("whg_temp"));
        rp.add("whg_kuendigung", get("whg_kuendigung"));
        rp.add("whg_mietverzug", get("whg_mietverzug"));
        rp.add("whg_schaeden", get("whg_schaeden"));
        rp.add("whg_kaution_no", get("whg_kaution_no"));
        rp.add("whg_kaution_bes_no", get("whg_kaution_bes_no"));
        rp.add("whg_versand", get("whg_versand"));

        rp.add("stromlieferung_strasse", get("stromlieferung_strasse"));
        rp.add("stromlieferung_hausnummer", get("stromlieferung_hausnummer"));
        rp.add("stromlieferung_plz", get("stromlieferung_plz"));
        rp.add("stromlieferung_ort", get("stromlieferung_ort"));

        if (lock == true) {
            setStatusSynced(context);
            rp.add("status", "1");
            update_internal(context, null);

            if (get("hash") != null && get("hash").length() > 0) {
                HttpUtils.put("order/" + get("hash"), rp, AdminHandler.getApiKey(context), OnSyncResponseHandler(context, OnStatusChange));
            } else {
                Log.v(" ccccc", "gggg   "+ rp);
                HttpUtils.post("order/", rp, AdminHandler.getApiKey(context), OnSyncResponseHandler(context, OnStatusChange));
            }
        } else {
            // Save as draft - no mail send yet
            if (get("hash") != null && get("hash").length() > 0) {
                HttpUtils.put("order/draft/" + get("hash"), rp, AdminHandler.getApiKey(context), OnSyncResponseHandler(context, OnStatusChange));
            } else {
                HttpUtils.post("order/draft/", rp, AdminHandler.getApiKey(context), OnSyncResponseHandler(context, OnStatusChange));
            }
        }
    }

    /**
     * Change the ContractModel to be synced. After setting the Status to synced
     * no more changes are doable unless the setStatusUnSynced is called
     *
     * @param context
     */
    public void setStatusSynced(Context context) {
        set("status", "1");
        update_internal(context, null);
    }

    /**
     * Change the ContractModel to be unsynced. This will allow the contract model to be editable again.
     *
     * @param context
     */
    public void setStatusUnsynced(Context context) {
        set("status", "0");
        update_internal(context, null);
    }

    public static String[] fields = {
            "ID",
            "vertragsart",
            "makler_id",
            "xcite_vertrag_id",
            "produkt_id",
            "partner_id_auftrag",
            "vertragsdatum",
            "Anredetext",
            "Name",
            "Vorname",
            "strasse",
            "postleitzahl",
            "hausnummer",
            "ort",
            "Telefonnummer",
            "Mobil",
            "Gewerbe",
            "Firma",
            "Registernummer",
            "Registergericht",
            "e_mail_adresse",
            "Geburtsdatum",
            "zaehlernummer",
            "versorgungstermin",
            "zaehlerstand",
            "iban",
            "bic",
            "Kontoinhaber",
            "sepa_datum",
            "sepa_ermaaechtigung",
            "agb",
            "optin",
            "freigabe_kunde",
            "status",
            "prov_vp",
            "rea_name",
            "rea_street",
            "rea_house_num",
            "rea_post",
            "rea_city",
            "timestamp",
            "hash",
            "stromlieferung_neu",
            "stromlieferung_einzug",
            "stromlieferung_wechsel",
            "stromlieferung_wunsch",
            "stromlieferung_alt_name",
            "stromlieferung_alt_kundennummer",
            "stromlieferung_alt_verbrauch",
            "sync_ready",
            "anrede_titel",
            "mieter_1_titel",
            "mieter_1_anrede",
            "mieter_1_vorname",
            "mieter_1_nachname",
            "mieter_1_strasse",
            "mieter_1_hnr",
            "mieter_1_plz",
            "mieter_1_ort",
            "mieter_1_email",
            "mieter_1_tel",
            "mieter_1_gebdat",
            "vermieter_anrede",
            "vermieter_vorname",
            "vermieter_nachname",
            "vermieter_strasse",
            "vermieter_hnr",
            "vermieter_plz",
            "vermiter_ort",
            "whg_miete",
            "whg_kaution",
            "whg_barkaution",
            "whg_temp",
            "whg_kuendigung",
            "whg_mietverzug",
            "whg_schaeden",
            "whg_kaution_no",
            "whg_kaution_bes_no",
            "whg_versand",
            "stromlieferung_strasse",
            "stromlieferung_hausnummer",
            "stromlieferung_plz",
            "stromlieferung_ort"
    };

    /**
     * Loading an specific contract by the given hash
     */
    public static ContractModel load(Context context, String hash, @Nullable CustomEventListener OnStatusChange) {
        ContractModel CM = null;

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("suewag_tab_vertraege_container", fields, "hash='" + hash + "'", "ID DESC");

        if (cursor == null) {
            return new ContractModel();
        }

        while (!cursor.isAfterLast() && cursor != null) {
            CM = new ContractModel(cursor);
            cursor.moveToNext();
        }

        cursor.close();

        db.close();
        return CM;
    }

    /**
     * Loading an specific contract by the given ID
     */
    public static ContractModel load(Context context, int ID, @Nullable CustomEventListener OnStatusChange) {
        ContractModel CM = null;

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("suewag_tab_vertraege_container", fields, "ID=" + ID, "ID DESC");

        if (cursor == null) {
            return new ContractModel();
        }

        while (!cursor.isAfterLast() && cursor != null) {
            CM = new ContractModel(cursor);
            cursor.moveToNext();
        }

        cursor.close();

        db.close();
        return CM;
    }


    public static ArrayList<ContractModel> load(Context context, boolean sync_ready) {
        ArrayList<ContractModel> Dataset = new ArrayList<ContractModel>();

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("suewag_tab_vertraege_container", fields, "sync_ready=1", "ID DESC");

        while (cursor != null && !cursor.isAfterLast()) {
            ContractModel CM = new ContractModel(cursor);
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
     * Loading an array of Data from the Database.
     *
     * @param context
     * @return
     */
    public static ArrayList<ContractModel> loadWithTarif(Context context) {
        ArrayList<ContractModel> Dataset = new ArrayList<ContractModel>();

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.rawQuery("SELECT suewag_tab_vertraege_container.ID, suewag_tab_vertraege_container.vertragsart,suewag_tab_vertraege_container.makler_id,suewag_tab_vertraege_container.xcite_vertrag_id,suewag_tab_vertraege_container.produkt_id,suewag_tab_vertraege_container.partner_id_auftrag,suewag_tab_vertraege_container.vertragsdatum,suewag_tab_vertraege_container.Anredetext, suewag_tab_vertraege_container.Name, suewag_tab_vertraege_container.Vorname, suewag_tab_vertraege_container.strasse, suewag_tab_vertraege_container.postleitzahl, suewag_tab_vertraege_container.hausnummer, suewag_tab_vertraege_container.ort, suewag_tab_vertraege_container.Telefonnummer, suewag_tab_vertraege_container.Mobil, suewag_tab_vertraege_container.Gewerbe, suewag_tab_vertraege_container.Firma, suewag_tab_vertraege_container.Registernummer,suewag_tab_vertraege_container.Registergericht, suewag_tab_vertraege_container.e_mail_adresse, suewag_tab_vertraege_container.Geburtsdatum, suewag_tab_vertraege_container.zaehlernummer, suewag_tab_vertraege_container.versorgungstermin, suewag_tab_vertraege_container.zaehlerstand, suewag_tab_vertraege_container.iban, suewag_tab_vertraege_container.bic, suewag_tab_vertraege_container.Kontoinhaber, suewag_tab_vertraege_container.sepa_datum, suewag_tab_vertraege_container.sepa_ermaaechtigung, suewag_tab_vertraege_container.agb, suewag_tab_vertraege_container.optin, suewag_tab_vertraege_container.freigabe_kunde, suewag_tab_vertraege_container.status, suewag_tab_vertraege_container.prov_vp, suewag_tab_vertraege_container.rea_name, suewag_tab_vertraege_container.rea_street, suewag_tab_vertraege_container.rea_house_num, suewag_tab_vertraege_container.rea_post,suewag_tab_vertraege_container.rea_city, suewag_tab_vertraege_container.timestamp, suewag_tab_vertraege_container.hash, suewag_tab_vertraege_container.stromlieferung_neu,suewag_tab_vertraege_container.stromlieferung_wechsel, suewag_tab_vertraege_container.stromlieferung_einzug, suewag_tab_vertraege_container.stromlieferung_wunsch, suewag_tab_vertraege_container.stromlieferung_alt_name,suewag_tab_vertraege_container.stromlieferung_alt_kundennummer, suewag_tab_vertraege_container.stromlieferung_alt_verbrauch, suewag_tab_vertraege_container.anrede_titel,suewag_tab_vertraege_container.mieter_1_titel,suewag_tab_vertraege_container.mieter_1_anrede,suewag_tab_vertraege_container.mieter_1_vorname,suewag_tab_vertraege_container.mieter_1_nachname,suewag_tab_vertraege_container.mieter_1_strasse,suewag_tab_vertraege_container.mieter_1_hnr,suewag_tab_vertraege_container.mieter_1_plz,suewag_tab_vertraege_container.mieter_1_ort,suewag_tab_vertraege_container.mieter_1_email,suewag_tab_vertraege_container.mieter_1_tel,suewag_tab_vertraege_container.mieter_1_gebdat,suewag_tab_vertraege_container.vermieter_anrede,suewag_tab_vertraege_container.vermieter_vorname,suewag_tab_vertraege_container.vermieter_nachname,suewag_tab_vertraege_container.vermieter_strasse,suewag_tab_vertraege_container.vermieter_hnr,suewag_tab_vertraege_container.vermieter_plz,suewag_tab_vertraege_container.vermiter_ort,suewag_tab_vertraege_container.whg_miete,suewag_tab_vertraege_container.whg_kaution,suewag_tab_vertraege_container.whg_barkaution,suewag_tab_vertraege_container.whg_temp,suewag_tab_vertraege_container.whg_kuendigung,suewag_tab_vertraege_container.whg_mietverzug,suewag_tab_vertraege_container.whg_schaeden,suewag_tab_vertraege_container.whg_kaution_no,suewag_tab_vertraege_container.whg_kaution_bes_no,suewag_tab_vertraege_container.whg_versand,suewag_tab_vertraege_container.stromlieferung_strasse,suewag_tab_vertraege_container.stromlieferung_hausnummer,suewag_tab_vertraege_container.stromlieferung_plz,suewag_tab_vertraege_container.stromlieferung_ort,suewag_tab_vertraege_container.sync_ready,suewag_tab_produkte.strom_or_gas FROM suewag_tab_vertraege_container LEFT JOIN suewag_tab_produkte ON suewag_tab_vertraege_container.produkt_id=suewag_tab_produkte.ID GROUP BY suewag_tab_vertraege_container.ID, suewag_tab_produkte.strom_or_gas ORDER BY suewag_tab_vertraege_container.ID DESC");

        if (cursor == null) {
            return Dataset;
        }

        cursor.moveToFirst();

        while (cursor != null && !cursor.isAfterLast()) {
            ContractModel CM = new ContractModel(cursor);
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
     * Loading an array of Data from the Database.
     *
     * @param context
     * @return
     */
    public static ArrayList<ContractModel> load(Context context) {
        ArrayList<ContractModel> Dataset = new ArrayList<ContractModel>();

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.fetch("suewag_tab_vertraege_container", fields, "ID DESC");

        while (cursor != null && !cursor.isAfterLast()) {
            ContractModel CM = new ContractModel(cursor);
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
     * Delete all contracts
     *
     * @param {context} context
     * @param {integer} day
     * @return int
     */
    public static int delete_contracts_all(Context context) {
        SQLHelper db = new SQLHelper(context);
        int ret = db.delete("suewag_tab_vertraege_container", "ID!=''");
        db.close();

        delete_images_all(context);
        return ret;
    }

    /**
     * Delete all contract model from the database that are synced
     *
     * @param {context} context
     * @param {integer} day
     * @return int
     */
    public static int delete_contracts_synced(Context context) {
        SQLHelper db = new SQLHelper(context);
        int ret = db.delete("suewag_tab_vertraege_container", "hash != ''");
        db.close();

        delete_images_without_contract(context);
        return ret;
    }

    /**
     * Delete all contract model from the database that are older than x days
     *
     * @param {context} context
     * @param {integer} day
     * @return int
     */
    public static int delete_contracts_older_than_days(Context context, int day) {
        if (day == 0) {
            return -1;
        }

        SQLHelper db = new SQLHelper(context);
        int ret = db.delete("suewag_tab_vertraege_container", "timestamp <= datetime('now','-" + day + " days') AND hash != ''");
        db.close();

        delete_images_without_contract(context);
        return ret;
    }

    /**
     * Delete all images
     */
    public static int delete_images_all(Context context) {
        SQLHelper db = new SQLHelper(context);
        int res = db.delete("suewag_tab_vertraege_container_images", "id!=''");

        db.close();
        Log.v("Bilder gelöscht", res + " bilder");

        return res;
    }

    /**
     * Delete all images not used anymore
     *
     * @return
     */
    public static int delete_images_without_contract(Context context) {
        SQLHelper db = new SQLHelper(context);
        db.query("DELETE FROM suewag_tab_vertraege_container_images WHERE id in (SELECT i.id FROM suewag_tab_vertraege_container_images as i LEFT JOIN suewag_tab_vertraege_container as v ON i.contract_hash = v.hash WHERE v.hash ISNULL AND i.contract_hash != '')");

        Cursor cursor = db.getWritableDatabase().rawQuery("select changes() 'affected_rows'", null);
        cursor.moveToFirst();
        int res = cursor.getInt(cursor.getColumnIndex("affected_rows"));
        cursor.close();

        db.close();
        Log.v("Bilder gelöscht", res + " bilder");

        return res;
    }

    /**
     * Delete an contract model from the database and returns true if it worked.
     *
     * @param context
     * @param ID
     * @param OnStatusChange
     * @return
     */
    public static boolean delete(Context context, int ID, @Nullable CustomEventListener OnStatusChange) {
        SQLHelper db = new SQLHelper(context);
        boolean ret = db.delete("suewag_tab_vertraege_container", "ID=" + ID) > 0;
        db.close();

        delete_images_without_contract(context);
        return ret;
    }

    /**
     * Insert if new entry
     *
     * @param context
     * @param OnStatusChange
     * @return boolean
     */
    private boolean insert_downloaded(Context context, @Nullable CustomEventListener OnStatusChange) {
        SQLHelper db = new SQLHelper(context);

        SettingsModel SM = SettingsModel.load(context, "makler_id", null);
        String makler_id = "-1";
        if (SM != null) {
            makler_id = SM.get("meta_value");
        }
        // new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
        String Statement = "INSERT INTO `suewag_tab_vertraege_container` " +
                "(" +
                "`vertragsart`," +
                "`makler_id`, " +
                "`partner_id_auftrag`, " +
                "`xcite_vertrag_id`, " +
                "`produkt_id`, " +
                "`vertragsdatum`, " +
                "`Anredetext`, " +
                "`Name`, " +
                "`Vorname`, " +
                "`strasse`, " +
                "`postleitzahl`, " +
                "`hausnummer`, " +
                "`ort`, " +
                "`Telefonnummer`, " +
                "`Mobil`, " +
                "`Gewerbe`, " +
                "`Firma`, " +
                "`Registernummer`, " +
                "`Registergericht`, " +
                "`e_mail_adresse`, " +
                "`Geburtsdatum`, " +
                "`zaehlernummer`," +
                "`zaehlerstand`, " +
                "`iban`," +
                "`Kontoinhaber`, " +
                "`sepa_datum`, " +
                "`sepa_ermaaechtigung`, " +
                "`agb`, " +
                "`optin`, " +
                "`freigabe_kunde`, " +
                "`rea_name`, " +
                "`rea_street`, " +
                "`rea_house_num`, " +
                "`rea_post`, " +
                "`rea_city`, " +
                "`timestamp`, " +
                "`stromlieferung_neu`, " +
                "`stromlieferung_wechsel`, " +
                "`stromlieferung_einzug`, " +
                "`stromlieferung_wunsch`, " +
                "`stromlieferung_alt_name`, " +
                "`stromlieferung_alt_kundennummer`, " +
                "`stromlieferung_alt_verbrauch`, " +
                "`status`, " +
                "`sync_ready`, " +
                "`hash`," +
                "`anrede_titel`," +
                "`mieter_1_anrede`," +
                "`mieter_1_titel`," +
                "`mieter_1_vorname`," +
                "`mieter_1_nachname`," +
                "`mieter_1_strasse`," +
                "`mieter_1_hnr`," +
                "`mieter_1_plz`," +
                "`mieter_1_ort`," +
                "`mieter_1_email`," +
                "`mieter_1_tel`," +
                "`mieter_1_gebdat`," +
                "`vermieter_anrede`," +
                "`vermieter_vorname`," +
                "`vermieter_nachname`," +
                "`vermieter_strasse`," +
                "`vermieter_hnr`," +
                "`vermieter_plz`," +
                "`vermiter_ort`," +
                "`whg_miete`," +
                "`whg_kaution`," +
                "`whg_barkaution`," +
                "`whg_temp`," +
                "`whg_kuendigung`," +
                "`whg_mietverzug`," +
                "`whg_schaeden`," +
                "`whg_kaution_no`," +
                "`whg_kaution_bes_no`," +
                "`whg_versand`," +
                "`stromlieferung_strasse`,"+
                "`stromlieferung_hausnummer`,"+
                "`stromlieferung_plz`,"+
                "`stromlieferung_ort`"+
                ") VALUES (" +
                "'" + get("vertragsart") + "'," +
                "'" + makler_id + "'," +
                "-1," +
                "-1," +
                "'" + get("produkt_id") + "'," +
                "-1," +
                "'" + get("salutation") + "'," +
                "'" + get("lastname") + "'," +
                "'" + get("firstname") + "'," +
                "'" + get("street") + "'," +
                "'" + get("zip") + "'," +
                "'" + get("housenumber") + "'," +
                "'" + get("city") + "'," +
                "'" + get("phone") + "'," +
                "'" + get("mobil") + "'," +
                "1," +
                "'" + get("company") + "'," +
                "'" + get("company_register_number") + "'," +
                "'" + get("company_register_court") + "'," +
                "'" + get("email") + "'," +
                //"'2018-01-01'," +
                "'" + get("birthday") + "'," +
                "'" + get("electric_meter_number") + "'," +
                "'" + get("electric_meter_reading") + "'," +
                "'" + get("account_iban") + "'," +
                "'" + get("account_holder") + "'," +
                "'" + EventListener.apply_filters("field_filter", get("sepa_datum"), "sepa_datum") + "'," +
                "'" + get("account_sepa") + "'," +
                "'" + get("ordering") + "'," +
                "'" + get("marketing") + "'," +
                "1," +
                "'" + get("invoice_recipient") + "'," +
                "'" + get("invoice_recipient_street") + "'," +
                "'" + get("invoice_recipient_housenumber") + "'," +
                "'" + get("invoice_recipient_zip") + "'," +
                "'" + get("invoice_recipient_city") + "'," +
                "'" + EventListener.apply_filters("field_filter", get("timestamp"), "timestamp") + "'," +
                "'" + get("moving_in") + "'," +
                "'" + get("distributor_change") + "'," +
                "'" + get("moving_in_date") + "'," +
                "'" + get("distributor_change_date") + "'," +
                "'" + get("distributor_name") + "'," +
                "'" + get("distributor_number") + "'," +
                "'" + get("power_consumption") + "'," +
                "'" + EventListener.apply_filters("field_filter", get("status"), "status") + "'," +
                "'" + get("sync_ready") + "'," +
                "'" + get("hash") + "', " +
                "'" + get("anrede_titel") + "'," +
                "'" + get("mieter_1_anrede") + "'," +
                "'" + get("mieter_1_titel") + "'," +
                "'" + get("mieter_1_vorname") + "'," +
                "'" + get("mieter_1_nachname") + "'," +
                "'" + get("mieter_1_strasse") + "'," +
                "'" + get("mieter_1_hnr") + "'," +
                "'" + get("mieter_1_plz") + "'," +
                "'" + get("mieter_1_ort") + "'," +
                "'" + get("mieter_1_email") + "'," +
                "'" + get("mieter_1_tel") + "'," +
                "'" + get("mieter_1_gebdat") + "'," +
                "'" + get("vermieter_anrede") + "'," +
                "'" + get("vermieter_vorname") + "'," +
                "'" + get("vermieter_nachname") + "'," +
                "'" + get("vermieter_strasse") + "'," +
                "'" + get("vermieter_hnr") + "'," +
                "'" + get("vermieter_plz") + "'," +
                "'" + get("vermiter_ort") + "'," +
                "'" + get("whg_miete") + "'," +
                "'" + get("whg_kaution") + "'," +
                "'" + get("whg_barkaution") + "'," +
                "'" + get("whg_temp") + "'," +
                "'" + get("whg_kuendigung") + "'," +
                "'" + get("whg_mietverzug") + "'," +
                "'" + get("whg_schaeden") + "'," +
                "'" + get("whg_kaution_no") + "'," +
                "'" + get("whg_kaution_bes_no") + "'," +
                "'" + get("whg_versand") + "'," +
                "'" + get("stromlieferung_strasse") + "'," +
                "'" + get("stromlieferung_hausnummer") + "'," +
                "'" + get("stromlieferung_plz") + "'," +
                "'" + get("stromlieferung_ort") + "'" +
                ")";

        db.query(Statement);
        int last_insert_id = db.last_insert_id("suewag_tab_vertraege_container");

        if (last_insert_id == 0) {
            db.close();
            return false;
        }

        set("ID", last_insert_id);
        ContractModel CM = ContractModel.load(context, last_insert_id, null);
        if (CM == null) {
            db.close();
            return false;
        }

        db.close();
        return this.compare(CM);
    }

    private boolean insert(Context context, @Nullable CustomEventListener OnStatusChange) {
        return insert(context, OnStatusChange, false);
    }

    /**
     * Insert if new entry
     *
     * @param context
     * @param OnStatusChange
     * @return boolean
     */
    private boolean insert(Context context, @Nullable CustomEventListener OnStatusChange, boolean isDuplicat) {
        SQLHelper db = new SQLHelper(context);

        SettingsModel SM = SettingsModel.load(context, "makler_id", null);
        String makler_id = "-1";
        if (SM != null) {
            makler_id = SM.get("meta_value");
        }

        /**
         * Change contract settings for duplicate
         */
        if (isDuplicat) {
            set("sync_ready", 0);
            set("status", 0);
        }

        // new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
        String Statement = "INSERT INTO `suewag_tab_vertraege_container` " +
                "(" +
                "`vertragsart`," +
                "`makler_id`," +
                "`xcite_vertrag_id`," +
                "`produkt_id`," +
                "`partner_id_auftrag`," +
                "`vertragsdatum`," +
                "`Anredetext`," +
                "`Name`," +
                "`Vorname`," +
                "`strasse`," +
                "`postleitzahl`," +
                "`hausnummer`," +
                "`ort`," +
                "`Telefonnummer`," +
                "`Mobil`," +
                "`Gewerbe`," +
                "`Firma`," +
                "`Registergericht`," +
                "`Registernummer`," +
                "`e_mail_adresse`," +
                "`Geburtsdatum`," +
                "`zaehlernummer`," +
                "`zaehlerstand`," +
                "`iban`," +
                "`Kontoinhaber`," +
                "`sepa_datum`," +
                "`sepa_ermaaechtigung`," +
                "`agb`," +
                "`optin`," +
                "`freigabe_kunde`," +
                "`rea_name`," +
                "`rea_street`," +
                "`rea_house_num`," +
                "`rea_post`," +
                "`rea_city`," +
                "`timestamp`," +
                "`stromlieferung_neu`," +
                "`stromlieferung_wechsel`," +
                "`stromlieferung_einzug`," +
                "`stromlieferung_wunsch`," +
                "`stromlieferung_alt_name`," +
                "`stromlieferung_alt_kundennummer`," +
                "`stromlieferung_alt_verbrauch`," +
                "`status`," +
                "`sync_ready`," +
                "`hash`," +
                "`anrede_titel`," +
                "`mieter_1_anrede`," +
                "`mieter_1_titel`," +
                "`mieter_1_vorname`," +
                "`mieter_1_nachname`," +
                "`mieter_1_strasse`," +
                "`mieter_1_hnr`," +
                "`mieter_1_plz`," +
                "`mieter_1_ort`," +
                "`mieter_1_email`," +
                "`mieter_1_tel`," +
                "`mieter_1_gebdat`," +
                "`vermieter_anrede`," +
                "`vermieter_vorname`," +
                "`vermieter_nachname`," +
                "`vermieter_strasse`," +
                "`vermieter_hnr`," +
                "`vermieter_plz`," +
                "`vermiter_ort`," +
                "`whg_miete`," +
                "`whg_kaution`," +
                "`whg_barkaution`," +
                "`whg_temp`," +
                "`whg_kuendigung`," +
                "`whg_mietverzug`," +
                "`whg_schaeden`," +
                "`whg_kaution_no`," +
                "`whg_kaution_bes_no`," +
                "`whg_versand`," +
                "`stromlieferung_strasse`," +
                "`stromlieferung_hausnummer`," +
                "`stromlieferung_plz`," +
                "`stromlieferung_ort`" +
                ") VALUES (" +
                "'" + get("vertragsart") + "'," +
                "'" + makler_id + "'," +
                "-1," +
                "'" + get("produkt_id") + "'," +
                "-1," +
                "-1," +
                "'" + get("salutation") + "'," +
                "'" + get("lastname") + "'," +
                "'" + get("firstname") + "'," +
                "'" + get("street") + "'," +
                "'" + get("zip") + "'," +
                "'" + get("housenumber") + "'," +
                "'" + get("city") + "'," +
                "'" + get("phone") + "'," +
                "'" + get("mobil") + "'," +
                "1," +
                "'" + get("company") + "'," +
                "'" + get("company_register_number") + "'," +
                "'" + get("company_register_court") + "'," +
                "'" + get("email") + "'," +
                //"'2018-01-01'," +
                "'" + get("birthday") + "'," +
                "'" + get("electric_meter_number") + "'," +
                "'" + get("electric_meter_reading") + "'," +
                "'" + get("account_iban") + "'," +
                "'" + get("account_holder") + "'," +
                "'" + EventListener.apply_filters("field_filter", get("sepa_datum"), "sepa_datum") + "'," +
                "'" + get("account_sepa") + "'," +
                "'" + get("ordering") + "'," +
                "'" + get("marketing") + "'," +
                "1," +
                "'" + get("invoice_recipient") + "'," +
                "'" + get("invoice_recipient_street") + "'," +
                "'" + get("invoice_recipient_housenumber") + "'," +
                "'" + get("invoice_recipient_zip") + "'," +
                "'" + get("invoice_recipient_city") + "'," +
                "'" + EventListener.apply_filters("field_filter", get("timestamp"), "timestamp") + "'," +
                "'" + get("moving_in") + "'," +
                "'" + get("distributor_change") + "'," +
                "'" + get("moving_in_date") + "'," +
                "'" + get("distributor_change_date") + "'," +
                "'" + get("distributor_name") + "'," +
                "'" + get("distributor_number") + "'," +
                "'" + get("power_consumption") + "'," +
                "'" + EventListener.apply_filters("field_filter", get("status"), "status") + "'," +
                "'" + get("sync_ready") + "'," +
                "''," +
                "'" + get("anrede_titel") + "'," +
                "'" + get("mieter_1_anrede") + "'," +
                "'" + get("mieter_1_titel") + "'," +
                "'" + get("mieter_1_vorname") + "'," +
                "'" + get("mieter_1_nachname") + "'," +
                "'" + get("mieter_1_strasse") + "'," +
                "'" + get("mieter_1_hnr") + "'," +
                "'" + get("mieter_1_plz") + "'," +
                "'" + get("mieter_1_ort") + "'," +
                "'" + get("mieter_1_email") + "'," +
                "'" + get("mieter_1_tel") + "'," +
                "'" + get("mieter_1_gebdat") + "'," +
                "'" + get("vermieter_anrede") + "'," +
                "'" + get("vermieter_vorname") + "'," +
                "'" + get("vermieter_nachname") + "'," +
                "'" + get("vermieter_strasse") + "'," +
                "'" + get("vermieter_hnr") + "'," +
                "'" + get("vermieter_plz") + "'," +
                "'" + get("vermiter_ort") + "'," +
                "'" + get("whg_miete") + "'," +
                "'" + get("whg_kaution") + "'," +
                "'" + get("whg_barkaution") + "'," +
                "'" + get("whg_temp") + "'," +
                "'" + get("whg_kuendigung") + "'," +
                "'" + get("whg_mietverzug") + "'," +
                "'" + get("whg_schaeden") + "'," +
                "'" + get("whg_kaution_no") + "'," +
                "'" + get("whg_kaution_bes_no") + "'," +
                "'" + get("whg_versand") + "'," +
                "'" + get("stromlieferung_strasse") + "'," +
                "'" + get("stromlieferung_hausnummer") + "'," +
                "'" + get("stromlieferung_plz") + "'," +
                "'" + get("stromlieferung_ort") + "'" +
                ")";

        db.query(Statement);

        int last_insert_id = db.last_insert_id("suewag_tab_vertraege_container");

        if (last_insert_id == 0) {
            db.close();
            return false;
        }

        set("ID", last_insert_id);
        ContractModel CM = ContractModel.load(context, last_insert_id, null);
        if (CM == null) {
            db.close();
            return false;
        }

        db.close();
        return this.compare(CM);
    }

    /**
     * Compares to Contract Models
     *
     * @param CM
     * @return
     */
    private boolean compare(ContractModel CM) {
        if (CM.get("firstname").equals(this.get("firstname")) && CM.get("lastname").equals(this.get("lastname"))) {
            return true;
        }
        return false;
    }

    /**
     * Compare Timestamp
     */
    public int isNewerThan(ContractModel CM) {
        SimpleDateFormat local = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat live = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date strDateLocal = local.parse((String) get("timestamp"));
            Date strDateLive = live.parse((String) CM.get("timestamp"));

            if (strDateLocal.getTime() < strDateLive.getTime()) {
                // do the update
                return -1;
            }

            if (strDateLocal.getTime() > strDateLive.getTime()) {
                return 1;
            }
        } catch (ParseException e) {
            Log.v("ParseException", e.getMessage());
        }

        return 0;
    }

    /**
     * Used
     *
     * @param context
     * @param OnStatusChange
     * @return
     */
    private int update_internal(Context context, @Nullable CustomEventListener OnStatusChange) {
        return update(context, false, OnStatusChange);
    }

    private int update(Context context, @Nullable CustomEventListener OnStatusChange) {
        return update(context, true, OnStatusChange);
    }

    /**
     * Update if existing already
     *
     * @param context
     * @param OnStatusChange
     * @return int 1 if successfully updated
     */
    private int update(Context context, boolean updateTimestamp, @Nullable CustomEventListener OnStatusChange) {
        SQLHelper db = new SQLHelper(context);

        SettingsModel SM = SettingsModel.load(context, "makler_id", null);
        String makler_id = "-1";
        if (SM != null) {
            makler_id = SM.get("meta_value");
        }

        ContentValues cv = new ContentValues();
        cv.put("makler_id", makler_id);
        cv.put("partner_id_auftrag", "-1");
        cv.put("xcite_vertrag_id", "-1");
        cv.put("produkt_id", get("produkt_id"));
        cv.put("vertragsdatum", get("vertragsdatum"));
        cv.put("Anredetext", get("salutation"));
        cv.put("Name", get("lastname"));
        cv.put("Vorname", get("firstname"));
        cv.put("strasse", get("street"));
        cv.put("postleitzahl", get("zip"));
        cv.put("hausnummer", get("housenumber"));
        cv.put("ort", get("city"));
        cv.put("Telefonnummer", get("phone"));
        cv.put("Mobil", get("mobil"));
        cv.put("Gewerbe", get("company_active"));
        cv.put("Firma", get("company"));
        cv.put("Registernummer", get("company_register_number"));
        cv.put("Registergericht", get("company_register_court"));
        cv.put("e_mail_adresse", get("email"));
        cv.put("Geburtsdatum", get("birthday"));
        cv.put("zaehlernummer", get("electric_meter_number"));
        cv.put("zaehlerstand", get("electric_meter_reading"));
        cv.put("iban", get("account_iban"));
        cv.put("Kontoinhaber", get("account_holder"));
        cv.put("sepa_datum", get("account_sepa_date"));
        cv.put("sepa_ermaaechtigung", get("account_sepa"));
        cv.put("agb", get("ordering"));
        cv.put("optin", get("marketing"));
        cv.put("freigabe_kunde", "0");
        cv.put("rea_name", get("invoice_recipient"));
        cv.put("rea_street", get("invoice_recipient_street"));
        cv.put("rea_house_num", get("invoice_recipient_housenumber"));
        cv.put("rea_post", get("invoice_recipient_zip"));
        cv.put("rea_city", get("invoice_recipient_city"));
        cv.put("stromlieferung_neu", get("moving_in"));
        cv.put("stromlieferung_wechsel", get("distributor_change"));
        cv.put("stromlieferung_einzug", get("moving_in_date"));
        cv.put("stromlieferung_wunsch", get("distributor_change_date"));
        cv.put("stromlieferung_alt_name", get("distributor_name"));
        cv.put("stromlieferung_alt_kundennummer", get("distributor_number"));
        cv.put("stromlieferung_alt_verbrauch", get("power_consumption"));
        cv.put("hash", get("hash"));
        cv.put("sync_ready", get("sync_ready"));
        cv.put("vertragsart", get("vertragsart"));

        cv.put("anrede_titel", get("anrede_titel"));
        cv.put("mieter_1_anrede", get("mieter_1_anrede"));
        cv.put("mieter_1_titel", get("mieter_1_titel"));
        cv.put("mieter_1_vorname", get("mieter_1_vorname"));
        cv.put("mieter_1_nachname", get("mieter_1_nachname"));
        cv.put("mieter_1_strasse", get("mieter_1_strasse"));
        cv.put("mieter_1_hnr", get("mieter_1_hnr"));
        cv.put("mieter_1_plz", get("mieter_1_plz"));
        cv.put("mieter_1_ort", get("mieter_1_ort"));
        cv.put("mieter_1_email", get("mieter_1_email"));
        cv.put("mieter_1_tel", get("mieter_1_tel"));
        cv.put("mieter_1_gebdat", get("mieter_1_gebdat"));
        cv.put("vermieter_anrede", get("vermieter_anrede"));
        cv.put("vermieter_vorname", get("vermieter_vorname"));
        cv.put("vermieter_nachname", get("vermieter_nachname"));
        cv.put("vermieter_strasse", get("vermieter_strasse"));
        cv.put("vermieter_hnr", get("vermieter_hnr"));
        cv.put("vermieter_plz", get("vermieter_plz"));
        cv.put("vermiter_ort", get("vermiter_ort"));
        cv.put("whg_miete", get("whg_miete"));
        cv.put("whg_kaution", get("whg_kaution"));
        cv.put("whg_barkaution", get("whg_barkaution"));
        cv.put("whg_temp", get("whg_temp"));
        cv.put("whg_kuendigung", get("whg_kuendigung"));
        cv.put("whg_mietverzug", get("whg_mietverzug"));
        cv.put("whg_schaeden", get("whg_schaeden"));
        cv.put("whg_kaution_no", get("whg_kaution_no"));
        cv.put("whg_kaution_bes_no", get("whg_kaution_bes_no"));
        cv.put("whg_versand", get("whg_versand"));

        cv.put("stromlieferung_strasse", get("stromlieferung_strasse"));
        cv.put("stromlieferung_hausnummer", get("stromlieferung_hausnummer"));
        cv.put("stromlieferung_plz", get("stromlieferung_plz"));
        cv.put("stromlieferung_ort", get("stromlieferung_ort"));

        if (updateTimestamp) {
            cv.put("timestamp", EventListener.apply_filters("field_filter", "", "timestamp"));
        } else {
            cv.put("timestamp", EventListener.apply_filters("field_filter", get("timestamp"), "timestamp"));
        }

        cv.put("status", EventListener.apply_filters("field_filter", get("status"), "status"));

        int ret = db.getmWriteableDatabase().update("suewag_tab_vertraege_container", cv, "ID=" + get("ID"), null);
        db.close();
        return ret;
    }

    /**
     * Duplicate the content of the contract model and insert into the database.
     *
     * @param context
     * @return
     */
    public boolean duplicate(Context context) {
        if (insert(context, null, true)) {
            return true;
        }

        return true;
    }

    /**
     * Interface to support old functions.
     *
     * @param context
     * @param OnStatusChange
     * @return
     */
    public boolean save(Context context, @Nullable CustomEventListener OnStatusChange) {
        return save(context, OnStatusChange, false);
    }

    /**
     * Saving the Data to the Database
     *
     * @param context
     * @param OnStatusChange
     * @param downloaded     Defining if the contract is saved after downloading. If it is saved after downloading we need to save
     *                       the hash too.
     * @return boolean
     */
    public boolean save(Context context, @Nullable CustomEventListener OnStatusChange, boolean downloaded) {
        /**
         * Update the Tarif before insert / update
         */
        TarifModel TM = TarifModel.load(context, this.get("zip"), this.get("vertragsart"), null);
        if (TM != null) {
            this.add("produkt_id", TM.get("ID"));
        }


        if (get("ID").length() > 0) {
            if (update(context, OnStatusChange) == 1) {
                if (null != OnStatusChange) {
                    OnStatusChange.sendMessage("Daten erfolgreich aktualisiert", 0, 200);
                    return true;
                }
            } else {
                if (null != OnStatusChange) {
                    OnStatusChange.sendMessage("Daten konnte nicht aktualisiert werden", 0, 500);
                    return false;
                }
            }
        } else {
            if (downloaded == true) {
                if (insert_downloaded(context, OnStatusChange)) {
                    if (null != OnStatusChange) {
                        OnStatusChange.sendMessage("Daten erfolgreich gespeichert", 0, 200);
                        return true;
                    }
                } else {
                    if (null != OnStatusChange) {
                        OnStatusChange.sendMessage("Daten konnten nicht gespeichert werden", 0, 200);
                        return false;
                    }
                }
            } else {
                if (insert(context, OnStatusChange)) {
                    if (null != OnStatusChange) {
                        OnStatusChange.sendMessage("Daten erfolgreich gespeichert", 0, 200);
                        return true;
                    }
                } else {
                    if (null != OnStatusChange) {
                        OnStatusChange.sendMessage("Daten konnten nicht gespeichert werden", 0, 200);
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
