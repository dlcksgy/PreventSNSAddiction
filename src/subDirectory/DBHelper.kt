package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Created by 이은솔 on 2017-09-20.
 * 출처: http://blog.naver.com/PostView.nhn?blogId=hee072794&logNo=220619425456
 * */
class DBHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {

    var data = Array<Int>(8,{0})

    override fun onCreate(db: SQLiteDatabase?) {
        //새로운 테이블 생성
        db?.execSQL(DataBases._CREATE_SETTINGS)
        Log.d("DBHelper","new table created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //기존 테이블 삭제, 새로 생성
        db?.execSQL("DROP TABLE IF EXISTS " + DataBases._TABLENAME_SETTINGS)
        Log.d("onUpgrade","table droped")
        onCreate(db)
        Log.d("onUpgrade","dbHelper onCreate ran")
    }

    fun insertSettings(data: Array<Int>){
        val db: SQLiteDatabase = writableDatabase //읽고쓰기가 가능하게 db열기
        //str에 Array에서 받아온 데이터를 넣어줌
        var str: String = "INSERT INTO "+DataBases._TABLENAME_SETTINGS+" VALUES("
        for(i:Int in 0..6){
            str=str+data[i].toString()+", "
        }
        str=str+data[7].toString()+");"
        Log.d("insert Settings",str)
        db.execSQL(str) //설정
        Log.d("insertSettings","data inserted")
        db.close()
    }

    fun deleteSettings(){
        val db: SQLiteDatabase = writableDatabase
        db.execSQL("DELETE FROM "+DataBases._TABLENAME_SETTINGS)
        Log.d("deleteSettings","settings deleted")
        db.close()
    }

    fun getSettings(): Array<Int>{
        var logString = ""
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_SETTINGS,null)
        while(cursor.moveToNext()){
            for(i:Int in 0..7){
                data[i] = cursor.getInt(i)
                logString = logString+data[i].toString()+" "
            }
        }
        Log.d("getSettings()",logString)
        return data
    }

    fun getElementCount(): Int{
        var count: Int = 0
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_SETTINGS,null)
        while(cursor.moveToNext()){
            for(i:Int in 0..7){
                count+=1
            }
        }
        return count
    }
}