package com.example.ayo.workmanager;

import android.arch.lifecycle.Observer;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import Worker.MyWorker;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {
    public static final String KEY = "KEY";

    private OneTimeWorkRequest request;
    private TextView textView;
    private Data data;
    private PeriodicWorkRequest periodicWorkRequest;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// this data is created with dataBuilder and will be set with the request
        data = new Data.Builder()
                .putString(KEY, "THIS IS THE DATA SENT")
                .build();


        //this constraint will be used in the work request
        Constraints constraints = new Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .build();

        Constraints chargingConstriant = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();
        PeriodicWorkRequest.Builder periodwork = new PeriodicWorkRequest.
                Builder(MyWorker.class, 1,
                TimeUnit.HOURS);

        //create a work request
        request = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInputData(data)
                .setConstraints(chargingConstriant)
                .build();

        periodicWorkRequest = periodwork
                .setConstraints(constraints)
                .build();

        sendNotify();


        textView = findViewById(R.id.info);





    }

    private void sendNotify() {
        //Work manager is what helps with performing the task in the workclass
        WorkManager.getInstance().enqueue(periodicWorkRequest);
        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        String statusReply = workInfo.getState()
                                .name();
                        textView.setText(statusReply + "\n");

                    }
                });
    }








    public void performRequest(View view) {
        WorkManager.getInstance().enqueue(request);
        //here we are getting livedata workinfo and observing it
        WorkManager.getInstance().getWorkInfoByIdLiveData(request.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {

                        if(workInfo != null){
                            if(workInfo.getState().isFinished()){
                                Data dataGot = workInfo.getOutputData();
                                String dataString = dataGot.getString(MyWorker.KEY_DATA_SENT);
                                textView.append(dataString + "\n");
                            }
                        }

                        String status = workInfo.getState().name();
                        textView.append(status + "\n");

                    }
                });
    }
}
