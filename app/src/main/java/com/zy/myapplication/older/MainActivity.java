package com.zy.myapplication.older;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zy.myapplication.R;

public class MainActivity extends AppCompatActivity {

    private ZSlideLayout zSlideLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);
        setTitle("ZYSlidingLayout");

        zSlideLayout = (ZSlideLayout) findViewById(R.id.activity_main_slidemenu);
    }

    public void leftMenuClick(View view) {
        String tips = "";
        try {
            tips = ((Button) view).getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(view.getContext(), "click left menu " + tips, Toast.LENGTH_SHORT).show();
    }

    public void rightMenuClick(View view) {
        String tips = "";
        try {
            tips = ((Button) view).getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(view.getContext(), "click right menu " + tips, Toast.LENGTH_SHORT).show();
    }
}
