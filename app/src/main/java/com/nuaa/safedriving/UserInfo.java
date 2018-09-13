package com.nuaa.safedriving;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.ByteArrayInputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UserInfo extends Fragment {

    private LinearLayout setting;
    private CircleImageView avator;
    private SharedPreferences preferences;
    private TextView name;
    private SwipeRefreshLayout mswipeRefreshLayout;

    public UserInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.fragment_user_info, container, false);
        setting = (LinearLayout) view.findViewById(R.id.setting);
        avator = (CircleImageView) view.findViewById(R.id.avatar);
        name = (TextView) view.findViewById(R.id.name);
        mswipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        preferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        ImageLoaderConfiguration config =
            new ImageLoaderConfiguration.Builder(view.getContext()).build();
        ImageLoader.getInstance().init(config);

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(true)
            .cacheOnDisc(true).displayer(new FadeInBitmapDisplayer(300))
            .imageScaleType(ImageScaleType.EXACTLY).build();

        Bitmap avator_content = getBitmapFromSharedPreferences();
        if (avator_content != null) {
            avator.setImageBitmap(avator_content);
        }
        name.setText(preferences.getString("userName", "guest"));

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), setting.class);
                startActivity(intent);
            }
        });

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fragment fg = getFragmentManager().findFragmentByTag("userinfo");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.detach(fg);
                transaction.attach(fg);
                transaction.commit();
            }
        });
        return view;
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
}
