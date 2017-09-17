package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val optionButton = findViewById(R.id.option_button) as Button
        optionButton.setOnClickListener{
            val intent = Intent(this@MainActivity, OptionActivity::class.java)
            startActivity(intent)
        }

        //출처//http://recipes4dev.tistory.com/43
        //listView 설정
        var listView: ListView = findViewById(R.id.app_list) as ListView
        //어뎁터 생성
        var listViewAdapter: ListViewAdapter = ListViewAdapter()
        //어뎁터 설정
        listView.adapter = listViewAdapter
        //아이템 추가
        //listViewAdapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_action_name),"00:00:01")

        var adapterList = ArrayList<ListViewAdapter>()

        //내 핸드폰 내의 앱 리스트 받아오기(앱 아이콘,패키지명,이름)
        //출처//http://blog.naver.com/pluulove84/100153350054
        var appList = ArrayList<String>()
        var pm: PackageManager = getPackageManager()
        var packs = pm.getInstalledApplications(PackageManager.GET_DISABLED_COMPONENTS)
        //var appInfo: ApplicationInfo? = null

        for(app:ApplicationInfo in packs){
            //appInfo = ApplicationInfo()
            //appInfo.loadIcon(pm)
            //appInfo.mAppName = app.loadLabel(pm)
            //appInfo.packageName
            Log.d("onCreate",app.packageName.toString())
            appList.add(app.loadLabel(pm).toString())


            //Log.d("Main","adapter")
            listViewAdapter.addItem(app.loadIcon(pm), app.loadLabel(pm).toString(), "00:00:01")
            //Log.d("Main","add")
            adapterList.add(listViewAdapter)
            //Log.d("Main","complete")
        }

        listViewAdapter.notifyDataSetChanged()
        var list: ListView = findViewById(R.id.app_list) as ListView
        //var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,appList)
        //list.setAdapter(adapter)
        //var adapter = ArrayAdapter(this, R.layout.listview_item, adapterList) 에러발생
        //var adapter = ArrayAdapter(this, R.layout.listview_item, listViewAdapter.listViewItemList)
        //list.setAdapter(adapter)
    }
}
