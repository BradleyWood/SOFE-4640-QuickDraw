package ca.uoit.quickdraw.model

import android.graphics.Path
import java.util.*

data class DrawingModel(
    val obj: String,
    val time: Long,
    val strokes: List<Stroke>,
    val originalWidth: Int,
    val originalHeight: Int
)

class Stroke {

    private val startTime = System.currentTimeMillis()

    internal val xPoints = LinkedList<Int>()
    internal val yPoints = LinkedList<Int>()
    internal val relativeTime = LinkedList<Int>()
    @Transient internal val path = Path()

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
