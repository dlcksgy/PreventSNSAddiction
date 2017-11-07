package kr.ac.kau.sw.a2016125063.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context.APP_OPS_SERVICE
import android.R.attr.mode
import android.content.Intent
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.data
import android.util.Log
import kr.ac.kau.sw.a2016125063.myapplication.R.id.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

<<<<<<< HEAD
        cameraButton.setOnClickListener{
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
=======
        val intent = Intent(applicationContext, UsageService::class.java)
        startService(intent)

        cameraButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
>>>>>>> b70ac9d3ecad3e085efe81e188caad661659558e
            startActivityForResult(intent,1)

        }

        serviceButton.setOnClickListener{
            Log.d("ServiceButton : ","OnClickListener")
            val intent = Intent(applicationContext, UsageService::class.java)
            startService(intent)
        }


        appLockServiceButton.setOnClickListener{
            Log.d("LockButton : ","OnClickListener")
            val intent = Intent(applicationContext, ActivityManagerService::class.java)
            startService(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        iv.setImageURI(data!!.data)
        
    }
}
