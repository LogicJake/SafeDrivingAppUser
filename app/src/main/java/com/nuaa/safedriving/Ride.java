package com.nuaa.safedriving;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.nuaa.safedriving.model.HResult;
import com.nuaa.safedriving.util.DynamicLineChartManager;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER_HORIZONTAL;

public class Ride extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private int type;
    private String date;
    private String time;
    private String tag = null;

    private ImageView backup;
    private SensorManager sensorManager;
    String Region = null;
    JSONObject res = new JSONObject();
    JSONObject res2 = new JSONObject();
    JSONArray data = new JSONArray();
    JSONArray data2 = new JSONArray();
    Boolean isHave1 = false;
    Boolean isHave2 = false;
    Button finsh;
    int rideId;
    private FinishPopupWindow menuWindow;
    private SharedPreferences preferences;

    private DynamicLineChartManager dynamicLineChartManager1;
    private DynamicLineChartManager dynamicLineChartManager2;
    private List<Float> list1 = new ArrayList<>(); //数据集合
    private List<Float> list2 = new ArrayList<>(); //数据集合
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    private int count1 = 50;
    private int count2 = 50;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    float rate = msg.getData().getFloat("rate");
                    String suggestion = msg.getData().getString("suggestion");
                    String token = preferences.getString("token", null);
                    postComment(token, rate, suggestion);
                    break;
                case 1:
                    int flag = (int) msg.obj;
                    if (flag == HResult.S_OK.getIndex()) {
                        Intent intent = new Intent(Ride.this, Surprise.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Ride.this, "提交失败请重新提交点评", Toast.LENGTH_LONG).show();
                        menuWindow = new FinishPopupWindow(Ride.this, handler);
                        //显示窗口
                        menuWindow.showAtLocation(Ride.this.findViewById(R.id.head),
                            BOTTOM | CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                        menuWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                        menuWindow.setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                    break;
                case 2:
                    JSONObject res = (JSONObject) msg.obj;
                    if (res == null) {
                        Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                        chooseSeat();
                    } else {
                        try {
                            int hr = res.getInt("hr");
                            if (hr == HResult.S_OK.getIndex()) {
                                rideId = res.getInt("data");
                                StartRecord();          //一切正常采集数据
                                init();
                            } else {
                                Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                                chooseSeat();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            chooseSeat();
                            Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            long timeStamp = System.currentTimeMillis();
            JSONObject tmp = new JSONObject();
            try {
                if (count1 == 50) {
                    list1.add(x);
                    list1.add(y);
                    list1.add(z);
                    dynamicLineChartManager1.addEntry(list1);
                    list1.clear();
                    count1 = 0;

                    tmp.put("time", "" + timeStamp);
                    tmp.put("x", "" + x);
                    tmp.put("y", "" + y);
                    tmp.put("z", "" + z);
                    data.put(tmp);
                    if (data.length() == 10) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String token = preferences.getString("token", null);
                                NewServices.collect(rideId, 1, token, data.toString());
                                Log.d("data length", "run: " + data.length());
                                data = new JSONArray();
                            }
                        }).start();
                    }
                }
                count1++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    //加速度传感器
    private SensorEventListener sensorEventListener2 = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            long timeStamp = System.currentTimeMillis();
            JSONObject tmp = new JSONObject();
            try {
                if (count2 == 50) {
                    list2.add(x);
                    list2.add(y);
                    list2.add(z);
                    dynamicLineChartManager2.addEntry(list2);
                    list2.clear();
                    count2 = 0;
                    tmp.put("time", "" + timeStamp);
                    tmp.put("x", "" + x);
                    tmp.put("y", "" + y);
                    tmp.put("z", "" + z);
                    data2.put(tmp);
                    if (data2.length() == 10) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String token = preferences.getString("token", null);
                                NewServices.collect(rideId, 0, token, data2.toString());
                                Log.d("data2 length", "run: " + data2.length());
                                data2 = new JSONArray();
                            }
                        }).start();
                    }
                }
                count2++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        context = this;
        backup = (ImageView) findViewById(R.id.backup);
        finsh = (Button) findViewById(R.id.finish);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);        //传感器
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        chooseSeat();

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");

        tag = type + date + time;       //评论唯一标志

        backup.setOnClickListener(this);

        finsh.setOnClickListener(this);
    }

    public void postComment(final String token, final float rate, final String suggestion) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int flag = NewServices.insertComment(token, rate, suggestion, rideId);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = flag;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    public void StartRecord() {     //注册传感器
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            isHave1 = true;
            sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                sensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            isHave2 = true;
            sensorManager.registerListener(sensorEventListener2,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void chooseSeat() {
        final AlertDialog.Builder selectRegion = new AlertDialog.Builder(Ride.this);
        final View dialogView =
            LayoutInflater.from(Ride.this).inflate(R.layout.dialog_region, null);
        selectRegion.setTitle("根据图示选择座位位置");
        selectRegion.setCancelable(false);
        selectRegion.setView(dialogView);
        RadioGroup result = (RadioGroup) dialogView.findViewById(R.id.select);
        final RadioButton A = (RadioButton) dialogView.findViewById(R.id.A);
        final RadioButton B = (RadioButton) dialogView.findViewById(R.id.B);
        final RadioButton C = (RadioButton) dialogView.findViewById(R.id.C);
        final RadioButton D = (RadioButton) dialogView.findViewById(R.id.D);
        result.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == A.getId()) {
                    Region = "A";
                } else if (checkedId == B.getId()) {
                    Region = "B";
                } else if (checkedId == C.getId()) {
                    Region = "C";
                } else if (checkedId == D.getId()) {
                    Region = "D";
                }
            }
        });
        selectRegion.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        selectRegion.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Region == null) {
                    Toast.makeText(Ride.this, "没有选择区域，请重新提交！", Toast.LENGTH_SHORT).show();
                    chooseSeat();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String token = preferences.getString("token", null);
                            JSONObject result = NewServices.startRide(Region, token, tag);
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
        selectRegion.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish:
                postEndRide();
                menuWindow = new FinishPopupWindow(Ride.this, handler);
                //显示窗口
                menuWindow.showAtLocation(Ride.this.findViewById(R.id.head),
                    BOTTOM | CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                menuWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                menuWindow.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                break;
            case R.id.backup:
                postEndRide();
                finish();
                break;
        }
    }

    private void postEndRide() {
        unRegisterListener();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = preferences.getString("token", null);
                NewServices.endRide(rideId, token);
            }
        }).start();
    }

    private void unRegisterListener() {
        if (sensorManager != null) {
            Log.d("Ride", "unRegisterListener: ");
            sensorManager.unregisterListener(sensorEventListener);
            sensorManager.unregisterListener(sensorEventListener2);
        }
    }

    public void init() {
        LineChart GYROSCOPE = (LineChart) findViewById(R.id.GYROSCOPE);
        LineChart ACCELEROMETER = (LineChart) findViewById(R.id.ACCELEROMETER);

        names.add("x");
        names.add("y");
        names.add("z");
        //折线颜色
        colour.add(Color.CYAN);
        colour.add(Color.GREEN);
        colour.add(Color.BLUE);

        dynamicLineChartManager1 = new DynamicLineChartManager(GYROSCOPE, names, colour);
        dynamicLineChartManager1.setDescription("陀螺仪数据");
        dynamicLineChartManager2 = new DynamicLineChartManager(ACCELEROMETER, names, colour);
        dynamicLineChartManager2.setDescription("加速度传感器数据");
    }
}
