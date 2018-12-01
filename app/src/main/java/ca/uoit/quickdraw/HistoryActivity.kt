package ca.uoit.quickdraw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ScrollView
import ca.uoit.quickdraw.view.ObservableScrollView
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync
import java.util.*

class HistoryActivity : AppCompatActivity(), ObservableScrollView.ScrollViewListener {

    private var timePlaceholder: Long = 0

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
                            val strokes = getString(getColumnIndex("strokes"))

                            results.add(DrawingModel(obj, time, strokes))
                        }
                    }
                }
            }
        }
    }

    data class DrawingModel(val obj: String, val time: Long, val strokes: String)

    override fun onScrollToBottom(view: ScrollView) {

    }
}
