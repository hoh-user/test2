package forge12.x_citeapi.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.R;

public class PasswordDialog {

    public static void show(final Context c, final Class cls, String Action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setView(R.layout.dialog_password);
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnLogin = ((AlertDialog) dialog).findViewById(R.id.password_dialog_button_login);
                Button btnCancel = ((AlertDialog) dialog).findViewById(R.id.password_dialog_button_cancel);

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText password = (EditText) ((Dialog) dialog).findViewById(R.id.dialog_password_input);

                        if (password.getText().toString().equals("admin")) {
                            EventListener.do_action("admin_on_click_api", c, cls);
                            dialog.dismiss();
                        } else {
                            TextView errorMessage = (TextView) ((Dialog) dialog).findViewById(R.id.dialog_password_error_message);
                            errorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }
}