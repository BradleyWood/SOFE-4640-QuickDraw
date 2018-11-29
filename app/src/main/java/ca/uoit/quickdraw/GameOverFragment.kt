package ca.uoit.quickdraw

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import ca.uoit.quickdraw.view.Stroke
import java.util.*

private const val ARG_DRAWINGS = "round_drawings"
private const val ARG_OBJECTS = "round_objects"


class GameOverFragment : Fragment() {
    private var drawings: List<List<Stroke>>? = null
    private var objects: Array<String>? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            drawings = it.getSerializable(ARG_DRAWINGS) as List<List<Stroke>>
            objects = it.getStringArray(ARG_OBJECTS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_over, container, false)

        val gameOverBtn: Button = view.findViewById(R.id.gameOverBtn)

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
        fun newInstance(drawings: LinkedList<List<Stroke>>, objects: Array<String>) =
            GameOverFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DRAWINGS, drawings)
                    putStringArray(ARG_OBJECTS, objects)
                }
            }
    }
}
