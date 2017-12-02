package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import kr.ac.kau.sw.a2016125063.run.DialogAdapter
import kr.ac.kau.sw.a2016125063.run.DialogItem
import java.util.ArrayList

/**
 * Created by 이은솔 on 2017-09-11.
 */
class OptionActivity: AppCompatActivity() {
    //DB에 넣을 데이터 배열
    var data = Array<Int>(8, {0})
    //앱 제한 목록 배열
    companion object {
        var appLimitList = ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        Log.d("OptionActivity","'created'")

        //db를 다루기위한 dbHelper
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)
        //db가 생성될때 안에 들어있는지 확인
        Log.d("getSettingElementCount",dbHelper.getSettingElementCount().toString())

        //activity_option에서 가져올 값
        //설정 체크
        val timeLimitChecked = findViewById<CheckBox>(R.id.time_limit_checkBox)
        val selfieChecked = findViewById<CheckBox>(R.id.selfie_upload_checkBox)
        val appLimitChecked = findViewById<CheckBox>(R.id.application_limit_checkBox)
        //시간 제한
        val limitHour = findViewById<EditText>(R.id.limit_hour)
        val limitMinute = findViewById<EditText>(R.id.limit_minute)
        val limitSecond = findViewById<EditText>(R.id.limit_second)
        //초기화 시각
        val timePicker = findViewById<TimePicker>(R.id.timePicker)


        //Setting Data있으면 데이터 가져와서 레이아웃 초기화 셋팅
        if(dbHelper.getSettingElementCount() > 0){
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


        //앱 제한 리스트 뷰
        val appLimiting = findViewById<TextView>(R.id.time_limit_dialog)
        appLimiting.setOnClickListener {
            Log.d("setOnClickListener","app limit textview is clicked")
            createDialog()
        }
    }

    //옵션 설정을 하다가 앱을 종료해 버리면 저장되지 말아야할 시간이 저장됨.
    override fun onPause() {
        super.onPause()
        //db를 다루기위한 dbHelper
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)
        if(data[3] >= 24 || data[4] >= 60 || data[5] >= 60){//설정할 수 없는 시간
            //토스트창 띄우기
            //춮처// http://h5bak.tistory.com/92
            val toast: Toast = Toast.makeText(applicationContext, "설정할 수 없는 시간입니다.\n설정불가능 시간이 0으로 초기화 됩니다.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            //시간 초기화
            if(data[3] >= 24) data[3] = 0
            if(data[4] >= 60) data[4] = 0
            if(data[5] >= 60) data[5] = 0

            //기존 데이터 삭제
            dbHelper.deleteSettings()
            //데이터 가져와서 DB에 삽입
            dbHelper.insertSettings(data)
            dbHelper.getSettings()
        }
    }

    override fun onBackPressed() {//백버튼으로 값 저장하기
        //db를 다루기위한 dbHelper
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)
        //activity_option에서 가져올 값
        //설정 체크
        val timeLimitChecked = findViewById<CheckBox>(R.id.time_limit_checkBox)
        val selfieChecked = findViewById<CheckBox>(R.id.selfie_upload_checkBox)
        val appLimitChecked = findViewById<CheckBox>(R.id.application_limit_checkBox)
        //시간 제한
        val limitHour = findViewById<EditText>(R.id.limit_hour)
        val limitMinute = findViewById<EditText>(R.id.limit_minute)
        val limitSecond = findViewById<EditText>(R.id.limit_second)
        //초기화 시각
        val timePicker = findViewById<TimePicker>(R.id.timePicker)

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

        //백버튼 막기
        //출처// http://migom.tistory.com/14
        if(data[3] >= 24 || data[4] >= 60 || data[5] >= 60){//설정할 수 없는 시간
            //토스트창 띄우기
            //춮처// http://h5bak.tistory.com/92
            val toast: Toast = Toast.makeText(applicationContext, "설정할 수 없는 시간입니다.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }else{
            super.onBackPressed()
        }
    }

    //다이얼 로그를 만듬
    fun createDialog(){
        //db를 다루기위한 dbHelper
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)

        //설치된 패키지 목록 뽑기
        //출처//http://www.masterqna.com/android/23456/%EC%84%A4%EC%B9%98%EB%90%9C-%ED%8C%A8%ED%82%A4%EC%A7%80-%EB%AA%A9%EB%A1%9D-%EB%BD%91%EB%8A%94-%EB%B0%A9%EB%B2%95
        var appDataList = ArrayList<DialogItem>()//커스텀 리스트뷰에 사용
        var appDataPackageName = ArrayList<String>()//서비스에서 핸들러를 제어하는데 사용되는 패키지명
        val pm: PackageManager = getPackageManager()
        val packs = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for(app: ApplicationInfo in packs){
            val intent: Intent? = packageManager.getLaunchIntentForPackage(app.packageName)
            if(intent != null) {
                appDataList.add(DialogItem(app.loadIcon(pm), app.loadLabel(pm).toString()))
                appDataPackageName.add(app.packageName.toString())
                //Log.d("app list dialog",app.packageName.toString())
                print("app list dialog  "+app.packageName.toString()+"    ")
                println(app.loadLabel(pm).toString())
            }
        }
        Log.d("apps element count",appDataList.size.toString())

        //DB에 저장된 제한 앱 목록 불러오기
        val limitApps = dbHelper.getLimitation()

        //출처//https://stackoverflow.com/questions/13504781/custom-listview-inside-a-dialog-in-android
        //dialog창을  띄우기 위한 과정
        var inflater: LayoutInflater = layoutInflater
        val popUpLayout: View = inflater.inflate(R.layout.dialog_listview, null)

        val listView = popUpLayout.findViewById<ListView>(R.id.dialog_app_listview)
        val dialogAdapter = DialogAdapter(this, appDataList)
        listView.adapter = dialogAdapter

        //DB값을 이용해서 저장된 앱은 표시해주기
        var i: Int = 0; var k: Int = 0
        val limitAppSize = limitApps.size
        while(i < limitAppSize){
            if(limitApps[i] == appDataList[k].appName){//제한앱을 찾으면
                listView.setItemChecked(k, true)
                i+=1
            }
            k+=1
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("접근 제한 목록")
        builder.setPositiveButton("설정", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                val checkedItems = listView.checkedItemPositions
                val dataSize:Int = appDataList.size-1
                //limit table에 넣을 배열
                var limitList = ArrayList<String>()
                //limit table에 해당하는 패키지
                var limitPackageList = ArrayList<String>()
                for(i:Int in 0..dataSize){
                    if(checkedItems[i]){
                        limitList.add(appDataList[i].appName!!)
                        limitPackageList.add(appDataPackageName[i])
                        Log.d("checked Items --> ",appDataList[i].appName)
                    }
                }
                dbHelper.deleteLimitation()
                dbHelper.insertLimitation(limitList)
                appLimitList = limitPackageList //앱 제한 서비스에서 사용될 것.
                Log.d("limit app count",dbHelper.getLimitElementCount().toString())
            }
        })
        builder.setNegativeButton("취소", null)
        builder.setNeutralButton("초기화", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
            }
        })
        builder.setCancelable(false)
        builder.setView(popUpLayout)

        var dialog: AlertDialog = builder.create()
        //다이얼로그 바깥을 만졌을 때 창이 취소되는지
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        //다이얼로그 버튼 눌러도 종료 안되게 하기
        //출처// http://fullstatck.tistory.com/13
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                for(i:Int in 0..appDataList.size-1){
                    listView.setItemChecked(i, false)
                }
            }
        })
    }
}