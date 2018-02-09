package com.nuaa.safedriving;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Ride extends AppCompatActivity {

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

    //重力传感器
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
    //加速度传感器
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        backup = (ImageView)findViewById(R.id.backup);
        finsh = (Button)findViewById(R.id.finish);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);        //传感器
        chooseSeat();
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        finsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ride.this, Surprise.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void StartRecord() {     //注册传感器
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            isHave1 = true;
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), sensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            isHave2 = true;
            sensorManager.registerListener(sensorEventListener2, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void chooseSeat(){
        final AlertDialog.Builder selectRegion = new AlertDialog.Builder(Ride.this);
        final View dialogView = LayoutInflater.from(Ride.this).inflate(R.layout.dialog_region,null);
        selectRegion.setTitle("根据图示选择座位位置");
        selectRegion.setCancelable(false);
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
                if(Region == null) {
                    Toast.makeText(Ride.this, "没有选择区域，请重新提交！", Toast.LENGTH_SHORT).show();
                    chooseSeat();
                }
                else
                    StartRecord();          //一切正常采集数据
            }
        });
        selectRegion.show();
    }
}
