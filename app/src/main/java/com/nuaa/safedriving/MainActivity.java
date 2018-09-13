package com.nuaa.safedriving;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Home home;
    private UserInfo userInfo;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    initHome();
                    return true;
                case R.id.navigation_car:
                    Intent intent = new Intent(MainActivity.this, Ride.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_info:
                    initUserInfo();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState != null) { // “内存重启”时调用
            initHome();
        } else {      //正常启动时调用
            initHome();
        }
    }

    private void initHome() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (home == null) {
            home = new Home();
            transaction.add(R.id.content, home, "home");
        }
        hideFragment(transaction);
        transaction.show(home);
        transaction.commit();
    }

    private void initUserInfo() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (userInfo == null) {
            userInfo = new UserInfo();
            transaction.add(R.id.content, userInfo, "userinfo");
        }
        hideFragment(transaction);
        transaction.show(userInfo);
        transaction.commit();
    }

    public void hideFragment(FragmentTransaction transaction) {
        if (home != null) {
            transaction.hide(home);
        }
        if (userInfo != null) {
            transaction.hide(userInfo);
        }
    }
}
