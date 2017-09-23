package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker

/**
 * Created by 이은솔 on 2017-09-11.
 */
class OptionActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        Log.d("OptionActivity","'created'")

        //db를 다루기위한 dbHelper
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)
        //db가 생성될때 안에 들어있는지 확인
        Log.d("!!!!!!!!",dbHelper.getElementCount().toString())

        //activity_option에서 가져올 값
        //설정 체크
        val timeLimitChecked = findViewById(R.id.time_limit_checkBox) as CheckBox
        val selfieChecked = findViewById(R.id.selfie_upload_checkBox) as CheckBox
        val appLimitChecked = findViewById(R.id.application_limit_checkBox) as CheckBox
        //시간 제한
        val limitHour = findViewById(R.id.limit_hour) as EditText
        val limitMinute = findViewById(R.id.limit_minute) as EditText
        val limitSecond = findViewById(R.id.limit_second) as EditText
        //초기화 시각
        val timePicker = findViewById(R.id.timePicker) as TimePicker

        //DB에 넣을 데이터 배열
        var data = Array<Int>(8, {0})
        Log.d("OptionActivity","values initializing complete")

        //Setting Data있으면 데이터 가져와서 레이아웃 초기화 셋팅
        if(dbHelper.getElementCount() > 0){
            Log.d("layout initialize","DATA EXISTS")
            data = dbHelper.getSettings()//데이터 추출
            //레이아웃 설정
            timeLimitChecked.isChecked = if(data[0]>0) true else false
            selfieChecked.isChecked = if(data[1]>0) true else false
            appLimitChecked.isChecked = if(data[2]>0) true else false
            if(data[3] != 0) limitHour.setText(data[3].toString())
            if(data[4] != 0) limitMinute.setText(data[4].toString())
            if(data[5] != 0) limitSecond.setText(data[5].toString())
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                timePicker.hour = data[6]
                timePicker.minute = data[7]
            }else{
                timePicker.currentHour = data[6]
                timePicker.currentMinute = data[7]
            }
        }else{//Setting Data없으면 Data생성, 0으로 초기화
            Log.d("layout initialize","NO DATA")
            dbHelper.insertSettings(data)
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                timePicker.hour = 0
                timePicker.minute = 0
            }else{
                timePicker.currentHour = 0
                timePicker.currentMinute = 0
            }
        }
        Log.d("OptionActivity","layout initialzing complete")

        val okButton = findViewById(R.id.ok_button) as Button
        okButton.setOnClickListener{
            //값들 가져오기
            data[0] = if(timeLimitChecked.isChecked) 1 else 0
            data[1] = if(selfieChecked.isChecked) 1 else 0
            data[2] = if(appLimitChecked.isChecked) 1 else 0
            data[3] = if(limitHour.text.isBlank()) 0 else limitHour.text.toString().toInt()
            data[4] = if(limitMinute.text.isBlank()) 0 else limitMinute.text.toString().toInt()
            data[5] = if(limitSecond.text.isBlank()) 0 else limitSecond.text.toString().toInt()

            //버튼이 눌릴때 timePicker값을 가져와서 초기화
            val initializeHour: Int
            val initializeMinute: Int
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                initializeHour = timePicker.hour
                initializeMinute = timePicker.minute
            }else{
                initializeHour = timePicker.getCurrentHour()
                initializeMinute = timePicker.getCurrentMinute()
            }
            data[6] = initializeHour
            data[7] = initializeMinute
            Log.d("okButton"," button Clicked")
            //기존 데이터 삭제
            dbHelper.deleteSettings()
            //레이아웃에서 데이터 가져와서 DB에 삽입
            dbHelper.insertSettings(data)
            dbHelper.getSettings()
            this.finish()
        }

        val cancelButton = findViewById(R.id.cancel_button) as Button
        cancelButton.setOnClickListener{
            Log.d("cancelButton","cancel button clicked")
            //그냥 취소
            this.finish()
        }
    }
}