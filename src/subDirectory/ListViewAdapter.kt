package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by 이은솔 on 2017-09-17.
 * 출처//http://recipes4dev.tistory.com/43
 */
class ListViewAdapter: BaseAdapter(){
    var listViewItemList: ArrayList<ListViewItem> = ArrayList<ListViewItem>()
        private set

    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val pos: Int = position
        val context: Context? = parent?.context

        //"listview_item" layout을 inflate하여 converView획득
        if(convertView == null){
            val inflater: LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var notChangedView = convertView
            notChangedView = inflater.inflate(R.layout.listview_item, parent, false)
            return notChangedView
        }

        //화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
        val iconImageView: ImageView = convertView?.findViewById(R.id.image_view_item) as ImageView
        val nameTextView: TextView = convertView?.findViewById(R.id.text_view_app_name) as TextView
        val timeTextView: TextView = convertView?.findViewById(R.id.text_view_time) as TextView

        //Data Set(ListViewItem)에서 position에 위치한 데이타 참조 획득
        val listViewItem: ListViewItem = listViewItemList.get(position)

        //아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.iconDrawable)
        nameTextView.setText(listViewItem.appName)
        timeTextView.setText(listViewItem.accumulatedTime)

        return convertView
    }

    //지정 위치의 데이터와 관련된 아이템의 ID를 리턴
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //지정위치의 데이터 반환
    override fun getItem(position: Int): Any {
        return listViewItemList.get(position)
    }

    //아이템 데이터 추가를 위한 함수
    fun addItem(icon: Drawable, appName: String, acTime: String){
        var item: ListViewItem = ListViewItem()

        item.iconDrawable = icon
        item.appName = appName
        item.accumulatedTime = acTime

        listViewItemList.add(item)
    }
}