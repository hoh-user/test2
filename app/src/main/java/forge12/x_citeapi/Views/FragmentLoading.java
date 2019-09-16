package forge12.x_citeapi.Views;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import forge12.x_citeapi.R;

class DialogContainer {
    private int _ID = 0;
    private Dialog _Dialog = null;

    public DialogContainer(int pID, Dialog pDialog) {
        this._ID = pID;
        this._Dialog = pDialog;
    }

    public Dialog getDialog() {
        return this._Dialog;
    }

    public int getID() {
        return this._ID;
    }
}

public class FragmentLoading extends Fragment {
    public static List<DialogContainer> sList = new ArrayList<DialogContainer>();

    public static void Add(DialogContainer pDialog) {
        sList.add(pDialog);
    }

    public static void Remove(int ID) {
        int index = -1;
        boolean hit = false;
        for (int i = 0; i < sList.size(); i++) {
            if (sList.get(i).getID() == ID) {
                index = i;
                hit = true;
                break;
            }
        }

        if(hit == true) {
            sList.remove(index);
        }
    }

    public static void closeDialog(int ID) {
        for (DialogContainer d : sList) {
            if (d.getID() == ID) {
                d.getDialog().hide();
                d.getDialog().dismiss();
            }
        }
        Remove(ID);
    }

    public static void openDialog(Context context, int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.fragment_loading);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Add(new DialogContainer(ID, dialog));
    }

    public static Dialog getDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.fragment_loading);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}