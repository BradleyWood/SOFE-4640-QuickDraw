package ca.uoit.quickdraw

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import ca.uoit.quickdraw.model.Stroke
import ca.uoit.quickdraw.view.DrawingView
import java.util.*

private const val ARG_DRAWINGS = "round_drawings"
private const val ARG_OBJECTS = "round_objects"
private const val ARG_CANVAS_WIDTH = "canvas_width"
private const val ARG_CANVAS_HEIGHT = "canvas_height"


class GameOverFragment : Fragment() {
    private var drawings: List<List<Stroke>>? = null
    private var objects: Array<String>? = null
    private var listener: OnFragmentInteractionListener? = null
    private var width: Int = 0
    private var height: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            drawings = it.getSerializable(ARG_DRAWINGS) as List<List<Stroke>>
            objects = it.getStringArray(ARG_OBJECTS)
            width = it.getInt(ARG_CANVAS_WIDTH)
            height = it.getInt(ARG_CANVAS_WIDTH)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_over, container, false)

        val gameOverBtn: Button = view.findViewById(R.id.gameOverBtn)
        val round1: DrawingView = view.findViewById(R.id.drawingView1)
        val round2: DrawingView = view.findViewById(R.id.drawingView2)
        val round3: DrawingView = view.findViewById(R.id.drawingView3)
        val round4: DrawingView = view.findViewById(R.id.drawingView4)
        val round5: DrawingView = view.findViewById(R.id.drawingView5)
        val round6: DrawingView = view.findViewById(R.id.drawingView6)

        round1.setDrawing(drawings!![0], width, height)
        round2.setDrawing(drawings!![1], width, height)
        round3.setDrawing(drawings!![2], width, height)
        round4.setDrawing(drawings!![3], width, height)
        round5.setDrawing(drawings!![4], width, height)
        round6.setDrawing(drawings!![5], width, height)

        gameOverBtn.setOnClickListener {
            listener!!.onGameOver()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onGameOver()
    }

    companion object {

        @JvmStatic
        fun newInstance(
            drawings: LinkedList<List<Stroke>>,
            objects: Array<String>,
            canvasWidth: Int,
            canvasHeight: Int
        ) =
            GameOverFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DRAWINGS, drawings)
                    putStringArray(ARG_OBJECTS, objects)
                    putInt(ARG_CANVAS_WIDTH, canvasWidth)
                    putInt(ARG_CANVAS_HEIGHT, canvasHeight)
                }
            }
    }
}
