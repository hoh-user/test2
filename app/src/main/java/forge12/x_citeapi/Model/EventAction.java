package forge12.x_citeapi.Model;

import java.util.Comparator;
import java.util.concurrent.Callable;

import forge12.x_citeapi.interfaces.CallableAction;

public class EventAction {
    /**
     * the name of the action
     */
    public String _Name = "";
    /**
     * the callback function
     */
    public CallableAction _Callback = null;
    /**
     * the priority of the action
     */
    public int _Priority = 10;
    /**
     * passed arguments to the function
     */
    public int _Args = 0;

    /**
     * Default constructor
     * @param pName
     * @param pCallback
     */
    public EventAction(String pName, CallableAction pCallback) {
        this(pName, pCallback, 10, 0);
    }

    /**
     * Constructor with additional informations
     * @param pName
     * @param pCallback
     * @param pPriority
     * @param pArgs
     */
    public EventAction(String pName, CallableAction pCallback, int pPriority, int pArgs) {
        _Name = pName;
        _Callback = pCallback;
        _Priority = pPriority;
        _Args = pArgs;
    }
}
