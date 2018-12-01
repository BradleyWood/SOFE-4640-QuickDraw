package ca.uoit.quickdraw.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import java.util.*
import android.graphics.*
import android.util.Log

class DrawingView(ctx: Context, attrSet: AttributeSet) : View(ctx, attrSet) {

    private val paths = LinkedList<Path>()
    private val paint: Paint = Paint()
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0

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

        for (stroke in strokes) {
            paths.add(stroke.path)
        }

        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        for (path in paths) {
            val newPath = Path()

            val scaleMatrix = Matrix()

            Log.d("GG", "${width.toFloat() / canvasWidth}, ${height.toFloat() / canvasHeight} ")

            scaleMatrix.setScale(width.toFloat() / canvasWidth, height.toFloat() / canvasHeight)
            path.transform(scaleMatrix, newPath)

            canvas.drawPath(newPath, paint)
        }
    }
}
