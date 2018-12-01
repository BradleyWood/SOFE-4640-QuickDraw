package ca.uoit.quickdraw

import android.app.Activity
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.uoit.quickdraw.view.Stroke
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.doAsync
import java.util.*

const val MAX_ROUNDS = 6

class GameActivity : AppCompatActivity(), RoundInfoFragment.RoundInfoFragmentListener,
    DrawFragment.DrawFragmentListener, GameOverFragment.OnFragmentInteractionListener {

    private lateinit var objectList: Array<String>
    private lateinit var objects: LinkedList<String>
    private val roundDrawings = LinkedList<List<Stroke>>()
    private var timeLimit = 20
    private var round = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        objectList = resources.getStringArray(R.array.objects)
        objects = LinkedList()

        for (i in 0..MAX_ROUNDS) {
            objects.add(objectList.random())
        }

        showRoundInfo(round, timeLimit)
    }

    private fun showRoundInfo(round: Int, timeLimit: Int) {
        val fragment = RoundInfoFragment.newInstance(objects[round], timeLimit)
        supportFragmentManager.beginTransaction().replace(R.id.gameLayout, fragment).commit()
    }

    override fun onStartRound() {
        val fragment = DrawFragment.newInstance(objects[round], intent.getIntExtra("draw_color", Color.RED), timeLimit)
        supportFragmentManager.beginTransaction().replace(R.id.gameLayout, fragment).commit()
    }

    override fun onCompleteRound(strokes: List<Stroke>, displayWidth: Int, displayHeight: Int, guess: String) {
        val previousObj = objects[round]

        roundDrawings.add(strokes)

        round++

        if (guess == previousObj) {
            doAsync {
                database.use {
                    insert(
                        "drawings",
                        "object" to previousObj,
                        "strokes" to strokes.toString()
                    )
                }
            }
        }

        if (round >= MAX_ROUNDS) {
            val fragment = GameOverFragment.newInstance(LinkedList(roundDrawings), arrayOf(), displayWidth, displayHeight)
            supportFragmentManager.beginTransaction().replace(R.id.gameLayout, fragment).commit()
        } else {
            showRoundInfo(round, timeLimit)
        }
    }

    override fun onGameOver() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}
