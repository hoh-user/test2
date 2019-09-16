package forge12.x_citeapi.Model;

import android.util.Log;

import java.util.ArrayList;

import forge12.x_citeapi.Utils.Property;

public class AbstractModel {
    /**
     * List with Properties
     */
    private ArrayList<Property> m_Properties = new ArrayList<Property>();

    public AbstractModel() {
    }

    /**
     * Add / set a new property
     *
     * @param pKey
     * @param pValue
     */
    public void add(String pKey, String pValue) {
        Log.i("add", pKey + "=" + pValue);
        this.set(pKey, pValue);
        //m_Properties.add(new Property(pKey, pValue));
    }

    /**
     * Add / set a new property
     *
     * @param pKey
     * @param pValue
     */
    public void add(String pKey, int pValue) {
        this.set(pKey, pValue);
        //m_Properties.add(new Property(pKey, pValue));
    }

    /**
     * Returns a property
     *
     * @param pKey
     * @return
     */
    public String get(String pKey) {
        for (int i = 0; i < m_Properties.size(); i++) {
            if (m_Properties.get(i).get_key().equals(pKey)) {
                Log.i("get", pKey + "=" + m_Properties.get(i).get_value());
                return m_Properties.get(i).get_value();
            }
        }
        return "";
    }

    /**
     * checks if the property exists
     *
     * @param pKey
     * @return
     */
    public boolean has(String pKey) {
        for (int i = 0; i < m_Properties.size(); i++) {
            if (m_Properties.get(i).get_key() == pKey) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds or updates a new property
     *
     * @param pKey
     * @param pValue
     */
    public void set(String pKey, String pValue) {
        for (int i = 0; i < m_Properties.size(); i++) {
            if (m_Properties.get(i).get_key().equals(pKey)) {
                m_Properties.get(i).set_value(pValue);
                return;
            }
        }

        m_Properties.add(new Property(pKey, pValue));
    }

    /**
     * Set a property
     *
     * @param pKey
     * @param pValue
     */
    public void set(String pKey, int pValue) {
        set(pKey, Integer.toString(pValue));
    }

    public void debug() {
        for (int i = 0; i < m_Properties.size(); i++) {
            Log.v("PROPERTY", m_Properties.get(i).get_key() + " : " + m_Properties.get(i).get_value());
        }
    }
}
