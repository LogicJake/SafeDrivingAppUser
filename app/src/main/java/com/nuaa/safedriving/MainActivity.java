package com.nuaa.safedriving;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.Calendar;
import java.util.TimeZone;
public class MainActivity extends AppCompatActivity {
    private Button start,over;
    private SensorManager sensorManager;
    SweetAlertDialog pDialog;
    JSONObject res = new JSONObject();
    JSONObject res2 = new JSONObject();
    JSONArray data = new JSONArray();
    JSONArray data2 = new JSONArray();
    String Region = null;
    boolean IsHave1 = false;
    boolean IsHave2 = false;
    private SharedPreferences preferences;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        //获取控件
        start = (Button)findViewById(R.id.start);
        over = (Button)findViewById(R.id.over);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);        //传感器
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.GONE);
                over.setVisibility(View.VISIBLE);
                startRecord();
            }
        });
        over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRecord();
            }
        });
    }

    //开始记录传感器数据
    public void startRecord(){
        String s = "缺少";

        if( sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null)
            s += "陀螺仪传感器";
        else{
            IsHave1 = true;
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),sensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
        if( sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null)
            s += ",加速度传感器";
        else{
            IsHave2 = true;
            sensorManager.registerListener(sensorEventListener2, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sensorManager.SENSOR_DELAY_NORMAL);
        }
        if(s.length() > 3){
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText(s);
            pDialog.setConfirmText("确定");
            pDialog.show();
        }

    }

    public void endRecord() {
        //注销监听器
        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(sensorEventListener2);
        AlertDialog.Builder selectRegion = new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_region,null);
        selectRegion.setTitle("根据图示选择座位位置");
        selectRegion.setView(dialogView);
        RadioGroup result = (RadioGroup) dialogView.findViewById(R.id.select);
        final RadioButton A = (RadioButton)dialogView.findViewById(R.id.A);
        final RadioButton B = (RadioButton)dialogView.findViewById(R.id.B);
        final RadioButton C = (RadioButton)dialogView.findViewById(R.id.C);
        final RadioButton D = (RadioButton)dialogView.findViewById(R.id.D);
        result.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == A.getId()) {
                    Region = "A";
                    Log.e("press","A");
                } else if (checkedId == B.getId()) {
                    Region = "B";
                    Log.e("press","B");

                } else if (checkedId == C.getId()) {
                    Region = "C";
                    Log.e("press","C");

                } else if (checkedId == D.getId()) {
                    Region = "D";
                    Log.e("press","D");

                }
            }
        });
        selectRegion.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Region!=null){
                    //提交
                    try {
                        if (IsHave1) {
                            res.put("region", Region);
                            res.put("data", data);
                            System.out.println(res.toString());
                            //提交
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    Boolean result = NewServices.collect("GYROSCOPE",preferences.getInt("id",0),res.toString());
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.obj = result;
                                    //handler.sendMessage(msg);
                                    res = new JSONObject();
                                    data = new JSONArray();

                                }
                            }).start();
                        }
                        if (IsHave2) {
                            res2.put("region", Region);
                            res2.put("data", data2);
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    Boolean result = NewServices.collect("ACCELEROMETER",preferences.getInt("id",0),res2.toString());                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.obj = result;
                                    //handler.sendMessage(msg);
                                    res2 = new JSONObject();
                                    data2 = new JSONArray();

                                }
                            }).start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Region = null;
                    start.setVisibility(View.VISIBLE);
                    over.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(MainActivity.this,"没有选择区域，请重新提交！",Toast.LENGTH_SHORT);
            }
        });
        selectRegion.show();

    }

    //传感器数据获取并保存
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
                tmp.put("time",""+timeStamp);
                tmp.put("x",""+x);
                tmp.put("y",""+y);
                tmp.put("z",""+z);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data.put(tmp);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener sensorEventListener2 = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; //
            float y = values[1]; //
            float z = values[2]; //
            long timeStamp = System.currentTimeMillis();
            JSONObject tmp = new JSONObject();
            try {
                tmp.put("time",""+timeStamp);
                tmp.put("x",""+x);
                tmp.put("y",""+y);
                tmp.put("z",""+z);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data2.put(tmp);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
