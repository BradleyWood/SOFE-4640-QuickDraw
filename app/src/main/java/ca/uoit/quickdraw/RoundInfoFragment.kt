package ca.uoit.quickdraw

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


private const val ARG_OBJECT_TYPE = "object"
private const val ARG_TIME = "time"

class RoundInfoFragment : Fragment() {

    private var objectType: String? = null
    private var timeLimit: Int? = null
    private var listener: RoundInfoFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            objectType = it.getString(ARG_OBJECT_TYPE)
            timeLimit = it.getInt(ARG_TIME)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_round_info, container, false)
        val instructionView: TextView = view.findViewById(R.id.roundInfo)
        val playButton: TextView = view.findViewById(R.id.beginRoundBtn)

        playButton.setOnClickListener {
            listener!!.onStartRound()
        }

        instructionView.text = resources.getString(R.string.draw_instructions, objectType, timeLimit)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RoundInfoFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement RoundInfoFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface RoundInfoFragmentListener {
        fun onStartRound()
    }

    companion object {

        @JvmStatic
        fun newInstance(objectType: String, timeLimit: Int) =
            RoundInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_OBJECT_TYPE, objectType)
                    putInt(ARG_TIME, timeLimit)
                }
            }
    }
}
