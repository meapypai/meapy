package project.meapy.meapy.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yassi on 26/04/2018.
 */

public class BuilderDialog {

    public static void dateDialog(Context context, final TextView dateTextView) {
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                dateTextView.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
            }};
        Calendar c = Calendar.getInstance();
        DatePickerDialog dpDialog=new DatePickerDialog(context, listener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpDialog.show();
    }
}
