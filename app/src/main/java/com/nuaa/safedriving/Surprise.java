package com.nuaa.safedriving;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.Random;

public class Surprise extends AppCompatActivity {
    private int num = 3;
    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise);
        layout = (LinearLayout)findViewById(R.id.layout);
        Random random = new Random();
        int i = random.nextInt(num)+1;
        System.out.println(i);
        String name = "p"+i;
        int resId = getResources().getIdentifier(name, "drawable", "com.nuaa.safedriving");
        layout.setBackgroundResource(resId);
    }
}
