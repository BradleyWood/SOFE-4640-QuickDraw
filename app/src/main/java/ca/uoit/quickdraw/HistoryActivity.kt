package ca.uoit.quickdraw

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import org.jetbrains.anko.db.select
import org.jetbrains.anko.doAsync
import java.util.*
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.uiThread
import android.view.LayoutInflater
import ca.uoit.quickdraw.model.DrawingModel
import ca.uoit.quickdraw.model.Stroke
import ca.uoit.quickdraw.view.DrawingView
import org.jetbrains.anko.db.SqlOrderDirection
import android.support.v4.widget.SwipeRefreshLayout


class HistoryActivity : AppCompatActivity() {

    private val drawingTokenType = object : TypeToken<List<Stroke>>() {}.type
    private lateinit var listView: ListView
    private val listAdaptor = LinkedList<DrawingModel>()
    private lateinit var adapter: QDViewAdaptor
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var timePlaceholder: Long = Long.MAX_VALUE
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        listView = findViewById(R.id.history_list_view)
        adapter = QDViewAdaptor(this, listAdaptor)
        listView.adapter = adapter

        refreshLayout = findViewById(R.id.pull_refresh)

        refreshLayout.setOnRefreshListener {
            addResultsToScrollView(10)
        }

        addResultsToScrollView(10)
    }

    private fun addResultsToScrollView(amount: Int) {
        doAsync {
            val datum = LinkedList<DrawingModel>()
            database.use {
                select(
                    "drawings"
                ).whereArgs(
                    "time < {time}",
                    "time" to timePlaceholder
                ).limit(amount).orderBy("time", SqlOrderDirection.DESC).exec {
                    while (moveToNext()) {
                        val obj = getString(getColumnIndex("object"))
                        val guess = getString(getColumnIndex("guess"))
                        val time = getLong(getColumnIndex("time"))
                        val json = getString(getColumnIndex("strokes"))
                        val width = getInt(getColumnIndex("width"))
                        val height = getInt(getColumnIndex("height"))

                        // so we don't re add previous results
                        if (time < timePlaceholder)
                            timePlaceholder = time

                        val strokes: List<Stroke> = gson.fromJson(json, drawingTokenType)
                        datum.add(DrawingModel(obj, guess, time, strokes, width, height))
                    }

                    close()
                }
            }

            uiThread {
                for (drawingModel in datum) {
                    listAdaptor.add(drawingModel)
                }

                adapter.notifyDataSetChanged()
                refreshLayout.isRefreshing = false
            }
        }
    }

    class QDViewAdaptor(private val ctx: Context, val list: List<DrawingModel>) : BaseAdapter() {

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): DrawingModel {
            return list[position]
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val item = getItem(position)
            var listItem = convertView

            if (listItem == null) {
                val inflater = LayoutInflater.from(ctx)
                listItem = inflater.inflate(R.layout.list_adaptor, parent, false)
            }

            listItem!!

            val instructionView: TextView = listItem.findViewById(R.id.list_text_view)
            instructionView.text = ctx.resources.getString(R.string.instruction, item.obj)

            val guessView: TextView = listItem.findViewById(R.id.guess_text_view)
            guessView.text = ctx.resources.getString(R.string.guess, item.guess)

            val dv: DrawingView = listItem.findViewById(R.id.list_drawing_view)
            dv.setDrawing(item.strokes, item.originalWidth, item.originalHeight)

            if (item.guess == item.obj) {
                listItem.setBackgroundColor(Color.GREEN)
            } else {
                listItem.setBackgroundColor(Color.RED)
            }

            return listItem
        }
    }
}
