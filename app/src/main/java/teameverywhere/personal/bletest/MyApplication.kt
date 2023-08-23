package teameverywhere.personal.bletest

import android.app.Application
import teameverywhere.personal.bletest.util.PreferenceUtil


class MyApplication: Application() {

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        super.onCreate()

        prefs = PreferenceUtil(applicationContext)
    }
}