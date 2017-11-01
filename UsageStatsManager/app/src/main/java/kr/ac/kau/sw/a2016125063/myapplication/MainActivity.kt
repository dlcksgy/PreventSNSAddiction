package kr.ac.kau.sw.a2016125063.myapplication

import android.app.AppOpsManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context.APP_OPS_SERVICE
import android.R.attr.mode
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.support.v4.content.PermissionChecker.checkCallingOrSelfPermission
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.data




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraButton.setOnClickListener(View.OnClickListener {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,1)

        })

        serviceButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, UsageService::class.java)
            startService(intent)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        iv.setImageURI(data!!.data)
        
    }
}
