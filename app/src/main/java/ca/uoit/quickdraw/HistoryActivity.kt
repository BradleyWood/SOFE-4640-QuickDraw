package ca.uoit.quickdraw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ScrollView
import ca.uoit.quickdraw.view.ObservableScrollView
import ca.uoit.quickdraw.view.Stroke
import com.google.gson.Gson
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync
import java.util.*
import com.google.gson.reflect.TypeToken


class HistoryActivity : AppCompatActivity(), ObservableScrollView.ScrollViewListener {

    private var timePlaceholder: Long = 0
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val scrollView: ObservableScrollView = findViewById(R.id.history_scroll_view)
        scrollView.scrollListener = this

        addResultsToScrollView(10)
    }

    private fun addResultsToScrollView(amount: Int) {
        doAsync {
            synchronized(this@HistoryActivity) {
                val results = LinkedList<Any>()
                database.use {
                    select(
                        "drawings"
                    ).whereArgs(
                        "time > {time}",
                        "time" to timePlaceholder
                    ).exec {
                        while (moveToNext()) {
                            val obj = getString(getColumnIndex("object"))
                            val time = getLong(getColumnIndex("time"))
                            val json = getString(getColumnIndex("strokes"))

                            val type = object : TypeToken<List<Stroke>>() {}.type
                            val strokes: List<Stroke> = gson.fromJson(json, type)

                            results.add(DrawingModel(obj, time, strokes))
                        }
                    }
                }
            }
        }
    }

    data class DrawingModel(val obj: String, val time: Long, val strokes: List<Stroke>)

    override fun onScrollToBottom(view: ScrollView) {

    }
}
