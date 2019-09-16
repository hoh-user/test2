package forge12.x_citeapi.interfaces;

public class CallableAction {
    /**
     * Default onCall with x args
     * @param args
     */
    public void onCall(Object... args) {
    }

    /**
     * Default on call without any args
     */
    public void onCall() {
    }

    /**
     * Default onCall with strings only
     * @param arg
     * @param args
     * @return {String}
     */
    public String onCall(String arg, String... args) {
        return arg;
    }

    /**
     * Default onCall with only one param
     * @param arg
     * @return {String}
     */
    public String onCall(String arg) {
        return arg;
    }
}
