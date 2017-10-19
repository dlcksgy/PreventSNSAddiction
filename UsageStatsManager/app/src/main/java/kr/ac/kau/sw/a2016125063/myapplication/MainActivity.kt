package kr.ac.kau.sw.a2016125063.myapplication

import android.app.AppOpsManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context.APP_OPS_SERVICE
import android.R.attr.mode
import android.content.pm.PackageManager
import android.support.v4.content.PermissionChecker.checkCallingOrSelfPermission





class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
