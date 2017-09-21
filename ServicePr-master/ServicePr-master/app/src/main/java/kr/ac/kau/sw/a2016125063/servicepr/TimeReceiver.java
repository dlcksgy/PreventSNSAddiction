package kr.ac.kau.sw.a2016125063.servicepr;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Arduino on 2017-07-18.
 */

public class TimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            ComponentName componentName = new ComponentName(context.getPackageName(), PracticeService.class.getName());
        }


    }
}
