package com.nuaa.safedriving;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SCY on 2018/3/17/0017.
 */

public class FinishPopupWindow extends PopupWindow {
    private RatingBar star;
    private View mMenuView;
    private Button submit;
    private EditText suggestion;
    private float rate;
    private boolean isfirst = true;


    public FinishPopupWindow(final Activity context, final Handler handler) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_finsh, null);
        this.setContentView(mMenuView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        star = (RatingBar) mMenuView.findViewById(R.id.star);
        submit = (Button)mMenuView.findViewById(R.id.submit);
        suggestion = (EditText)mMenuView.findViewById(R.id.suggestion);

        star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate = rating;
                if (isfirst){
                    submit.setVisibility(View.VISIBLE);
                    suggestion.setVisibility(View.VISIBLE);
                    isfirst = false;
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String context = suggestion.getText().toString();
                Message msg = new Message();
                msg.what = 0;
                Bundle bundle=new Bundle();
                bundle.putFloat("rate", rate);
                bundle.putString("suggestion",context);
                msg.setData(bundle);
                handler.sendMessage(msg);
                dismiss();
            }
        });
    }

}
