package Worker;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.ayo.workmanager.MainActivity;
import com.example.ayo.workmanager.R;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public static final String KEY_DATA_SENT = "key_data_sent";
    //here we create a constructor for the class thats has the context and WorkerParameters as the
    //parameter
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }



    /*
     * This method is responsible for doing the work
     * so whatever work that is needed to be performed
     * we will put it here
     *
     * For example, here I am calling the method displayNotification()
     * It will display a notification
     * So that we will understand the work is executed
     * */

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    @Override
    public Result doWork() {

        //here we get data sent from the user activty
        Data data = getInputData();

        String key = data.getString(MainActivity.KEY);
        displayNotification("MY NOTIFICATION", key +
                " " + "I have finished My work");
    //this is used to send data from the operation in the worker class
        Data sent = new Data.Builder()
                .putString(KEY_DATA_SENT, "Data sent Successfully")
                .build();

        //this data sent here can be read in workinfo
        setOutputData(sent);

        return Result.SUCCESS;
    }
    /*
     * The method is doing nothing but only generating
     * a simple notification
     * If you are confused about it
     * you should check the Android Notification Tutorial
     * */


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void displayNotification(String title, String data) {

        NotificationCompat.Builder notification = new NotificationCompat
                .Builder(getApplicationContext(),
                "lovisgod")
                .setContentTitle(title)
                .setContentText(data)
                .setSmallIcon(R.mipmap.ic_launcher);


        /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);

        /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().
                        getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("lovisgod",
                    "lovisgod", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }



        notificationManager.notify(1, notification.build());
    }
}
