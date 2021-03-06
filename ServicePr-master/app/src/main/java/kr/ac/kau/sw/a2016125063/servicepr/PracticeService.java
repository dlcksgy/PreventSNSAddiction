package kr.ac.kau.sw.a2016125063.servicepr;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.content.Context;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PracticeService extends AccessibilityService {


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );

                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                if (isActivity)
                    Log.i("CurrentActivity", componentName.flattenToShortString());
            }
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {}
}








/*
    private long accumulatedTime = 0;

    private TextView usageText;

    private TimerTask task;

    private final Context context;


    public PracticeService() {
        super();
        context = this;
    }




    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        final ActivityManager am = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);

        String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
        Log.v(PracticeService.class.getName(), "Top Activity Name :: "+packageName);

        task = new TimerTask(){
            @Override
            public void run(){





/*코드1
                @SuppressWarnings("deprecation")
                List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(7);
                int size=info.size();
                Log.i("size", Integer.toString(size));
                //Log.i("size", info.get(0).topActivity.getPackageName());
                Log.i("normal", info.get(0).topActivity.getPackageName());
코드1/>
*/

/*
                CurrentApplicationPackageRetriever currentApplicationPackageRetriever = new CurrentApplicationPackageRetriever(getApplicationContext());
                for (String s : currentApplicationPackageRetriever.get()) {

                    Log.d("SEX", s);
                }

                List<ActivityManager.RunningTaskInfo> info;
                info = am.getRunningTasks(7);
                for (Iterator iterator = info.iterator(); iterator.hasNext();) {
                    ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) iterator.next();
                    Log.i("탴탴", am.getRunningTasks(1).get(0).topActivity.getPackageName());
                }
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
*/