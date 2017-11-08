package kr.ac.kau.sw.a2016125063.working

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context.APP_OPS_SERVICE
import android.R.attr.mode
import android.content.Intent
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.data
import android.app.AppOpsManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import kr.ac.kau.sw.a2016125063.working.ActivityManagerService
import kr.ac.kau.sw.a2016125063.working.UsageService
import kr.ac.kau.sw.a2016125063.working.R.id.*
import java.util.jar.Manifest
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.app.Activity
import android.annotation.TargetApi
import android.net.Uri
import android.provider.Settings


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // GET_USAGE_STATS 권한 확인
        var granted = false
        val appOps = this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), this.getPackageName())

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = this.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) === PackageManager.PERMISSION_GRANTED
        } else {
            granted = mode == AppOpsManager.MODE_ALLOWED
        }

        Log.e(ContentValues.TAG, "===== CheckPhoneState isRooting granted = " + granted)

        if (granted == false) {
            // 권한이 없을 경우 권한 요구 페이지 이동
            val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
            this.startActivity(intent)
        }





        cameraButton.setOnClickListener{
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,1)

        }

        serviceButton.setOnClickListener{
            Log.d("ServiceButton : ","OnClickListener")
            val intent = Intent(applicationContext, UsageService::class.java)
            startService(intent)
        }


        appLockServiceButton.setOnClickListener{


            //canDrowOverlays 권한 확인
            // 출처 : https://stackoverflow.com/questions/40355344/how-to-programmatically-grant-the-draw-over-other-apps-permission-in-android
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
                startActivityForResult(intent, 0)
            }


            Log.d("LockButton : ","OnClickListener")
            val intent = Intent(applicationContext, AppLockService::class.java)
            startService(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        iv.setImageURI(data!!.data)

    }


    fun requestOverlayPermission(activity: Activity) {
//        activity.startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), PermissionUtil.PERMISSION_OVERLAY)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {


    }


}
