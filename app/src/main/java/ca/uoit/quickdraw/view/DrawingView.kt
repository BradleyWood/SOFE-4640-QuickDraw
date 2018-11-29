package ca.uoit.quickdraw.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import java.util.*
import android.graphics.*


class DrawingView(ctx: Context, attrSet: AttributeSet) : View(ctx, attrSet) {

    private val paths = LinkedList<Path>()
    private val paint: Paint = Paint()

    private var minX = Integer.MAX_VALUE
    private var maxX = 0
    private var minY = Integer.MAX_VALUE
    private var maxY = 0

    init {
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
    }

    fun addStrokes(strokes: List<Stroke>) {
        for (stroke in strokes) {
            val path = stroke.path

            val scaleMatrix = Matrix()
            val rectF = RectF()

            path.computeBounds(rectF, true)

            scaleMatrix.setScale(width.toFloat() / (maxX - minX), height.toFloat() / (maxY - minY))
            path.transform(scaleMatrix)

            paths.add(path)
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        for (path in paths) {
            canvas.drawPath(path, paint)
        }
    }
}
