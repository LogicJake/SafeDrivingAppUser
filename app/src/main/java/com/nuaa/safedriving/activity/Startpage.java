package com.nuaa.safedriving.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nuaa.safedriving.NewServices;
import com.nuaa.safedriving.R;
import com.nuaa.safedriving.model.HResult;
import org.json.JSONException;
import org.json.JSONObject;

public class Startpage extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            JSONObject result = (JSONObject) msg.obj;
            if (result != null) {
                int status = -1;
                String token = null;
                String email = null;
                try {
                    status = result.getInt("hr");
                    token = result.getJSONObject("data").getString("token");
                    email = result.getJSONObject("data").getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status == HResult.S_OK.getIndex()) {      //登陆成功，获取最新的token
                    editor.putString("token", token);
                    editor.putString("email", email);
                    editor.commit();
                    Intent intent = new Intent(Startpage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Startpage.this, Login.class);     //登陆界面
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(Startpage.this, Login.class);     //登陆界面
                startActivity(intent);
                finish();
            }
        }
    };

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();

        final String name = preferences.getString("userName", null);
        final String password = preferences.getString("userPassword", null);
        if (name == null || password == null) {       //打开登陆界面
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Startpage.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        } else {                                    //自动登陆
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject result = NewServices.login(name, password);
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }
}
