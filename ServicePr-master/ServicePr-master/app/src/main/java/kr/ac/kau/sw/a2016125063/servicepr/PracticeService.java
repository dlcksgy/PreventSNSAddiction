package kr.ac.kau.sw.a2016125063.servicepr;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PracticeService extends Service {

    private long accumulatedTime = 0;

    private TextView usageText;

    private TimerTask task;


    public PracticeService() {
        super();
    }




    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        @SuppressWarnings("deprecation")
        final ActivityManager activityManager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        task = new TimerTask(){
            @Override
            public void run(){
                @SuppressWarnings("deprecation")
                List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(7);
                int size=info.size();
                Log.i("size", Integer.toString(size));
                //Log.i("size", info.get(0).topActivity.getPackageName());
                Log.i("normal", info.get(0).topActivity.getPackageName());

/*
                 List<ActivityManager.RunningTaskInfo> info;
                info = activityManager.getRunningTasks(7);
                for (Iterator iterator = info.iterator(); iterator.hasNext();) {
                    ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) iterator.next();
                    Log.i("탴탴", activityManager.getRunningTasks(1).get(0).topActivity.getPackageName());
                }
*/
                accumulatedTime++;
                long min = accumulatedTime/60;
                long sec = accumulatedTime%60;
                Intent intent = new Intent();
                intent.putExtra("min", min);
                intent.putExtra("sec", sec);
                String msg = String.format("%d분 %d초",min, sec);

                Log.i("타이머테스트",msg);
                sendBroadcast(intent);

            }
        };
        Timer timer = new Timer();
        timer.schedule(task,0,1000);
// 3초후 run을 실행하고 종료 timer.schedule(task, 3000);
// 10초후 run을 실행하고 매3초마다 실행 timer.schedule(task, 10000, 3000);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}
