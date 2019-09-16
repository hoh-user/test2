package forge12.x_citeapi.Utils;

public class Property {
    private String m_key;
    private String m_value;

    public Property(String pKey, String pValue){
        m_key = pKey;
        this.set_value(pValue);
    }

    public Property(String pKey, int pValue){
        m_key = pKey;
        this.set_value(pValue);
    }

    public String get_key(){
        return this.m_key;
    }

    public String get_value(){
        return this.m_value;
    }

    public void set_value(String pValue){
        this.m_value = pValue;
    }

    public void set_value(int pValue){ this.m_value = Integer.toString(pValue); }
}
