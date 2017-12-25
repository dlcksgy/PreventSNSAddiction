package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Created by 이은솔 on 2017-09-20.
 * 출처: http://blog.naver.com/PostView.nhn?blogId=hee072794&logNo=220619425456
 * 새로운 앱을 다운로드 받았을 때 테이블에 추가하는 기능을 구현해야함
 * */
class DBHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {

    var data = Array<Int>(8,{0})
    var limitApps = ArrayList<String>()

    override fun onCreate(db: SQLiteDatabase?) {
        //새로운 테이블 생성
        db?.execSQL(DataBases._CREATE_SETTINGS)
        Log.d("DBHelper","settings table created")
        db?.execSQL(DataBases._CREATE_LIMIT)
        Log.d("DBHelper","limit table created")
        db?.execSQL(DataBases._CREATE_TIME)
        Log.d("DBHelper","time table created")
        db?.execSQL(DataBases._CREATE_HOUR_TIME)
        Log.d("DBHelper","hour time table created")
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
        //셋팅 데이블 내용 지우기
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
        db.close()
        return data
    }

    fun getSettingElementCount(): Int{
        var count: Int = 0
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_SETTINGS,null)
        while(cursor.moveToNext()){
            count+=1
        }
        db.close()
        return count
    }


    fun initializeTime(data: ArrayList<String>){
        //timeTable을 초기화 할때 처음에만 만듦
        val db: SQLiteDatabase = writableDatabase
        for(i: String in data){
            db.execSQL("INSERT INTO "+DataBases._TABLENAME_TIME+" VALUES('"+i+"', "+0+");")
        }
        Log.d("initializeTime","time initialized")
        db.close()
    }

    fun updateTime(data: Pair<String,Int>){
        //바뀐 시간들 업데이트
        val db: SQLiteDatabase = writableDatabase
        db.execSQL("UPDATE "+DataBases._TABLENAME_TIME+" SET "+
                DataBases.acTime+" = "+data.second+" WHERE "+DataBases.appNameTime+" = '"+data.first+"';")
        Log.d("updateTime","time changed sucessfully -> "+data.second.toString())
        db.close()
    }

    fun initializeAcTime(data: ArrayList<String>){
        //누적 시간을 초기화
       val db: SQLiteDatabase = writableDatabase
        for(i: String in data) {
            db.execSQL("UPDATE " + DataBases._TABLENAME_TIME + " SET " +
                    DataBases.acTime + " = " + 0 + " WHERE " + DataBases.appNameTime + " = '" + i + "';")
        }
        Log.d("initializeTime","trying")
        db.close()
    }

    fun getTime(pakageName: String): Int{
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT "+DataBases.acTime+" FROM "+DataBases._TABLENAME_TIME+
                " WHERE "+DataBases.appNameTime+" = '"+pakageName+"'",null)
        cursor.moveToNext()
        val time = cursor.getInt(0)
        db.close()
        return time
    }

    fun getTimeSum(): Int{//누적시간의 총합을 반환
        val db: SQLiteDatabase = readableDatabase
        var timeSum = 0
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_TIME,null)
        while(cursor.moveToNext()){
            timeSum += cursor.getInt(1)
        }
        db.close()
        return timeSum
    }

    fun getTimeElementCount(): Int{
        var count: Int = 0
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_TIME,null)
        while(cursor.moveToNext()){
            count+=1
        }
        db.close()
        return count
    }


    fun insertLimitation(data: ArrayList<String>){
        //limitTable을 초기화 할때 처음에만 만듦
        val db: SQLiteDatabase = writableDatabase
        for(i: String in data){
            db.execSQL("INSERT INTO "+DataBases._TABLENAME_LIMIT +" VALUES('"+i+"');")
        }
        Log.d("insertLimitation","limited apps inserted")
        db.close()
    }

    fun deleteLimitation(){
        //제한 데이블 내용 지우기
        val db: SQLiteDatabase = writableDatabase
        db.execSQL("DELETE FROM "+DataBases._TABLENAME_LIMIT)
        Log.d("deleteLimitation","limit table deleted")
        db.close()
    }

    fun getLimitation(): ArrayList<String>{
        //제한 테이블 목록 불러오기
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_LIMIT, null)
        while(cursor.moveToNext()){
            limitApps.add(cursor.getString(0))
            Log.d("getLimitApp",limitApps[limitApps.size-1])
        }
        db.close()
        return limitApps
    }

    fun getLimitElementCount(): Int{
        var count: Int = 0
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_LIMIT,null)
        while(cursor.moveToNext()){
            count+=1
        }
        db.close()
        return count
    }

    fun initializeHourTime(){
        val db: SQLiteDatabase = writableDatabase
        for(i: Int in 1..24){
            db.execSQL("INSERT INTO "+DataBases._TABLENAME_HOUR_TIME+" VALUES("+i+", "+0+");")
        }
        Log.d("initializeTime","time initialized")
        db.close()
    }

    fun getHourTimeElementCount(): Int{
        var count: Int = 0
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_HOUR_TIME,null)
        while(cursor.moveToNext()){
            count+=1
        }
        db.close()
        Log.d("getAllHourTime","")
        return count
    }

    fun getAllHourTime(): Array<Int>{
        val db: SQLiteDatabase = readableDatabase
        var cursor: Cursor = db.rawQuery("SELECT * FROM "+DataBases._TABLENAME_HOUR_TIME,null)
        var dataArray = Array<Int>(24, {i -> 0})
        var idx = 0
        while(cursor.moveToNext()){
            dataArray[idx] = cursor.getInt(1)
            idx += 1
        }
        db.close()
        Log.d("getAllHourTime","")
        return dataArray
    }

    fun updateHourTime(data: Pair<Int,Int>){
        //바뀐 시간들 업데이트
        val db: SQLiteDatabase = writableDatabase
        db.execSQL("UPDATE "+DataBases._TABLENAME_HOUR_TIME+" SET "+
                DataBases.hourTime+" = "+data.second+" WHERE "+DataBases.hour+" = "+data.first+";")
        db.close()
        Log.d("updateHourTime","time changed sucessfully -> "+data.second.toString())
    }
}