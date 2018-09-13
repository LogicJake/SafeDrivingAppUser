package com.nuaa.safedriving;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static com.nuaa.safedriving.NewServices.picurl;

public class setting extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    super.handleMessage(msg);
                    int res = (int) msg.obj;
                    switch (res) {
                        case 200:
                            saveBitmapToSharedPreferences(
                                picurl + preferences.getInt("id", 0) + ".jpg");
                            pDialog.cancel();
                            break;
                        case 404:
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("出错了");
                            pDialog.setCancelable(true);
                            break;
                        case 0:
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("网络错误");
                            pDialog.setCancelable(true);
                            break;
                        case 1:
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("对不起，您上传的照片超过了1M");
                            pDialog.setCancelable(true);
                            break;
                        case 2:
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("文件上传发生错误");
                            pDialog.setCancelable(true);
                            break;
                        case 3:
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("不允许的扩展名");
                            pDialog.setCancelable(true);
                            break;
                    }
                    break;
                case 1:
                    super.handleMessage(msg);
                    res = (int) msg.obj;
                    if (res == 1) {
                        Toast.makeText(setting.this, "注销成功", Toast.LENGTH_SHORT).show();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(setting.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(setting.this, "注销失败", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(setting.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };
    private ImageView backup;
    private CircleImageView avator;
    private SelectPicPopupWindow menuWindow;
    private DisplayImageOptions displayImageOptions;
    private SharedPreferences preferences;
    private String url;
    private SweetAlertDialog pDialog;
    private TextView name;
    private SharedPreferences.Editor editor;
    private Button exit;
    private TextView mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        backup = (ImageView) findViewById(R.id.backup);
        avator = (CircleImageView) findViewById(R.id.avator);
        name = (TextView) findViewById(R.id.name);
        exit = (Button) findViewById(R.id.exit);
        mail = (TextView) findViewById(R.id.mail);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        ImageLoaderConfiguration config =
            new ImageLoaderConfiguration.Builder(setting.this).build();
        ImageLoader.getInstance().init(config);
        displayImageOptions = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(true)
            .cacheOnDisc(true).displayer(new FadeInBitmapDisplayer(300))
            .imageScaleType(ImageScaleType.EXACTLY).build();

        Bitmap avator_content = getBitmapFromSharedPreferences();
        if (avator_content != null) {
            avator.setImageBitmap(avator_content);
        }
        name.setText(preferences.getString("userName", "guest"));
        mail.setText(preferences.getString("email", "未设置"));

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        avator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow = new SelectPicPopupWindow(setting.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(setting.this.findViewById(R.id.pic),
                    BOTTOM | CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String token = preferences.getString("token", null);
                        int result = NewServices.logout(token);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    PictureSelector.create(setting.this)
                        .openCamera(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .freeStyleCropEnabled(true)
                        .isCamera(true)
                        .enableCrop(true)// 是否裁剪 true or false
                        .compress(true)// 是否压缩
                        .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                        .scaleEnabled(true)
                        .previewImage(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                    break;
                case R.id.btn_pick_photo:
                    PictureSelector.create(setting.this)
                        .openGallery(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .freeStyleCropEnabled(true)
                        .isCamera(true)
                        .enableCrop(true)// 是否裁剪 true or false
                        .compress(true)// 是否压缩
                        .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                        .scaleEnabled(true)
                        .previewImage(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                    break;
                default:
                    break;
            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocalMedia media;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    media = PictureSelector.obtainMultipleResult(data).get(0);
                    if (media.isCompressed()) {
                        url = media.getCompressPath();
                    } else {
                        url = media.getCutPath();
                        if (media.getCutPath() == null) {
                            url = media.getPath();
                        }
                    }
                    pDialog = new SweetAlertDialog(setting.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("上传头像中");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    ImageLoader.getInstance()
                        .displayImage("file:/" + url, avator, displayImageOptions);
                    uploadPic(url);
                    break;
            }
        }
    }

    public void uploadPic(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = preferences.getString("token", null);
                if (token != null) {
                    int res = NewServices.postPic(token, new File(url));
                    Message msg = new Message();
                    msg.obj = res;
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }
        }).start();
        PictureFileUtils.deleteCacheDirFile(setting.this);
    }

    private Bitmap getBitmapFromSharedPreferences() {
        Bitmap bitmap = null;
        String imageString = preferences.getString("avator", "null");
        if (imageString.equals("null")) {
            bitmap = null;
        } else {
            byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
        }
        return bitmap;
    }

    public void saveBitmapToSharedPreferences(String url) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().build();
        ImageLoader.getInstance()
            .loadImage(url, options, new SimpleImageLoadingListener() {    //加载存到本地
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    loadedImage.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String imageString =
                        new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
                    editor.putString("avator", imageString);
                    editor.commit();
                }
            });
    }
}
