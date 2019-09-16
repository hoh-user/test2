package forge12.x_citeapi.Model;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.SQLHelper;

public class TarifModel extends AbstractModel {

    /**
     * Loading an specific contract by the given ID
     */
    public static TarifModel load(Context context, String zip, String type, @Nullable CustomEventListener OnStatusChange) {
        TarifModel TM = new TarifModel();

        SQLHelper db = new SQLHelper(context);

        if (type.equals("Strom")) {
            type = "10";
        } else if (type.equals("Gas")) {
            type = "11";
        } else {
            type = "19";
        }

        Cursor cursor;
        if (!type.equals("19")) {
            //String qry = "SELECT plz.Postleitzahl, suewag_tab_produkte_gruppen.name_gruppe, suewag_tab_produkte.strom_or_gas, suewag_tab_produkte.Tarifname, suewag_tab_produkte.grundgebuehr, suewag_tab_produkte.grundgebuehr2, suewag_tab_produkte.kwhpreis, suewag_tab_produkte.tarifschluessel, suewag_tab_produkte.Preisgarantie, suewag_tab_produkte.Laufzeit, suewag_tab_produkte.Verlaengerung, suewag_tab_produkte.Kuendigungsfrist FROM (((suewag_tab_produkte_gruppen INNER JOIN suewag_tab_produkt_to_gruppe ON suewag_tab_produkte_gruppen.ID = suewag_tab_produkt_to_gruppe.id_gruppe) INNER JOIN suewag_tab_produkte ON suewag_tab_produkt_to_gruppe.id_produkt = suewag_tab_produkte.ID) INNER JOIN suewag_tab_produkt_plz ON suewag_tab_produkte.ID = suewag_tab_produkt_plz.id_produkt) INNER JOIN plz ON suewag_tab_produkt_plz.id_plz = plz.ID WHERE (suewag_tab_produkt_to_gruppe.id_gruppe='" + type + "') AND plz.Postleitzahl='" + zip + "'";
            cursor = db.rawQuery("SELECT suewag_tab_produkte.ID, plz.Postleitzahl, suewag_tab_produkte_gruppen.name_gruppe, suewag_tab_produkte.strom_or_gas, suewag_tab_produkte.Tarifname, suewag_tab_produkte.grundgebuehr, suewag_tab_produkte.grundgebuehr2, suewag_tab_produkte.kwhpreis, suewag_tab_produkte.tarifschluessel, suewag_tab_produkte.Preisgarantie, suewag_tab_produkte.Laufzeit, suewag_tab_produkte.Verlaengerung, suewag_tab_produkte.Kuendigungsfrist FROM (((suewag_tab_produkte_gruppen INNER JOIN suewag_tab_produkt_to_gruppe ON suewag_tab_produkte_gruppen.ID = suewag_tab_produkt_to_gruppe.id_gruppe) INNER JOIN suewag_tab_produkte ON suewag_tab_produkt_to_gruppe.id_produkt = suewag_tab_produkte.ID) INNER JOIN suewag_tab_produkt_plz ON suewag_tab_produkte.ID = suewag_tab_produkt_plz.id_produkt) INNER JOIN plz ON suewag_tab_produkt_plz.id_plz = plz.ID WHERE (suewag_tab_produkt_to_gruppe.id_gruppe='" + type + "') AND plz.Postleitzahl='" + zip + "'");
        } else {
            cursor = db.rawQuery("SELECT suewag_tab_produkte.ID, suewag_tab_produkte_gruppen.name_gruppe, suewag_tab_produkte.strom_or_gas, suewag_tab_produkte.Tarifname, suewag_tab_produkte.grundgebuehr, suewag_tab_produkte.grundgebuehr2, suewag_tab_produkte.kwhpreis, suewag_tab_produkte.tarifschluessel, suewag_tab_produkte.Preisgarantie, suewag_tab_produkte.Laufzeit, suewag_tab_produkte.Verlaengerung, suewag_tab_produkte.Kuendigungsfrist FROM (	( ( suewag_tab_produkte_gruppen INNER JOIN suewag_tab_produkt_to_gruppe ON suewag_tab_produkte_gruppen.ID = suewag_tab_produkt_to_gruppe.id_gruppe	) INNER JOIN suewag_tab_produkte ON suewag_tab_produkt_to_gruppe.id_produkt = suewag_tab_produkte.ID ) ) WHERE (suewag_tab_produkt_to_gruppe.id_gruppe='19')");
        }

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();

        while (!cursor.isAfterLast() && cursor != null) {
            TM.add("ID",cursor.getString(cursor.getColumnIndex("ID")));
            if(cursor.getColumnIndex("Postleitzahl") != -1) {
                TM.add("Postleitzahl", cursor.getString(cursor.getColumnIndex("Postleitzahl")));
            }else{
                TM.add("Postleitzahl", zip);
            }
            TM.add("name_gruppe", cursor.getString(cursor.getColumnIndex("name_gruppe")));
            TM.add("Tarifname", cursor.getString(cursor.getColumnIndex("Tarifname")));
            TM.add("grundgebuehr", cursor.getString(cursor.getColumnIndex("grundgebuehr")));
            TM.add("grundgebuehr2", cursor.getString(cursor.getColumnIndex("grundgebuehr2")));
            TM.add("kwhpreis", cursor.getString(cursor.getColumnIndex("kwhpreis")));
            TM.add("tarifschluessel", cursor.getString(cursor.getColumnIndex("tarifschluessel")));
            TM.add("Preisgarantie", cursor.getString(cursor.getColumnIndex("Preisgarantie")));
            TM.add("Laufzeit", cursor.getString(cursor.getColumnIndex("Laufzeit")));
            TM.add("Verlaengerung", cursor.getString(cursor.getColumnIndex("Verlaengerung")));
            TM.add("Kuendigungsfrist", cursor.getString(cursor.getColumnIndex("Kuendigungsfrist")));
            TM.add("strom_or_gas", cursor.getString(cursor.getColumnIndex("strom_or_gas")));

            cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return TM;
    }

    /**
     * @param context
     * @param product_id
     * @param zip
     * @param type
     * @param OnStatusChange
     * @return
     */
    public static TarifModel loadTarif(Context context, String product_id, String zip, String type, @Nullable CustomEventListener OnStatusChange){
        TarifModel TM = new TarifModel();

        SQLHelper db = new SQLHelper(context);

        if (type.equals("Strom")) {
            type = "10";
        } else if (type.equals("Gas")) {
            type = "11";
        } else {
            type = "19";
        }

        Cursor cursor;
        if (!type.equals("19")) {
            //String qry = "SELECT plz.Postleitzahl, suewag_tab_produkte_gruppen.name_gruppe, suewag_tab_produkte.strom_or_gas, suewag_tab_produkte.Tarifname, suewag_tab_produkte.grundgebuehr, suewag_tab_produkte.grundgebuehr2, suewag_tab_produkte.kwhpreis, suewag_tab_produkte.tarifschluessel, suewag_tab_produkte.Preisgarantie, suewag_tab_produkte.Laufzeit, suewag_tab_produkte.Verlaengerung, suewag_tab_produkte.Kuendigungsfrist FROM (((suewag_tab_produkte_gruppen INNER JOIN suewag_tab_produkt_to_gruppe ON suewag_tab_produkte_gruppen.ID = suewag_tab_produkt_to_gruppe.id_gruppe) INNER JOIN suewag_tab_produkte ON suewag_tab_produkt_to_gruppe.id_produkt = suewag_tab_produkte.ID) INNER JOIN suewag_tab_produkt_plz ON suewag_tab_produkte.ID = suewag_tab_produkt_plz.id_produkt) INNER JOIN plz ON suewag_tab_produkt_plz.id_plz = plz.ID WHERE (suewag_tab_produkt_to_gruppe.id_gruppe='" + type + "') AND plz.Postleitzahl='" + zip + "'";
            cursor = db.rawQuery("SELECT suewag_tab_produkte.ID, plz.Postleitzahl, suewag_tab_produkte_gruppen.name_gruppe, suewag_tab_produkte.strom_or_gas, suewag_tab_produkte.Tarifname, suewag_tab_produkte.grundgebuehr, suewag_tab_produkte.grundgebuehr2, suewag_tab_produkte.kwhpreis, suewag_tab_produkte.tarifschluessel, suewag_tab_produkte.Preisgarantie, suewag_tab_produkte.Laufzeit, suewag_tab_produkte.Verlaengerung, suewag_tab_produkte.Kuendigungsfrist FROM (((suewag_tab_produkte_gruppen INNER JOIN suewag_tab_produkt_to_gruppe ON suewag_tab_produkte_gruppen.ID = suewag_tab_produkt_to_gruppe.id_gruppe) INNER JOIN suewag_tab_produkte ON suewag_tab_produkt_to_gruppe.id_produkt = suewag_tab_produkte.ID) INNER JOIN suewag_tab_produkt_plz ON suewag_tab_produkte.ID = suewag_tab_produkt_plz.id_produkt) INNER JOIN plz ON suewag_tab_produkt_plz.id_plz = plz.ID WHERE (suewag_tab_produkt_to_gruppe.id_gruppe='" + type + "') AND suewag_tab_produkte.ID='" + product_id + "' AND plz.Postleitzahl='"+zip+"'");
        } else {
            cursor = db.rawQuery("SELECT suewag_tab_produkte.ID, suewag_tab_produkte_gruppen.name_gruppe, suewag_tab_produkte.strom_or_gas, suewag_tab_produkte.Tarifname, suewag_tab_produkte.grundgebuehr, suewag_tab_produkte.grundgebuehr2, suewag_tab_produkte.kwhpreis, suewag_tab_produkte.tarifschluessel, suewag_tab_produkte.Preisgarantie, suewag_tab_produkte.Laufzeit, suewag_tab_produkte.Verlaengerung, suewag_tab_produkte.Kuendigungsfrist FROM (	( ( suewag_tab_produkte_gruppen INNER JOIN suewag_tab_produkt_to_gruppe ON suewag_tab_produkte_gruppen.ID = suewag_tab_produkt_to_gruppe.id_gruppe	) INNER JOIN suewag_tab_produkte ON suewag_tab_produkt_to_gruppe.id_produkt = suewag_tab_produkte.ID ) ) WHERE (suewag_tab_produkt_to_gruppe.id_gruppe='"+type+"') AND suewag_tab_produkte.ID='"+product_id+"'");
        }

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();

        while (!cursor.isAfterLast() && cursor != null) {
            TM.add("ID",cursor.getString(cursor.getColumnIndex("ID")));
            if(cursor.getColumnIndex("Postleitzahl") != -1) {
                TM.add("Postleitzahl", cursor.getString(cursor.getColumnIndex("Postleitzahl")));
            }else{
                TM.add("Postleitzahl", "");
            }
            TM.add("name_gruppe", cursor.getString(cursor.getColumnIndex("name_gruppe")));
            TM.add("Tarifname", cursor.getString(cursor.getColumnIndex("Tarifname")));
            TM.add("grundgebuehr", cursor.getString(cursor.getColumnIndex("grundgebuehr")));
            TM.add("grundgebuehr2", cursor.getString(cursor.getColumnIndex("grundgebuehr2")));
            TM.add("kwhpreis", cursor.getString(cursor.getColumnIndex("kwhpreis")));
            TM.add("tarifschluessel", cursor.getString(cursor.getColumnIndex("tarifschluessel")));
            TM.add("Preisgarantie", cursor.getString(cursor.getColumnIndex("Preisgarantie")));
            TM.add("Laufzeit", cursor.getString(cursor.getColumnIndex("Laufzeit")));
            TM.add("Verlaengerung", cursor.getString(cursor.getColumnIndex("Verlaengerung")));
            TM.add("Kuendigungsfrist", cursor.getString(cursor.getColumnIndex("Kuendigungsfrist")));
            TM.add("strom_or_gas", cursor.getString(cursor.getColumnIndex("strom_or_gas")));

            cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return TM;
    }

    /**
     * Loading an specific contract by the given ID
     */
    public static TarifModel loadByID(Context context, String key, String plz, @Nullable CustomEventListener OnStatusChange) {
        TarifModel TM = new TarifModel();

        SQLHelper db = new SQLHelper(context);

        Cursor cursor = db.rawQuery("SELECT suewag_tab_produkte.ID, plz.Postleitzahl, suewag_tab_produkte_gruppen.name_gruppe, suewag_tab_produkte.strom_or_gas, suewag_tab_produkte.Tarifname,suewag_tab_produkte.grundgebuehr, suewag_tab_produkte.grundgebuehr2, suewag_tab_produkte.kwhpreis, suewag_tab_produkte.tarifschluessel, suewag_tab_produkte.Preisgarantie, suewag_tab_produkte.Laufzeit, suewag_tab_produkte.Verlaengerung, suewag_tab_produkte.Kuendigungsfrist FROM plz LEFT JOIN suewag_tab_produkt_plz ON plz.ID = suewag_tab_produkt_plz.id_plz LEFT JOIN suewag_tab_produkte ON suewag_tab_produkt_plz.id_produkt = suewag_tab_produkte.ID LEFT JOIN suewag_tab_produkt_to_gruppe ON suewag_tab_produkte.ID = suewag_tab_produkt_to_gruppe.id_produkt LEFT JOIN suewag_tab_produkte_gruppen ON suewag_tab_produkte_gruppen.ID = suewag_tab_produkt_to_gruppe.id_gruppe WHERE (suewag_tab_produkte_gruppen.ID='10' OR suewag_tab_produkte_gruppen.ID='11') AND plz.Postleitzahl='" + plz + "' AND tarifschluessel='" + key + "'");
        //Cursor cursor = db.rawQuery("SELECT plz.Postleitzahl, suewag_tab_produkte_gruppen.name_gruppe, suewag_tab_produkte.strom_or_gas, suewag_tab_produkte.Tarifname, suewag_tab_produkte.grundgebuehr, suewag_tab_produkte.grundgebuehr2, suewag_tab_produkte.kwhpreis, suewag_tab_produkte.tarifschluessel, suewag_tab_produkte.Preisgarantie, suewag_tab_produkte.Laufzeit, suewag_tab_produkte.Verlaengerung, suewag_tab_produkte.Kuendigungsfrist FROM (((suewag_tab_produkte_gruppen INNER JOIN suewag_tab_produkt_to_gruppe ON suewag_tab_produkte_gruppen.ID = suewag_tab_produkt_to_gruppe.id_gruppe) INNER JOIN suewag_tab_produkte ON suewag_tab_produkt_to_gruppe.id_produkt = suewag_tab_produkte.ID) INNER JOIN suewag_tab_produkt_plz ON suewag_tab_produkte.ID = suewag_tab_produkt_plz.id_produkt) INNER JOIN plz ON suewag_tab_produkt_plz.id_plz = plz.ID WHERE tarifschluessel='"+key+"' AND Postleitzahl='"+plz+"'");

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();

        while (!cursor.isAfterLast() && cursor != null) {
            TM.add("ID",cursor.getString(cursor.getColumnIndex("ID")));
            TM.add("Postleitzahl", cursor.getString(cursor.getColumnIndex("Postleitzahl")));
            TM.add("name_gruppe", cursor.getString(cursor.getColumnIndex("name_gruppe")));
            TM.add("Tarifname", cursor.getString(cursor.getColumnIndex("Tarifname")));
            TM.add("grundgebuehr", cursor.getString(cursor.getColumnIndex("grundgebuehr")));
            TM.add("grundgebuehr2", cursor.getString(cursor.getColumnIndex("grundgebuehr2")));
            TM.add("kwhpreis", cursor.getString(cursor.getColumnIndex("kwhpreis")));
            TM.add("tarifschluessel", cursor.getString(cursor.getColumnIndex("tarifschluessel")));
            TM.add("Preisgarantie", cursor.getString(cursor.getColumnIndex("Preisgarantie")));
            TM.add("Laufzeit", cursor.getString(cursor.getColumnIndex("Laufzeit")));
            TM.add("Verlaengerung", cursor.getString(cursor.getColumnIndex("Verlaengerung")));
            TM.add("Kuendigungsfrist", cursor.getString(cursor.getColumnIndex("Kuendigungsfrist")));
            TM.add("strom_or_gas", cursor.getString(cursor.getColumnIndex("strom_or_gas")));

            cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return TM;
    }
}
