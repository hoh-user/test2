package forge12.x_citeapi.Handler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Model.ContractImageModel;
import forge12.x_citeapi.Model.ContractModel;
import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.HttpUtils;
import forge12.x_citeapi.interfaces.CallableAction;

public class ContractHandler {
    /**
     * Instance of the ContractHandler
     */
    private static ContractHandler _sInstance = null;

    /**
     * Return an Instance of the Handler
     *
     * @return ContractHandler
     */
    public static ContractHandler getInstance() {
        if (_sInstance == null) {
            _sInstance = new ContractHandler();
        }
        return _sInstance;
    }

    /**
     * The constructor
     */
    public ContractHandler() {
        EventListener.add_action("contract_handler_sync_completed", this.syncImage(), 10, 3);
        EventListener.add_action("contract_handler_sync_completed", this.syncSignature(), 10, 3);
        EventListener.add_action("contract_handler_after_signature_synced", this.sendMail(), 10, 2);
    }

    /**
     * Sync the image with the server
     *
     * @return
     */
    public CallableAction syncImage() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                if (!(args[1] instanceof ContractModel)) {
                    return;
                }

                Context context = (Context) args[0];
                ContractModel Contract = (ContractModel) args[1];
                int ID = Integer.parseInt(Contract.get("ID"));

                ContractImageModel Image = ContractImageModel.load(context, ID, "image", null);

                // Sync Image
                if (Image.isValid()) {
                    Image.set("contract_hash", Contract.get("hash"));
                    Image.sync(context, new CustomEventListener());
                }
            }
        };
    }

    /**
     * Called after the signature has been synched.
     *
     * @return
     */
    public CallableAction sendMail() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                if (!(args[1] instanceof ContractModel)) {
                    return;
                }

                ContractModel Contract = (ContractModel) args[1];
                Context context = (Context) args[0];

                // Create PDF
                HttpUtils.get("mail/contract/live/" + Contract.get("hash"), null, AdminHandler.getApiKey(context), OnMailSend());
                Toast.makeText(context, "Daten übertragen", Toast.LENGTH_LONG).show();
            }
        };
    }

    /**
     * Synchronize the signature with the live server
     *
     * @return
     */
    public CallableAction syncSignature() {
        return new CallableAction() {
            @Override
            public void onCall(Object... args) {
                if (!(args[0] instanceof Context)) {
                    return;
                }

                if (!(args[1] instanceof ContractModel)) {
                    return;
                }

                final Context context = (Context) args[0];
                final ContractModel Contract = (ContractModel) args[1];
                int ID = Integer.parseInt(Contract.get("ID"));

                ContractImageModel Image = ContractImageModel.load(context, ID, "signature", null);

                // Sync Image
                if (Image.isValid()) {
                    Image.set("contract_hash", Contract.get("hash"));
                    Image.sync(context, new CustomEventListener() {
                        @Override
                        public void sendMessage(String message) {
                            EventListener.do_action("contract_handler_after_signature_synced", context, Contract);
                        }
                    });
                } else {
                    EventListener.do_action("contract_handler_after_signature_synced", context, Contract);
                }
            }
        };
    }

    public boolean deleteContractById(Context context, int ID) {
        return ContractModel.delete(context, ID, null);
    }

    public ContractModel getContractByID(Context context, int ID) {
        return ContractModel.load(context, ID, null);
    }

    /**
     * JSON Response after the mail has been send
     *
     * @return
     */
    public JsonHttpResponseHandler OnMailSend() {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("MAIL SEND", "Response: "+response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                Log.v("MAIL SEND", "Response: "+errorResponse);
            }
        };
    }

    /**
     * Sync the contract by the given id
     *
     * @param context
     * @param ID
     */
    public void syncContractById(final Context context, int ID) {
        final ContractModel Contract = getContractByID(context, ID);

        Contract.sync_and_lock(context, new CustomEventListener() {
            @Override
            public void sendMessage(String message) {
                EventListener.do_action("contract_handler_sync_failed",context, message);
            }

            @Override
            public void sendMessage(String message, int option) {
                switch (option) {
                    case 1:
                        EventListener.do_action("contract_handler_sync_completed", context, Contract, message);
                        break;
                    default:
                    case 0:
                        sendMessage(message);
                        break;
                }
            }
        });
    }
}
