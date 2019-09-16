package forge12.x_citeapi.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.R;

public class SignatureDialog {
    /**
     * Saves the last bitmap for the signature
     */
    private static Bitmap mLastBitmap = null;

    /**
     * The Signature class
     */
    private static InkViewCustomized mInk = null;

    /**
     * Defines if the Signature dialog is open or not
     */
    private static boolean mIsOpen = false;

    private static void setSignatureImage(Context c, AlertDialog dialog, @Nullable Bitmap s) {
        mInk = dialog.findViewById(R.id.signature);
        mInk.setMinimumHeight(300);
        mInk.setMinimumHeight(150);
        mInk.setColor(c.getResources().getColor(android.R.color.black, c.getTheme()));
        mInk.setMinStrokeWidth(1.5f);
        mInk.setMaxStrokeWidth(6f);

        if (null != s) {
            mInk.clear();
            mInk.drawBitmap(s, 0, 0, null);
            mInk.invalidate();
        }

        ImageButton ResetSignature = dialog.findViewById(R.id.signature_dialog_button_reset);
        ResetSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInk.clear();
            }
        });
    }

    /**
     * Request the last image/bitmap
     * @return Bitmap|null
     */
    public static Bitmap getBitmap(){
        return mLastBitmap;
    }

    /**
     * Show the Alert Dialog which offers the signature popup
     * @param c
     */
    public static void show(final Context c, @Nullable OnSignatureEventListener OnSignatureEvent) {
        show(c, OnSignatureEvent, null);
    }

    /**
     * Callback function on save
     */
    interface OnSignatureEventListener{
        void onSave(Bitmap bmp);
    }

    /**
     * Show the alert dialog which offers the signature popup
     * @param c
     * @param s
     */
    public static void show(final Context c, @Nullable final OnSignatureEventListener OnSignatureEvent, @Nullable final Bitmap s) {
        // Ensure it is only opened once
        if(mIsOpen){
            return;
        }

        mIsOpen = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setView(R.layout.dialog_signature);
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                setSignatureImage(c, (AlertDialog) dialog, s);

                Button btnSave = ((AlertDialog) dialog).findViewById(R.id.signature_dialog_button_save);
                Button btnCancel = ((AlertDialog) dialog).findViewById(R.id.signature_dialog_button_cancel);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mIsOpen = false;
                        //EventListener.do_action("admin_on_click_api", c, cls);
                        mLastBitmap = mInk.getBitmap(Color.WHITE);

                        if(null != mLastBitmap && null != OnSignatureEvent){
                            OnSignatureEvent.onSave(mLastBitmap);
                        }

                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mIsOpen = false;
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }
}