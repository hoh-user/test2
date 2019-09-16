package forge12.x_citeapi.Handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import forge12.x_citeapi.Model.ContractImageModel;
import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.Helper;

public class ContractListHandler {
    /**
     * ContractListHandler Instance
     */
    private static ContractListHandler _sInstance = null;

    /**
     * Saves the current photo path
     */
    String mCurrentPhotoPath = null;

    /**
     * Save the last image contract id
     */
    public static int last_image_contract_id = -1;

    /**
     * Return an Instance of the List Handler
     * @return
     */
    public static ContractListHandler getInstance(){
        if(_sInstance == null){
            _sInstance = new ContractListHandler();
        }
        return _sInstance;
    }

    /**
     * Check if the image exists
     * @param context
     * @return {boolean}
     */
    public boolean imageExist(Context context, int ID){
        ContractListHandler.last_image_contract_id = ID;
        ContractImageModel CIM = ContractImageModel.load(context, ID, "image", null);
        return CIM.isValid();
    }

    /**
     * Returns the image object given by the Contract ID
     * @param context
     * @param ID
     * @return
     */
    public String getImageByID(Context context, int ID){
        ContractImageModel CIM = ContractImageModel.load(context, ID, "image", null);
        return CIM.get("image");
    }

    /**
     * Create the image file which will store the image taken by the camera.
     * @param context
     * @return
     * @throws IOException
     */
    public File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Save the image taken by the camera to the database.
     * @param context
     */
    public void saveImage(Context context){
        Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

        //return;
        //Bundle extras = data.getExtras();
        //Bitmap imageBitmap = (Bitmap) extras.get("data");
        String imageEncoded = Helper.convertBitmapToBase64(imageBitmap);

        File file = new File(mCurrentPhotoPath);
        file.delete();

        if (ContractListHandler.last_image_contract_id != -1) {
            ContractImageModel CIM = ContractImageModel.load(context, ContractListHandler.last_image_contract_id, "image", null);

            if (CIM == null) {
                CIM = new ContractImageModel();
            }

            CIM.set("contract_id", last_image_contract_id);
            CIM.set("image", imageEncoded);
            CIM.set("image_type", "image");
            CIM.save(context, new CustomEventListener() {
                @Override
                public void sendMessage(String message) {
                    Log.v("Test:", message);
                }

                @Override
                public void sendMessage(String message, int option) {
                    sendMessage(message);
                }

                @Override
                public void sendMessage(String message, int option, int code) {
                    sendMessage(message);
                }
            });
        }
    }
}
