package kr.ac.kau.sw.a2016125063.preventsnsaddiction

/**
 * Created by 이은솔 on 2017-09-20.
 */
//출처: http://arabiannight.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9CAndroid-DB-%EC%83%9D%EC%84%B1-%EB%B0%8F-%EA%B4%80%EB%A6%AC-Cursor-Query
//DataBase Table
class DataBases{
    companion object{
        //옵션에 해당하는 값을 넣을 테이블
        val isTimeLimited = "isTimeLimited"
        val isSelfie = "isSelfie"
        val isAppLimited = "isAppLimited"
        val timeLimitHour = "timeLimitHour"
        val timeLimitMinute = "timeLimitMinute"
        val timeLimitsecond = "timeLimitsecond"
        val initializingHour = "initializingHour"
        val initializingMinute = "initializingMinute"
        val _TABLENAME_SETTINGS = "settingTable"
        val _CREATE_SETTINGS = "CREATE TABLE IF NOT EXISTS "+_TABLENAME_SETTINGS+
                "("+isTimeLimited+" INTEGER, "+isSelfie+" INTEGER, "+
                isAppLimited+" INTEGER, "+timeLimitHour+" INTEGER, "+
                timeLimitMinute+" INTEGER, "+timeLimitsecond+" INTEGER, "+
                initializingHour+" INTEGER, "+initializingMinute+" INTEGER);"

        //앱의 누적 시간을 넣을 테이블
        val appNameTime = "appName"
        val acTime = "accumulatedTime"
        val _TABLENAME_TIME = "timeTable"
        val _CREATE_TIME = "CREATE TABLE IF NOT EXISTS "+_TABLENAME_TIME+
                "("+appNameTime+" TEXT, "+acTime+" INTEGER);"

        //앱 제한 목록을 넣을 테이블
        val appName = "appName"
        val _TABLENAME_LIMIT = "limitTable"
        val _CREATE_LIMIT = "CREATE TABLE IF NOT EXISTS "+ _TABLENAME_LIMIT +
                "("+appName+" TEXT);"

        //1시간 간격으로 사용된 시간을 넣을 테이블
        val hour = "hour"
        val hourTime = "hourTime"
        val _TABLENAME_HOUR_TIME = "hourTimeTable"
        val _CREATE_HOUR_TIME = "CREATE TABLE IF NOT EXISTS "+ _TABLENAME_HOUR_TIME +
                "("+ hour + " INTEGER, " + hourTime+" INTEGER);"
    }
}