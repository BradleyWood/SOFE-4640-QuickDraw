package ca.uoit.quickdraw

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

const val DRAW_REQUEST_CODE = 10

class MainActivity : AppCompatActivity() {

    lateinit var objects: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        objects = resources.getStringArray(R.array.objects)
    }

    fun onPressPlay(view: View) {
        val reqIntent = Intent(this, DrawActivity::class.java)

        reqIntent.putExtra("object", objects.random())
        startActivityForResult(reqIntent, DRAW_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DRAW_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data!!.getBooleanExtra("success", false)) {
                Toast.makeText(this, "Congrats, the AI guessed correctly", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Sorry, the AI's best guess was ${data.getStringExtra("best")}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
