package ca.uoit.quickdraw.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ca.uoit.quickdraw.model.Stroke
import java.util.*
import kotlin.math.roundToInt

class QuickDrawCanvas(ctx: Context, attrSet: AttributeSet) : View(ctx, attrSet) {

    private val strokes = LinkedList<Stroke>()
    private val paint: Paint = Paint()
    private var infoText: String = ""
    private var drawColor = Color.RED
    var strokeListener: StrokeListener? = null

    init {
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        paint.textSize = 40f
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
    }

    fun setDrawColor(color: Int) {
        drawColor = color
    }

    fun setInfoText(infoText: String) {
        this.infoText = infoText
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        paint.color = Color.BLACK

        canvas!!.drawText(infoText, width / 2f - 20f, 40f, paint)

        paint.color = drawColor

        for (stroke in strokes) {
            canvas.drawPath(stroke.path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x.roundToInt()
        val y = event.y.roundToInt()

        val result = when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val stroke = Stroke()
                stroke.addPoint(x, y)
                strokes.add(stroke)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                strokes.last.addPoint(x, y)
                true
            }
            MotionEvent.ACTION_UP -> {
                strokes.last.addPoint(x, y)
                strokeListener!!.onStroke(this, strokes.last)
                true
            }

            else -> super.onTouchEvent(event)
        }

        postInvalidate()
        return result
    }

    interface StrokeListener {

        fun onStroke(view: View, stroke: Stroke)

    }
}
