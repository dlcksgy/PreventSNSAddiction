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
class ListViewAdapter(context: Context, items: ArrayList<ListViewItem>): BaseAdapter(){
    var listViewItemList: ArrayList<ListViewItem> = items
        private set

    val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return listViewItemList.size
    }

    //지정 위치의 데이터와 관련된 아이템의 ID를 리턴
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //지정위치의 데이터 반환
    override fun getItem(position: Int): Any {
        return listViewItemList.get(position)
    }
    //bug catch 안드로이드 시간의 listView 가져옴. 비효율적이지만 버그 잡음
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = mInflater.inflate(R.layout.listview_item, parent, false)

        //화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
        val iconImageView: ImageView = rowView.findViewById<ImageView>(R.id.image_view_item)
        val nameTextView: TextView = rowView.findViewById<TextView>(R.id.text_view_app_name)
        val timeTextView: TextView = rowView.findViewById<TextView>(R.id.text_view_time)

        //Data Set(ListViewItem)에서 position에 위치한 데이타 참조 획득
        val listViewItem: ListViewItem = listViewItemList.get(position)

        //아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.iconDrawable)
        nameTextView.text = listViewItem.appName
        timeTextView.text = listViewItem.accumulatedTime

        return rowView
    }
}