package forge12.x_citeapi.Views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

interface DatePickerListener{
    void onDataSet(DatePicker view, int year, int month, int day);
}

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private DatePickerListener mDatePickerListener;

    @Override
    public DatePickerDialog onCreateDialog(Bundle savedInstanceState){
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setOnDateSetListener(DatePickerListener pDatePickerListener){
        mDatePickerListener = pDatePickerListener;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if(mDatePickerListener != null){
            mDatePickerListener.onDataSet(view,year,month+1,day);
        }
    }
}
