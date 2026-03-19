package com.betaproduct.Kalender;

import android.app.DatePickerDialog;
import android.content.Context;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import java.util.Calendar;
import java.text.DateFormatSymbols; // Tambahan untuk ambil nama bulan
import java.util.Locale;

@DesignerComponent(
        version = 7, 
        description = "Kalender Luxury - Support Nama Bulan Indonesia",
        category = ComponentCategory.EXTENSION,
        nonVisible = true)
@SimpleObject(external = true)

public class Kalender extends AndroidNonvisibleComponent {
    private Context context;

    public Kalender(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
    }

    @SimpleFunction(description = "Munculkan Kalender Mewah (Output Angka & Teks)")
    public void TampilkanPopUp() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Tema 5 untuk tampilan Material Light (Putih Bersih)
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, 5, 
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // Ambil nama bulan dalam Bahasa Indonesia
                    String namaBulan = new DateFormatSymbols(new Locale("id")).getMonths()[monthOfYear];
                    
                    // Kirim ke Event DayChanged
                    DayChanged(year, monthOfYear + 1, dayOfMonth, namaBulan);
                }
            }, year, month, day);

        datePickerDialog.show();
    }

    @SimpleEvent(description = "Terpanggil saat tanggal dipilih. Memberikan output angka dan teks bulan.")
    public void DayChanged(int year, int month, int dayOfMonth, String monthName) {
        EventDispatcher.dispatchEvent(this, "DayChanged", year, month, dayOfMonth, monthName);
    }
}
