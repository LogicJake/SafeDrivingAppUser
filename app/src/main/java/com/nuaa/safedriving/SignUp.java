package com.nuaa.safedriving;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nuaa.safedriving.model.HResult;
import org.json.JSONException;
import org.json.JSONObject;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUp extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject result = (JSONObject) msg.obj;
            if (result == null) {
                pDialog.cancel();
                Toast.makeText(SignUp.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            } else {
                int status = 0;
                try {
                    status = result.getInt("hr");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status == HResult.S_OK.getIndex()) {
                    String token = null;
                    try {
                        token = result.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pDialog.cancel();
                    editor.putString("token", token);
                    editor.commit();
                    Toast.makeText(SignUp.this, R.string.sign_up_success, Toast.LENGTH_SHORT)
                        .show();
                    Intent intent = new Intent(SignUp.this, FullInEmail.class);
                    startActivity(intent);
                    finish();
                } else if (status == HResult.E_DUPLICATED_USERNAME.getIndex()) {
                    pDialog.cancel();
                    Toast.makeText(SignUp.this, R.string.name_has_exist, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private Button bt_sign_up;
    private EditText editName, editPassword, editPassword2;
    private SweetAlertDialog pDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editName = (EditText) findViewById(R.id.name);
        editPassword = (EditText) findViewById(R.id.password);
        editPassword2 = (EditText) findViewById(R.id.password2);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        bt_sign_up = (Button) findViewById(R.id.signup);
        bt_sign_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pDialog = new SweetAlertDialog(SignUp.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText(getString(R.string.pushing_info));
                pDialog.setCancelable(false);
                pDialog.show();
                try {
                    final String name = editName.getText().toString();
                    final String password = editPassword.getText().toString();
                    String password2 = editPassword2.getText().toString();
                    if (!password.equals(password2)) {             //两次密码不一致
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText(getString(R.string.ERROR));
                        pDialog.setContentText(getString(R.string.pass_no_same));
                        pDialog.setConfirmText(getString(R.string.OK));
                        pDialog.setCancelable(false);
                        pDialog.show();
                    } else if (name.length() == 0) {
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText(getString(R.string.ERROR));
                        pDialog.setContentText(getString(R.string.name_no_empty));
                        pDialog.setConfirmText(getString(R.string.OK));
                        pDialog.setCancelable(false);
                        pDialog.show();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject res = NewServices.signup(name, password);
                                    Message msg = new Message();
                                    msg.obj = res;
                                    handler.sendMessage(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
