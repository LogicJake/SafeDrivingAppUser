package com.nuaa.safedriving;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.Calendar;

public class MyCalendar extends AppCompatActivity {
    private CalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        long minTime = cal.getTimeInMillis();
        calendarView.setMinDate(minTime);
        Calendar calendar = Calendar.getInstance();
        // 结束日期
        calendar.add(Calendar.DATE, 2);
        long lateTime = calendar.getTimeInMillis();
        calendarView.setMaxDate(lateTime);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i2, int i3) {
                Intent mIntent = new Intent();
                mIntent.putExtra("year", i);
                mIntent.putExtra("month", (i2+1));
                mIntent.putExtra("day", i3);
                // 设置结果，并进行传送
                setResult(400,mIntent);
                finish();
            }
        });
    }
}
