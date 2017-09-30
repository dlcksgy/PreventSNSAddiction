package kr.ac.kau.sw.a2016125063.servicepr;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PracticeService2 extends Service {

    private long accumulatedTime = 0;

    private TextView usageText;

    private TimerTask task;

    private final Context context;


    public PracticeService2() {
        super();
        context = this;
    }




    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("tag:", "onCreate");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){


        Log.d("tag:", "onStartCommand");

        while(true){
            ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            componentInfo.getPackageName();

            try {
                Thread.sleep(1000);
            }catch (Exception ex){

            }


            if(false){
                break;
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}
