package com.nuaa.safedriving;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ShuttleInfo extends AppCompatActivity {
    private ImageView up;
    private ScrollView scrollView;
    private TextView noInfo;
    private List<HashMap<String, Object>> mListData = new ArrayList<HashMap<String, Object>>();
    private SimpleAdapter mSchedule;
    private ListView lv;
    private SwipeRefreshLayout mswipeRefreshLayout;
    private SweetAlertDialog pDialog;
    private int type;
    private String date;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mListData.size() == 0)
                        noInfo.setVisibility(View.VISIBLE);
                    else
                        noInfo.setVisibility(View.GONE);
                    mSchedule = new SimpleAdapter(ShuttleInfo.this,
                            mListData,//数据来源
                            R.layout.info_list,//ListItem的XML实现
                            new String[]{"time", "car_num", "departure", "destination","status"},
                            new int[]{R.id.time, R.id.carnum, R.id.departure, R.id.destination,R.id.status});
                    lv.setAdapter(mSchedule);
                    setListViewHeightBasedOnChildren(lv);
                    mSchedule.notifyDataSetChanged();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String time = ((TextView) view.findViewById(R.id.time)).getText().toString();
                            Bundle bundle = new Bundle();
                            bundle.putInt("type", type);
                            bundle.putString("date",date.split(" ")[0]);
                            bundle.putString("time",time);
                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            intent.setClass(ShuttleInfo.this, Ride.class);
                            startActivity(intent);
                        }
                    });
                    pDialog.cancel();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuttle_info);
        up = (ImageView)findViewById(R.id.up);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        noInfo = (TextView)findViewById(R.id.noInfo);
        lv = (ListView)findViewById(R.id.shuttlelist);
        mswipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.sv);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        Intent intent =getIntent();
        type = intent.getIntExtra("type",1);
        date = intent.getStringExtra("date");

        getInfo(type,date);         //获取班车信息

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && scrollView.getScrollY() > 0)
                    up.setVisibility(View.VISIBLE);
                return false;
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                        up.setVisibility(View.GONE);
                    }
                });
            }
        });

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mListData.clear();
//                getInfo(type,date);         //获取班车信息
                mswipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    protected void getInfo(final int type,String date)  {
        pDialog.show();
        date = date.split(" ")[0];
        final String fdate = date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            final Date date_d = format.parse(date);
            final long time = date_d.getTime()/1000;
            new Thread(new Runnable(){
                @Override
                public void run()
                {
                    JSONArray res = NewServices.getInfo(type,time);
                    if(res!=null) {
                        for (int i = 0; i < res.length(); i++) {
                            try {
                                JSONObject temp = (JSONObject) res.get(i);
                                HashMap map = new HashMap<String, Object>();
                                map.put("time", temp.getString("time"));
                                map.put("car_num", temp.getString("car_num") + "辆");
                                if (type == 1) {       //将军路出发
                                    map.put("destination", "明故宫校区");
                                    if (temp.getString("east").equals("1"))
                                        map.put("departure", "将军路校区东区");
                                    else if (temp.getString("lancui").equals("1"))
                                        map.put("departure", "揽翠苑");
                                    else
                                        map.put("departure", "将军路校区西区");
                                } else {
                                    map.put("departure", "明故宫校区");
                                    if (temp.getString("east").equals("1"))
                                        map.put("destination", "将军路校区东区");
                                    else if (temp.getString("lancui").equals("1"))
                                        map.put("destination", "揽翠苑");
                                    else
                                        map.put("destination", "将军路校区西区");
                                }
                                Calendar c = Calendar.getInstance();
                                c.setTime(date_d);
                                Calendar c2 = Calendar.getInstance();
                                c2.setTime(new Date());
                                if (c.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
                                    long now_time = System.currentTimeMillis() / 1000;
                                    String date_s = fdate + " " + temp.getString("time");
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

                                    if (format.parse(date_s).getTime() / 1000 <= now_time)
                                        map.put("status", "已发车");
                                    else
                                        map.put("status", "未发车");
                                } else
                                    map.put("status", "未发车");
                                mListData.add(map);
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取listview的适配器
        ListAdapter listAdapter = listView.getAdapter(); //item的高度
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高 //统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight()+listView.getDividerHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }
}
