package ca.uoit.quickdraw

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.uoit.quickdraw.view.QuickDrawCanvas
import ca.uoit.quickdraw.view.Stroke
import ca.uoit.quickdraw.view.StrokeListener
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpPost
import java.util.*


private const val ARG_OBJECT_TYPE = "object"
private const val ARG_TIME = "time"
private const val DRAW_COLOR = "draw_color"

class DrawFragment : Fragment(), StrokeListener {

    private val rawStrokeData = LinkedList<List<List<Int>>>()
    private val strokes = LinkedList<Stroke>()
    private var bestResult = "nothing"

    private var drawColor: Int = Color.RED
    private var objectType: String? = null
    private var timeLimit: Int = 20
    private var listener: DrawFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            objectType = it.getString(ARG_OBJECT_TYPE)
            timeLimit = it.getInt(ARG_TIME)
            drawColor = it.getInt(DRAW_COLOR, Color.RED)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_draw, container, false)
        val canvas: QuickDrawCanvas = view.findViewById(R.id.ggCanvas)

        canvas.setDrawColor(drawColor)
        canvas.strokeListener = this

        rawStrokeData.clear()

        val handler = Handler()

        handler.postDelayed({
            if (isVisible) {
                listener!!.onCompleteRound(strokes, canvas.width, canvas.height, bestResult)
            }
        }, timeLimit * 1000L)

        Thread {
            var time = timeLimit

            while (isVisible && time >= 0) {
                handler.post {
                    canvas.setInfoText("$time")
                }
                Thread.sleep(1000)
                time--
            }
        }.start()

        return view
    }

    override fun onStroke(view: View, stroke: Stroke) {
        val url = "https://inputtools.google.com/request?ime=handwriting&app=quickdraw&dbg=1&cs=1&oe=UTF-8"

        val stk = listOf(stroke.xPoints, stroke.yPoints, stroke.relativeTime)
        rawStrokeData.add(stk)
        strokes.add(stroke)

        val obj =
            "{\"input_type\":0,\"requests\":[{\"language\":\"quickdraw\",\"writing_guide\":{\"width\":${view.width},\"height\":${view.height}},\"ink\":" +
                    "$rawStrokeData" +
                    "}]}"

        url.httpPost().jsonBody(obj).responseJson { _, _, (result, error) ->
            if (error == null || result == null) {
                val jsonResponse = result!!.array()
                val rankedResults = jsonResponse.getJSONArray(1).getJSONArray(0).getJSONArray(1)
                bestResult = rankedResults.getString(0)

                Log.d("AI", "I see $bestResult.")

                if (bestResult == objectType) {
                    listener!!.onCompleteRound(strokes, view.width, view.height, bestResult)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DrawFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement DrawFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface DrawFragmentListener {
        fun onCompleteRound(strokes: List<Stroke>, displayWidth: Int, displayHeight: Int, guess: String)
    }

    companion object {

        @JvmStatic
        fun newInstance(objectType: String, drawColor: Int, timeLimit: Int) =
            DrawFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_OBJECT_TYPE, objectType)
                    putInt(DRAW_COLOR, drawColor)
                    putInt(ARG_TIME, timeLimit)
                }
            }
    }
}
