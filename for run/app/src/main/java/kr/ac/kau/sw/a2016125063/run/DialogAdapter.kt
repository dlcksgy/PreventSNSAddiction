package kr.ac.kau.sw.a2016125063.run

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kr.ac.kau.sw.a2016125063.preventsnsaddiction.ListViewItem
import kr.ac.kau.sw.a2016125063.preventsnsaddiction.R

/**
 * Created by 이은솔 on 2017-11-05.
 */
class DialogAdapter(items: ArrayList<DialogItem>): BaseAdapter(){
    var dialogItemList: ArrayList<DialogItem> = items
       // private set

    override fun getCount(): Int {
        return dialogItemList.size
    }

    //지정 위치의 데이터와 관련된 아이템의 ID를 리턴
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //지정위치의 데이터 반환
    override fun getItem(position: Int): Any {
        return dialogItemList.get(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val pos: Int = position
        val context: Context? = parent?.context

        //"dialog_item" layout을 inflate하여 converView획득
        if(convertView == null){
            val inflater: LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var notChangedView = convertView
            notChangedView = inflater.inflate(R.layout.dialog_item, parent, false)
            return notChangedView
        }

        //화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
        val iconImageView: ImageView = convertView?.findViewById<ImageView>(R.id.dialog_item_image_view)
        val nameTextView: TextView = convertView?.findViewById<TextView>(R.id.dialog_item_app_name)

        //Data Set(dialogItem)에서 position에 위치한 데이타 참조 획득
        val dialogItem: DialogItem = dialogItemList.get(position)

        //아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(dialogItem.iconDrawable)
        nameTextView.text = dialogItem.appName

        return convertView
    }
}