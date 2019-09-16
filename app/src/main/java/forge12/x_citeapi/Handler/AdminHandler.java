package forge12.x_citeapi.Handler;

import android.content.Context;
import android.content.Intent;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Model.SettingsModel;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.interfaces.CallableAction;

/**
 * The Admin Handler
 */
public class AdminHandler {
    /**
     * Save the instance of the admin handler
     */
    private static AdminHandler _sInstance = null;

    /**
     * Singleton
     */
    private AdminHandler() {
        EventListener.add_action("admin_on_click_sync", this.onClickSync(), 10, 2);
        EventListener.add_action("admin_on_click_api", this.onClickApi(), 10, 2);
        EventListener.add_action("admin_on_makler_request_received", this.onMaklerRequestReceived(), 10, 4);
        EventListener.add_action("admin_on_save_api_key", this.onApiKeySave(), 10, 2);
    }

    public CallableAction onApiKeySave() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                if (!(args[1] instanceof String)) {
                    return;
                }

                final Context context = (Context) args[0];
                String api_key = (String) args[1];

                SettingsModel SM = SettingsModel.load(context, "api_key", null);

                if (SM == null) {
                    SM = new SettingsModel();
                }

                SM.set("meta_key", "api_key");
                SM.set("meta_value", api_key);

                SM.save(context, new CustomEventListener() {
                    @Override
                    public void sendMessage(String message) {
                        AlertHelper.showDataSaved(context);
                    }
                });
            }
        };
    }

    public CallableAction onMaklerRequestReceived() {
        return new CallableAction() {
            @Override
            /**
             * @param Context context
             * @param String makler_id
             * @param String email
             * @param String password
             */
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                if (!(args[2] instanceof String) || !(args[1] instanceof String) || !(args[3] instanceof String)) {
                    return;
                }


                final Context context = (Context) args[0];
                String makler_id = (String) args[1];
                String email = (String) args[2];
                String password = (String) args[3];


                SettingsModel SM = SettingsModel.load(context, "makler_id", null);

                // save makler id
                if (SM == null) {
                    SM = new SettingsModel();
                }

                SM.set("meta_key", "makler_id");
                SM.set("meta_value", makler_id);

                SM.save(context, new CustomEventListener() {
                    @Override
                    public void sendMessage(String message, int option, int code) {
                        if(code == 500) {
                            AlertHelper.showDataError(context, message);
                        }
                    }
                });

                // Save email makler
                SM = SettingsModel.load(context, "makler_email", null);

                if (SM == null) {
                    SM = new SettingsModel();
                }

                SM.set("meta_key", "makler_email");
                SM.set("meta_value", email);

                SM.save(context, new CustomEventListener() {
                    @Override
                    public void sendMessage(String message, int option, int code) {
                        if (code == 500) {
                            AlertHelper.showDataError(context, message);
                        }
                    }
                });

                // Save password makler as md5
                SM = SettingsModel.load(context, "makler_email", null);

                if (SM == null) {
                    SM = new SettingsModel();
                }

                SM.set("meta_key", "makler_password");
                SM.set("meta_value", password);

                SM.save(context, new CustomEventListener() {
                    @Override
                    public void sendMessage(String message, int option, int code) {
                        if (code == 500) {
                            AlertHelper.showDataError(context);
                        } else {
                            AlertHelper.showDataSaved(context);
                        }
                    }
                });
            }
        };
    }

    /**
     * Callback function to open a new intent.
     *
     * @return {CallableAction}
     */
    public CallableAction onClickSync() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                if (!(args[1] instanceof Class)) {
                    return;
                }
                Context context = (Context) args[0];

                Intent intent = new Intent(context, (Class) args[1]);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }
        };
    }

    /**
     * Callback function to open a new intent.
     *
     * @return {CallableAction}
     */
    public CallableAction onClickApi() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                if (!(args[1] instanceof Class)) {
                    return;
                }

                Context context = (Context) args[0];

                Intent intent = new Intent(context, (Class) args[1]);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }
        };
    }

    /**
     * Allow only one instance of the class to be generated
     */
    public static AdminHandler getInstance() {
        if (_sInstance == null) {
            _sInstance = new AdminHandler();
        }
        return _sInstance;
    }

    /**
     * return the api key
     *
     * @param context
     * @return
     */
    public static String getApiKey(Context context) {
        SettingsModel SM = SettingsModel.load(context, "api_key", null);
        if (null == SM) {
            return "";
        }
        return SM.get("meta_value");
    }
}
