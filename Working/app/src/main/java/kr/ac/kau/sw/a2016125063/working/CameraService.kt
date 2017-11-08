package kr.ac.kau.sw.a2016125063.working


import android.app.Service
import android.content.Intent
import android.graphics.Camera
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.IBinder



/**
 * Created by Arduino on 2017-11-02.
 */
class CameraService : Service(){
    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)


    }

}