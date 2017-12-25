package kr.ac.kau.sw.a2016125063.working

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_main.*
import android.app.AppOpsManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.app.Activity
import android.app.ActivityManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat.*
import android.support.v4.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private val CAMERA_REQUEST = 1
    private val CAMERA_PERMISSION_CODE = 2
    private val READ_EXTERNAL_STORAGE = 3


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
            val intent = Intent(applicationContext, UsagePermissionActivity::class.java)
            this.startActivity(intent)
        }


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }






        cameraButton.setOnClickListener {

            shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST)

        }

        serviceButton.setOnClickListener {
            Log.d("ServiceButton : ", "OnClickListener")
            val intent = Intent(applicationContext, UsageService::class.java)
            startService(intent)
        }


        appLockServiceButton.setOnClickListener {


            //canDrowOverlays 권한 확인
            // 출처 : https://stackoverflow.com/questions/40355344/how-to-programmatically-grant-the-draw-over-other-apps-permission-in-android
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
                startActivityForResult(intent, 0)
            }


            Log.d("LockButton : ", "OnClickListener")
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            CAMERA_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    fun isServiceRunningCheck(serviceName: String): Boolean {
        val manager = this.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName == service.service.getClassName()) {
                return true
            }
        }
        return false
    }

    //출처: http://itmir.tistory.com/326 [미르의 IT 정복기]
}
