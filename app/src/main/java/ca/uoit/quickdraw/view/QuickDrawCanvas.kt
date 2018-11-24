package ca.uoit.quickdraw.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.math.roundToInt

class QuickDrawCanvas(ctx: Context, attrSet: AttributeSet) : View(ctx, attrSet) {

    private val strokes = LinkedList<Stroke>()
    private val paint: Paint = Paint()
    var strokeListener: StrokeListener? = null

    init {
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
    }

    fun setDrawColor(color: Int) {
        paint.color = color
    }

    override fun onDraw(canvas: Canvas?) {
        for (stroke in strokes) {
            canvas!!.drawPath(stroke.path, paint)
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
}

class Stroke {

    private val startTime = System.currentTimeMillis()

    internal val xPoints = LinkedList<Int>()
    internal val yPoints = LinkedList<Int>()
    internal val relativeTime = LinkedList<Int>()
    internal val path = Path()

    fun addPoint(x: Int, y: Int) {
        xPoints.add(x)
        yPoints.add(y)

        if (relativeTime.size == 0) {
            relativeTime.add(0)
            path.moveTo(x.toFloat(), y.toFloat())
        } else {
            relativeTime.add((System.currentTimeMillis() - startTime).toInt())
            path.lineTo(x.toFloat(), y.toFloat())
        }
    }
}

interface StrokeListener {

    fun onStroke(view: View, stroke: Stroke)

}
