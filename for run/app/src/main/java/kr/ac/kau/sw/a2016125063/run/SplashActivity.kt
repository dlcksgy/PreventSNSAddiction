package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {
    //로딩 화면 만들기
    //출처//http://dudmy.net/android/2017/04/09/improved-loading-screen/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        this.finish()
    }
}
