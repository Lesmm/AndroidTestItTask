package com.xpel.flag.task;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    private ActivityResultLauncher launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.instance = this;

        // start a new task activity
        findViewById(R.id.button_start).setOnClickListener(v -> {
            Intent intent = new Intent(this, SecondActivity.class);
            //            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(intent);
        });

        // start a settings view
        findViewById(R.id.button_permission).setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });

        // start a photo picker view
        findViewById(R.id.button_pick_photo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 10086);
        });

        // start New activity result API
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                                             new ActivityResultCallback<ActivityResult>() {
                                                 @Override
                                                 public void onActivityResult(ActivityResult o) {
                                                     android.util.Log.d("~~~~~~~~", "onActivityResult: " + o);
                                                 }
                                             });
        findViewById(R.id.button_new_api).setOnClickListener(v -> {
            launcher.launch(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"));
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        android.util.Log.d("~~~~~~~~",
                           "onActivityResult. requestCode:" + requestCode + ", resultCode:" + resultCode + ", data:" + data);
    }

    /**
     * Static Methods
     */

    public static Activity getActivity() {
        return MainActivity.instance;
    }

    public static void removeOtherTaskIfNeeded() {
        try {
            List<ActivityManager.AppTask> tasks = ((ActivityManager) getActivity().getSystemService(
                    Context.ACTIVITY_SERVICE)).getAppTasks();
            if (tasks.size() > 1) {
                for (int i = 0; i < tasks.size(); i++) {
                    ActivityManager.AppTask task = tasks.get(i);
                    ActivityManager.RecentTaskInfo taskInfo = task.getTaskInfo();
                    ComponentName oName = taskInfo.origActivity;
                    ComponentName bName = taskInfo.baseIntent.getComponent();
                    boolean isQuickTask = false;
                    if (oName != null && oName.getClassName().equals(SecondActivity.class.getName())) {
                        isQuickTask = true;
                    } else if (bName != null && bName.getClassName().equals(SecondActivity.class.getName())) {
                        isQuickTask = true;
                    }
                    if (isQuickTask) {
                        task.finishAndRemoveTask();
                        break;
                    }
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void moveMainActivityToFrontIfNeeded() {
        try {
            List<ActivityManager.AppTask> tasks = ((ActivityManager) getActivity().getSystemService(
                    Context.ACTIVITY_SERVICE)).getAppTasks();
            if (tasks.size() > 1) {
                // 获取 MainActivity 所在的 task
                ActivityManager.AppTask mainTask = null;
                for (int i = 0; i < tasks.size(); i++) {
                    ActivityManager.AppTask task = tasks.get(i);
                    ActivityManager.RecentTaskInfo taskInfo = task.getTaskInfo();
                    ComponentName componentName = taskInfo.baseActivity;
                    if (componentName != null) {
                        if (componentName.getClassName().equals(MainActivity.class.getName())) {
                            mainTask = task;
                            break;
                        }
                    }
                }

                if (mainTask == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                    ActivityManager.RecentTaskInfo taskInfo = mainTask.getTaskInfo();
                    if (taskInfo != null) {
                        if (!taskInfo.isVisible()) {
                            // task.moveToFront();
                            moveToFront();
                            return;
                        }
                    }
                    return;
                }
                // task.moveToFront();
                moveToFront();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void moveToFront() {
        try {
            getActivity().startActivity(
                    new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}