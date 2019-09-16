package forge12.x_citeapi.Handler;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import forge12.x_citeapi.Model.ContractImageModel;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.Views.InkViewCustomized;
import forge12.x_citeapi.interfaces.CallableAction;

public class SignatureHandler {
    /**
     * Adding the signature
     */
    public static CallableAction save_signature_image = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            String ID = args[0].toString();
            Bitmap Ink = (Bitmap) args[2];
            AppCompatActivity context = (AppCompatActivity) args[1];

            ContractImageModel CIM = ContractImageModel.load(context, 0, "signature", null);
            CIM.add("contract_id", ID);
            CIM.add("image", Helper.convertBitmapToBase64(Ink));
            CIM.add("image_type", "signature");
            CIM.save(context, null);
        }
    };

    /**
     * Updating the signature
     */
    public static CallableAction update_signature_image = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            String ID = args[0].toString();
            Bitmap Ink = (Bitmap) args[2];
            AppCompatActivity context = (AppCompatActivity) args[1];

            /**
             * No signature attached so we can skip adding it.
             */
            if(Ink == null){
                return;
            }

            ContractImageModel CIM = ContractImageModel.load(context, Integer.parseInt(ID), "signature", null);
            CIM.add("contract_id", ID);
            CIM.add("image", Helper.convertBitmapToBase64(Ink));
            CIM.add("image_type", "signature");
            CIM.save(context, null);
        }
    };
}
