package pl.pjatk.finansemanager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.absoluteValue

data class DataPoint(
    var xVal: Int,
    var yVal: Int
)

class ExpensesGraph(context: Context, attributes: AttributeSet) : View(context, attributes) {

    private val dataSet = mutableListOf<DataPoint>()
    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0

    private val dataPointPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        isFakeBoldText = true
        strokeWidth = 7f
        textSize = 40f
    }

    private val smallTextPaint = Paint().apply {
        color = Color.BLACK
        isFakeBoldText = true
        strokeWidth = 7f
        textSize = 20f
    }

    private val dataPointFillPaint = Paint().apply {
        color = Color.WHITE
    }

    private val dataPointLinePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 7f
        isAntiAlias = true
    }

    private val negativeDataPointLinePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 7f
        isAntiAlias = true
    }

    private val axisLinePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
    }

    fun setData(newDataSet: List<DataPoint>) {
        xMin = newDataSet.minBy { it.xVal }?.xVal ?: 0
        xMax = newDataSet.maxBy { it.xVal }?.xVal ?: 0
        yMin = newDataSet.minBy { it.yVal }?.yVal ?: 0
        yMax = newDataSet.maxBy { it.yVal }?.yVal ?: 0
        dataSet.clear()
        dataSet.addAll(newDataSet)
        invalidate()
    }

    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec) + 900
        val height = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        parent.requestDisallowInterceptTouchEvent(true)

        super.onDraw(canvas)
        val totalZeroY = height / 2
        val minimumBalance = dataSet.minOf { it.yVal }
        val maximumBalance = dataSet.maxOf { it.yVal }
        val scale =
            kotlin.runCatching { minimumBalance / maximumBalance }.getOrDefault(5).absoluteValue + 5

        val maxValue =
            listOf(minimumBalance.absoluteValue, maximumBalance.absoluteValue).maxOf { it }
        val step = maxValue / 10
        val steps = List(21) { maxValue - it * step }

        steps.forEach {
            var y: Float = it.toRealY() / scale;

                if (y < 0) {
                    y = totalZeroY + y.absoluteValue
                } else {
                    y = totalZeroY - y
                }
            canvas.drawText(it.toString(), 2f, y - 5f, textPaint)

            if(it != 0) {
                canvas.drawLine(185f, y - 20f, 215f, y - 20f, axisLinePaint)
            }
        }

        println("maxValue $maxValue, maximumBalance $maximumBalance, minimumBalance $minimumBalance, $step, sizeDataSet ${dataSet.size}")

        dataSet.forEachIndexed { index, currentDataPoint ->
            var realX = currentDataPoint.xVal.toRealX()
            var realY = (currentDataPoint.yVal.toRealY() / scale).toInt().toFloat()

            if (realY < 0) {
                realY = totalZeroY + realY.absoluteValue
            } else {
                realY = totalZeroY - realY
            }

            if (index < dataSet.size - 1) {
                val nextDataPoint = dataSet[index + 1]
                val endX = nextDataPoint.xVal.toRealX()
                var endY = (nextDataPoint.yVal.toRealY() / scale).toInt().toFloat()

                if (endY < 0) {
                    endY = totalZeroY + endY.absoluteValue
                } else {
                    endY = totalZeroY - endY
                }

                if (endY > height / 2) {
                    canvas.drawLine(realX, realY, endX, endY, negativeDataPointLinePaint)
                } else {
                    canvas.drawLine(realX, realY, endX, endY, dataPointLinePaint)
                }
            }

            canvas.drawCircle(realX, realY, 7f, dataPointFillPaint)
            canvas.drawCircle(realX, realY, 7f, dataPointPaint)
            canvas.drawText(
                (1 + currentDataPoint.xVal).toString(),
                realX,
                height / 2 + 25f,
                smallTextPaint
            )
            canvas.drawCircle(realX + 10f, totalZeroY.toFloat(), 4f, dataPointPaint)
        }

        //@todo zrob funkcje draw przedzial kwot na osi Y, wybierz najwieksza i po prostu schodz co X
        //@otod dobieraj skale wykresu realX max(abs) / realX min(abs) :) maxBy itp

        canvas.drawLine(
            0f,
            height.toFloat() / 2,
            width.toFloat(),
            height.toFloat() / 2,
            axisLinePaint
        )

        canvas.drawLine(
            200f,
            0f,
            200f,
            height.toFloat(),
            axisLinePaint
        )
    }


    private fun Int.toRealX() = (toFloat() / xMax * (width - 400f)) + 240f
    private fun Int.toRealY() = toFloat() / yMax * height
}