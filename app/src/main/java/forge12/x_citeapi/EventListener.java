package forge12.x_citeapi;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import forge12.x_citeapi.Model.EventAction;
import forge12.x_citeapi.interfaces.CallableAction;

/**
 * Eventlistener used to call different
 * events whenever neccessary.
 */
public class EventListener {
    /**
     * Array containing all registered Filter
     */
    private static List<EventAction> _Filters = new ArrayList<>();
    /**
     * Array containing all registrered Actions
     */
    private static List<EventAction> _Actions = new ArrayList<>();

    /**
     * Removes the given filter from the list
     *
     * @param {string}         pName
     * @param {CallableAction} pCallback
     */
    public static void remove_filter(String pName, CallableAction pCallback) {
        for (int j = _Filters.size() - 1; j >= 0; j--) {
            EventAction action = _Filters.get(j);

            if (action != null && action._Name.equals(pName) && action._Callback == pCallback) {
                Log.i("Filter", "Filter removed " + pName);
                _Filters.remove(j);
            }
        }
    }

    /**
     * Firing a filter
     *
     * @param {string} pName
     * @param {string} args
     * @return {string}
     */
    public static String apply_filters(String pName, String arg, String... args) {
        for (EventAction action : _Filters) {
            if (action._Name.equals(pName) && action._Args == args.length + 1) {
                arg = action._Callback.onCall(arg, args);
            }
        }
        return arg;
    }

    /**
     * Add a new filter to the list
     *
     * @param {string}         pName
     * @param {CallableAction} pCallback
     */
    public static void add_filter(String pName, CallableAction pCallback) {
        _Filters.add(new EventAction(pName, pCallback, 10, 1));
    }

    /**
     * Add a new filter to the list
     *
     * @param {string}         pName
     * @param {CallableAction} pCallback
     * @param {int}            pPriority
     * @param {int}            pArgs
     */
    public static void add_filter(String pName, CallableAction pCallback, int pPriority, int pArgs) {
        _Filters.add(new EventAction(pName, pCallback, pPriority, pArgs));
    }

    /**
     * Remove an Action from the List
     * <p>
     * Notice: Functions do not work as callback to be removed.
     *
     * @param {string}         pName
     * @param {CallableAction} pCallback
     */
    public static void remove_action(String pName, CallableAction pCallback) {
        Log.i("Action", "Action removed " + pName);
        for (Iterator<EventAction> iterator = _Actions.iterator(); iterator.hasNext(); ) {
            EventAction it = iterator.next();
            if(it._Name.equals(pName) && it._Callback.equals(pCallback)){
                iterator.remove();
            }
            /* if (iterator.next()._Name.equals(pName)) {
                iterator.remove();
            }*/
        }
    }

    /**
     * Add a new Action to the Listener
     *
     * @param {string}         pName
     * @param {CallableAction} pCallback
     */
    public static void add_action(String pName, CallableAction pCallback) {
        Log.i("Action", "Action added " + pName);
        add_action(pName, pCallback, 10, 0);
    }

    /**
     * Add an Action with the given priority and the number of arguments
     *
     * @param {string}         pName
     * @param {CallableAction} pCallback
     * @param {int}            pPriority
     * @param {int}            pArgs
     */
    public static void add_action(String pName, CallableAction pCallback, int pPriority, int pArgs) {
        Log.i("Action", "Action added " + pName);
        _Actions.add(new EventAction(pName, pCallback, pPriority, pArgs));

        Collections.sort(_Actions, new Comparator<EventAction>() {
            @Override
            public int compare(EventAction o1, EventAction o2) {
                if (o1._Priority > o2._Priority) {
                    return -1;
                } else if (o1._Priority < o2._Priority) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    public static boolean has_action(String pName) {
        return has_action(pName, 0);
    }

    public static boolean has_action(String pName, int args) {
        for (Iterator<EventAction> iterator = _Actions.iterator(); iterator.hasNext(); ) {
            EventAction it = iterator.next();
            if (it._Name.equals(pName) && it._Args == args) {
                return true;
            }
        }

        return false;
    }

    /**
     * Call a given action from the listener
     *
     * @param {string} pName
     */
    public static boolean do_action(String pName) {
        if (!has_action(pName)) {
            Log.i("Action", "Action not found " + pName + " width 0 args");
            return false;
        }

        for (Iterator<EventAction> iterator = _Actions.iterator(); iterator.hasNext(); ) {
            EventAction it = iterator.next();
            if (it._Name.equals(pName)) {
                if (it._Args == 0) {
                    Log.i("Action", "Action fired " + pName + " with args 0");
                    it._Callback.onCall();
                } else {
                    Log.i("Action", "Args not matching " + pName);
                }
            }
        }

        return true;
    }

    /**
     * Call a given action with additional arguments
     *
     * @param {string} pName
     * @param {Object} args
     */
    public static boolean do_action(String pName, Object... args) {
        if (!has_action(pName, args.length)) {
            Log.i("Action", "Action not found " + pName + " with " + args.length + " args");
            return false;
        }

        for (Iterator<EventAction> iterator = _Actions.iterator(); iterator.hasNext(); ) {
            EventAction it = iterator.next();
            if (it._Name.equals(pName)) {
                if (it._Args == args.length) {
                    Log.i("Action", "Action fired " + pName + " with args " + args.length);
                    it._Callback.onCall(args);
                } else {
                    Log.i("Action", "Args not matching " + pName);
                }
            }
        }

        return true;
    }
}
