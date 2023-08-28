package com.xpel.flag.task;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //        getWindow().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        getWindow().addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        findViewById(R.id.button_bring_back).setOnClickListener(v -> {
            MainActivity.moveMainActivityToFrontIfNeeded();

            SecondActivity.this.finish();

            MainActivity.removeOtherTaskIfNeeded();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("SecondActivity onDestroy");
    }
}