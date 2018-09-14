package com.nuaa.safedriving.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuaa.safedriving.MyCalendar;
import com.nuaa.safedriving.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Home extends Fragment {

    private TextView origin, destination, time;
    private ImageView exchange;
    private Button query;

    public Home() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        final View view =
            LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, container, false);
        origin = (TextView) view.findViewById(R.id.origin);
        time = (TextView) view.findViewById(R.id.time);
        destination = (TextView) view.findViewById(R.id.destination);
        exchange = (ImageView) view.findViewById(R.id.exchange);
        query = (Button) view.findViewById(R.id.query);

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String date_text = sdf.format(d);
        date_text = date_text + " " + getDate(d);
        time.setText(date_text);

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin_content = origin.getText().toString().trim();
                String destination_content = destination.getText().toString().trim();
                String temp = destination_content;
                destination_content = origin_content;
                origin_content = temp;
                origin.setText(origin_content);
                destination.setText(destination_content);
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MyCalendar.class);
                startActivityForResult(intent, 200);
            }
        });

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ShuttleInfo.class);
                String origin_content = origin.getText().toString().trim();
                if (origin_content.equals("将军路校区")) {
                    intent.putExtra("type", 0);
                } else {
                    intent.putExtra("type", 1);
                }
                intent.putExtra("date", time.getText().toString().trim());
                startActivity(intent);
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 400:
                String date_text = "";
                int year = data.getExtras().getInt("year");
                int month = data.getExtras().getInt("month");
                int day = data.getExtras().getInt("day");
                date_text += Integer.toString(year) + "年";
                if (month < 10) {
                    date_text = date_text + "0" + Integer.toString(month);
                } else {
                    date_text = date_text + Integer.toString(month);
                }
                date_text += "月";
                if (day < 10) {
                    date_text = date_text + "0" + Integer.toString(day);
                } else {
                    date_text = date_text + Integer.toString(day);
                }
                date_text += "日 ";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                try {
                    Date d = sdf.parse(date_text);
                    date_text += getDate(d);
                    time.setText(date_text);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }
    }

    public String getDate(Date d) {
        String date_text = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        switch (cal.get(Calendar.DAY_OF_WEEK) - 1) {
            case 1:
                date_text += "周一";
                break;
            case 2:
                date_text += "周二";
                break;
            case 3:
                date_text += "周三";
                break;
            case 4:
                date_text += "周四";
                break;
            case 5:
                date_text += "周五";
                break;
            case 6:
                date_text += "周六";
                break;
            case 7:
                date_text += "周日";
                break;
        }
        Calendar cal2 = Calendar.getInstance();
        if (cal2.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
            && cal2.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
            if (cal2.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
                date_text += " 今天";
            }
            if (cal2.get(Calendar.DAY_OF_MONTH) + 1 == cal.get(Calendar.DAY_OF_MONTH)) {
                date_text += " 明天";
            }
            //TODO 跨月份问题
            if (cal2.get(Calendar.DAY_OF_MONTH) + 2 == cal.get(Calendar.DAY_OF_MONTH)) {
                date_text += " 后天";
            }
        }
        return date_text;
    }
}
