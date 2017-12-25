package kr.ac.kau.sw.a2016125063.working


import android.app.Service
import android.content.Intent
import android.graphics.Camera
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log


/**
 * Created by Arduino on 2017-11-02.
 */
class CameraService : Service(){

    val task = object : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            Thread.sleep(2000)
            val intent = Intent(applicationContext, CameraActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return null
        }

    }



    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("CAmeraService","onCreate")
        task.execute()



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

}