package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

        //출처
        //http://blog.naver.com/pluulove84/100153350054
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
        }

        var list: ListView = findViewById(R.id.app_list) as ListView
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,appList)
        list.setAdapter(adapter)
    }
}
