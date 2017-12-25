package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import kotlinx.android.synthetic.main.activity_usage_permission.*
import kr.ac.kau.sw.a2016125063.preventsnsaddiction.R

class UsagePermissionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_usage_permission)

        ok_button.setOnClickListener({ v ->

            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            this.startActivity(intent)
            finish()
        })

    }


    //권한확인을 받지 않으면 앱을 강제종료시킨다.
    override fun onBackPressed() {


        moveTaskToBack(true)
        android.os.Process.killProcess(android.os.Process.myPid())

        //출처: http://codeticker.tistory.com/entry/Android-안드로이드-어플-종료시키기 [CodeTicker]
        super.onBackPressed()
    }
}