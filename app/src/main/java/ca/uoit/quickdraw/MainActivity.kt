package ca.uoit.quickdraw

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    lateinit var objects: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        objects = resources.getStringArray(R.array.objects)
    }

    fun onPressPlay(view: View) {
        val prefs =  getSharedPreferences("preferences", MODE_PRIVATE)
        val reqIntent = Intent(this, GameActivity::class.java)

        reqIntent.putExtra("draw_color", prefs.getInt("draw_color", Color.RED))

        startActivity(reqIntent)
    }

    fun onPressSettings(view: View) {
        val reqIntent = Intent(this, SettingsActivity::class.java)
        reqIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment::class.java.name)
        reqIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true)

        startActivity(reqIntent)
    }
}
