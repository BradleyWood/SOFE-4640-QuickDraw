package ca.uoit.quickdraw

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import ca.uoit.quickdraw.view.QuickDrawCanvas
import ca.uoit.quickdraw.view.Stroke
import ca.uoit.quickdraw.view.StrokeListener
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpPost
import java.util.*

class DrawActivity : AppCompatActivity(), StrokeListener {

    private val strokes = LinkedList<List<List<Int>>>()
    private var bestResult = "nothing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        val canvas = findViewById<QuickDrawCanvas>(R.id.canvas)
        canvas.strokeListener = this
        strokes.clear()

        val time = intent.getLongExtra("time", 20000L)
        val handler = Handler()

        handler.postDelayed({
            if (!isFinishing) {
                val resultIntent = Intent()

                resultIntent.putExtra("strokes", strokes)
                resultIntent.putExtra("best", bestResult)
                resultIntent.putExtra("success", false)

                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }, time)

        Toast.makeText(this, "Draw ${intent.getStringExtra("object")} in 20 seconds.", Toast.LENGTH_LONG).show()
    }

    override fun onStroke(view: View, stroke: Stroke) {
        val url = "https://inputtools.google.com/request?ime=handwriting&app=quickdraw&dbg=1&cs=1&oe=UTF-8"

        val stk = listOf(stroke.xPoints, stroke.yPoints, stroke.relativeTime)
        strokes.add(stk)

        val obj = "{\"input_type\":0,\"requests\":[{\"language\":\"quickdraw\",\"writing_guide\":{\"width\":${view.width},\"height\":${view.height}},\"ink\":" +
                    "$strokes" +
                    "}]}"

        url.httpPost().jsonBody(obj).responseJson { _, _, result ->
            val jsonResponse = result.get().array()
            val rankedResults = jsonResponse.getJSONArray(1).getJSONArray(0).getJSONArray(1)
            bestResult = rankedResults.getString(0)

            Log.d("AI", "I see $bestResult.")

            if (bestResult == intent.getStringExtra("object")) {
                val resultIntent = Intent()

                resultIntent.putExtra("strokes", strokes)
                resultIntent.putExtra("best", bestResult)
                resultIntent.putExtra("success", true)

                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
