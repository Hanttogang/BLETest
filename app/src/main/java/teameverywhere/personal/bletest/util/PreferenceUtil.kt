package teameverywhere.personal.bletest.util

import android.content.Context
import android.content.SharedPreferences


class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    //앱 설치 후 첫 시작인지
    fun getIsFirstStart(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setIsFirstStart(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //디바이스 이름 저장
    fun getString(key: String, defValue: String): String{
        return prefs.getString(key, defValue).toString()
    }
    fun setString(key: String, str: String){
        prefs.edit().putString(key, str).apply()
    }

    fun getSensitiveIsChecked(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }
    fun setSensitiveIsChecked(key: String, defValue: String){
        prefs.edit().putString(key, defValue).apply()
    }

    fun getSensitive(key: String, defValue: Float): Float{
        return prefs.getFloat(key, defValue)
    }
    fun setSensitive(key: String, defValue: Float){
        prefs.edit().putFloat(key, defValue).apply()
    }

    //영/유아, 고령자
    fun getPersonIsChecked(key: String, defValue: String): String{
        return prefs.getString(key, defValue).toString()
    }
    fun setPersonIsChecked(key: String, defValue: String){
        prefs.edit().putString(key, defValue).apply()
    }

    //오토스타트
    fun getIsAutoStart(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setIsAutoStart(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //온도센서
    fun getIsTempSensorStart(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setIsTempSensorStart(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //진동모드(2022-12-21 현재 사용안함)
    fun getIsVibrationStart(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setIsVibrationStart(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //알림음 반복 횟수
    fun getSoundCount(key: String, defValue: Int): Int{
        return prefs.getInt(key, defValue)
    }
    fun setSoundCount(key: String, defValue: Int){
        prefs.edit().putInt(key, defValue).apply()
    }

    //언어
    fun getLanguage(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setLanguage(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 활성화
    fun getDoNotDisturb(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setDoNotDisturb(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 시작 시간
    fun getStartTimeDoNotDisturb(key: String, defValue: Int): Int{
        return prefs.getInt(key, defValue)
    }
    fun setStartTimeDoNotDisturb(key: String, defValue: Int){
        prefs.edit().putInt(key, defValue).apply()
    }

    //방해금지시간 시작 분
    fun getStartMinuteDoNotDisturb(key: String, defValue: Int): Int{
        return prefs.getInt(key, defValue)
    }
    fun setStartMinuteDoNotDisturb(key: String, defValue: Int){
        prefs.edit().putInt(key, defValue).apply()
    }

    //방해금지시간 종료 시간
    fun getEndTimeDoNotDisturb(key: String, defValue: Int): Int{
        return prefs.getInt(key, defValue)
    }
    fun setEndTimeDoNotDisturb(key: String, defValue: Int){
        prefs.edit().putInt(key, defValue).apply()
    }

    //방해금지시간 종료 분
    fun getEndMinuteDoNotDisturb(key: String, defValue: Int): Int{
        return prefs.getInt(key, defValue)
    }
    fun setEndMinuteDoNotDisturb(key: String, defValue: Int){
        prefs.edit().putInt(key, defValue).apply()
    }

    //방해금지시간 매일
    fun getEveryDay(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setEveryDay(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 월
    fun getMonday(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setMonday(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 화
    fun getTuesday(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setTuesday(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 수
    fun getWednesday(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setWednesday(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 목
    fun getThursday(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setThursday(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 금
    fun getFriday(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setFriday(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 토
    fun getSaturday(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setSaturday(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //방해금지시간 일
    fun getSunday(key: String, defValue: Boolean): Boolean{
        return prefs.getBoolean(key, defValue)
    }
    fun setSunday(key: String, defValue: Boolean){
        prefs.edit().putBoolean(key, defValue).apply()
    }

    //Notification Sound
    fun getSoundName(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }
    fun setSoundName(key: String, defValue: String){
        prefs.edit().putString(key, defValue).apply()
    }
}