package com.nuaa.safedriving;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nuaa.safedriving.model.HResult;
import org.json.JSONObject;

public class FullInEmail extends AppCompatActivity {

    private Button SendVerificationCode, save;
    private EditText email, VerificationCode;
    private SharedPreferences preferences;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int status = (int) msg.obj;
                    if (status == HResult.S_OK.getIndex()) {
                        System.out.println("success");
                        Toast.makeText(FullInEmail.this, "发送成功", Toast.LENGTH_SHORT);
                        CountDownTimer timer = new CountDownTimer(10 * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                SendVerificationCode.setText(millisUntilFinished / 1000 + "s");
                            }

                            @Override
                            public void onFinish() {
                                SendVerificationCode.setEnabled(true);
                                SendVerificationCode.setText("重新获取");
                            }
                        }.start();
                    } else {
                        System.out.println("fail");
                        SendVerificationCode.setEnabled(true);
                        Toast.makeText(FullInEmail.this, "发送失败", Toast.LENGTH_SHORT);
                    }
                    break;
                case 1:
                    status = (int) msg.obj;
                    if (status == HResult.S_OK.getIndex()) {
                        System.out.println("success");
                        Toast.makeText(FullInEmail.this, "验证成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FullInEmail.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        System.out.println("fail");
                        save.setEnabled(true);
                        Toast.makeText(FullInEmail.this, "验证失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_in_email);

        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SendVerificationCode = (Button) findViewById(R.id.SendVerificationCode);
        save = (Button) findViewById(R.id.save);
        email = (EditText) findViewById(R.id.email);
        VerificationCode = (EditText) findViewById(R.id.VerificationCode);
        SendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerificationCode.setEnabled(false);
                final String emailText = email.getText().toString().trim();
                if (emailText.matches(
                    "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?")
                    && emailText.length() > 0) {
                    final String token = preferences.getString("token", null);
                    if (token != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int res = 0;
                                res = NewServices.sendCode(emailText, token);
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = res;
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                } else {
                    Toast.makeText(FullInEmail.this, "无效的邮箱地址", Toast.LENGTH_SHORT).show();
                    SendVerificationCode.setEnabled(true);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setClickable(false);
                final String codeText = VerificationCode.getText().toString().trim();
                if (codeText.length() != 0) {
                    final String token = preferences.getString("token", null);
                    if (token != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int res = 0;
                                res = NewServices.verifyCode(codeText, token);
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = res;
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                } else {
                    save.setClickable(true);
                    Toast.makeText(FullInEmail.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}