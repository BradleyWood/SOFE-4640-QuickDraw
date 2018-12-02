package ca.uoit.quickdraw.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import java.util.*
import android.graphics.*
import ca.uoit.quickdraw.model.Stroke

class DrawingView(ctx: Context, attrSet: AttributeSet) : View(ctx, attrSet) {

    private val paths = LinkedList<Path>()
    private val paint: Paint = Paint()
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0
    private val scaleMatrix = Matrix()
    private val newPath = Path()

    init {
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
    }

    fun setDrawing(strokes: List<Stroke>, canvasWidth: Int, canvasHeight: Int) {
        this.canvasWidth = canvasWidth
        this.canvasHeight = canvasHeight

        synchronized(paths) {
            paths.clear()

            for (stroke in strokes) {
                val path = Path()

                for (i in 0 until stroke.xPoints.size) {
                    if (i == 0)
                        path.moveTo(stroke.xPoints[i].toFloat(), stroke.yPoints[i].toFloat())
                    else
                        path.lineTo(stroke.xPoints[i].toFloat(), stroke.yPoints[i].toFloat())
                }

                paths.add(path)
            }
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        synchronized(paths) {
            for (path in paths) {
                newPath.reset()
                scaleMatrix.reset()

                scaleMatrix.setScale(width.toFloat() / canvasWidth, height.toFloat() / canvasHeight)
                path.transform(scaleMatrix, newPath)

                canvas.drawPath(newPath, paint)
            }
        }
    }
}
