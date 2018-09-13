package com.nuaa.safedriving;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONObject;

public class CheckCode extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pDialog.cancel();
                    int status = (int) msg.obj;
                    if (status == 1) {
                        System.out.println("success");
                        Toast.makeText(CheckCode.this, "发送成功", Toast.LENGTH_SHORT);
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
                        Toast.makeText(CheckCode.this, "发送失败", Toast.LENGTH_SHORT);
                    }
                    break;
                case 1:
                    status = (int) msg.obj;
                    if (status == 1) {
                        System.out.println("success");
                        Toast.makeText(CheckCode.this, "验证成功", Toast.LENGTH_SHORT).show();
                        part1.setVisibility(View.GONE);
                        part2.setVisibility(View.VISIBLE);
                    } else {
                        System.out.println("fail");
                        save.setClickable(true);
                        Toast.makeText(CheckCode.this, "验证失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Boolean res = (Boolean) msg.obj;
                    if (res) {
                        System.out.println("success");
                        Toast.makeText(CheckCode.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        System.out.println("fail");
                        Toast.makeText(CheckCode.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private Button SendVerificationCode, save;
    private EditText VerificationCode;
    private SharedPreferences preferences;
    private SweetAlertDialog pDialog;
    private TextView hint;
    private LinearLayout part1, part2;
    private EditText pass1, pass2;
    private Button sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_code);

        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SendVerificationCode = (Button) findViewById(R.id.SendVerificationCode);
        save = (Button) findViewById(R.id.save);
        VerificationCode = (EditText) findViewById(R.id.VerificationCode);
        hint = (TextView) findViewById(R.id.hint);
        part1 = (LinearLayout) findViewById(R.id.part1);
        part2 = (LinearLayout) findViewById(R.id.part2);
        pass1 = (EditText) findViewById(R.id.pass1);
        pass2 = (EditText) findViewById(R.id.pass2);

        final String token = preferences.getString("token", null);
        final String email = preferences.getString("email", null);

        hint.setText("验证码已发送到" + email);
        pDialog = new SweetAlertDialog(CheckCode.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("处理中");
        pDialog.setCancelable(false);

        sendCode(email, token);

        SendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerificationCode.setEnabled(false);
                if (token != null) {
                    sendCode(email, token);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setClickable(false);
                final String codeText = VerificationCode.getText().toString().trim();
                if (codeText.length() != 0) {
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
                    Toast.makeText(CheckCode.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sure = (Button) findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str1 = pass1.getText().toString();
                String str2 = pass2.getText().toString();
                if (!str1.equals(str2)) {
                    Toast.makeText(CheckCode.this, R.string.pass_no_same, Toast.LENGTH_SHORT)
                        .show();
                } else if (str1.length() == 0) {
                    Toast.makeText(CheckCode.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Boolean res = NewServices.ChangePass(token, str1);
                                Message msg = new Message();
                                msg.obj = res;
                                msg.what = 2;
                                handler.sendMessage(msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    private void sendCode(final String email, final String token) {
        pDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int res = 0;
                res = NewServices.sendCode(email, token);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = res;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
