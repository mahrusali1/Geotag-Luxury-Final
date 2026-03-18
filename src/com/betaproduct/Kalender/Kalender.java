package com.betaproduct.Kalender;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import java.util.Calendar;

@DesignerComponent(
        version = 1,
        description = "Calendar View Extension - SKP Click Edition",
        category = ComponentCategory.EXTENSION,
        nonVisible = true,
        iconName = "")
@SimpleObject(external = true)

public class Kalender extends AndroidNonvisibleComponent {
    private Context context;
    private CalendarView calendarView;

    public Kalender(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
        this.calendarView = new CalendarView(context);
    }

    @SimpleFunction(description = "Menampilkan kalender ke dalam layout (Arrangement)")
    public void CreateCalendar(AndroidViewComponent view) {
        LinearLayout linearLayout = new LinearLayout(context);
        if (calendarView.getParent() != null) {
            ((ViewGroup) calendarView.getParent()).removeView(calendarView);
        }
        linearLayout.addView(calendarView);
        ViewGroup viewGroup = (ViewGroup) view.getView();
        viewGroup.addView(linearLayout);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                DayChanged(year, month + 1, dayOfMonth);
            }
        });
    }

    @SimpleEvent(description = "Terpanggil saat tanggal dipilih")
    public void DayChanged(int year, int month, int dayOfMonth) {
        EventDispatcher.dispatchEvent(this, "DayChanged", year, month, dayOfMonth);
    }

    @SimpleProperty
    public void Date(long value) {
        calendarView.setDate(value);
    }

    @SimpleProperty
    public long Date() {
        return calendarView.getDate();
    }
}
