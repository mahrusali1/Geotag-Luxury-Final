package com.betaproduct.Kalender;

import android.app.DatePickerDialog;
import android.content.Context;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import java.util.Calendar;

@DesignerComponent(
        version = 5, 
        description = "Kalender Material Luxury - Full Calendar View",
        category = ComponentCategory.EXTENSION,
        nonVisible = true)
@SimpleObject(external = true)

public class Kalender extends AndroidNonvisibleComponent {
    private Context context;

    public Kalender(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
    }

    @SimpleFunction(description = "Munculkan Kalender Material Seperti Gambar")
    public void TampilkanPopUp() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Menggunakan tema 0 (Default) tapi dengan listener yang dipaksa
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, 
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    DayChanged(year, monthOfYear + 1, dayOfMonth);
                }
            }, year, month, day);

        // RAHASIA AGAR TAMPIL KALENDER PENUH:
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.getDatePicker().setSpinnersShown(false); 

        datePickerDialog.show();
    }

    @SimpleEvent(description = "Hasil pilihan tanggal")
    public void DayChanged(int year, int month, int dayOfMonth) {
        EventDispatcher.dispatchEvent(this, "DayChanged", year, month, dayOfMonth);
    }
}
