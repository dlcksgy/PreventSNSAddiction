package kr.ac.kau.sw.a2016125063.run

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.Checkable
import android.widget.LinearLayout
import kr.ac.kau.sw.a2016125063.preventsnsaddiction.R

/**
 * Created by 이은솔 on 2017-11-07.
 */

//출처//http://recipes4dev.tistory.com/68
//리스트뷰 아이템이 클릭되도 CheckBox의 표시가 바뀌게 하는데 필요한 아이템 레이아웃
class CheckableLinearLayout(context: Context, attribute: AttributeSet):LinearLayout(context),Checkable{
    override fun isChecked(): Boolean {
        val checkBox = findViewById<CheckBox>(R.id.dialog_checkBox)
        return checkBox.isChecked
    }

    override fun toggle() {
        val checkBox = findViewById<CheckBox>(R.id.dialog_checkBox)
        checkBox.isChecked = !checkBox.isChecked
    }

    override fun setChecked(p0: Boolean) {
        val checkBox = findViewById<CheckBox>(R.id.dialog_checkBox)
        if(checkBox.isChecked != p0){
            checkBox.isChecked = p0
        }
    }
}