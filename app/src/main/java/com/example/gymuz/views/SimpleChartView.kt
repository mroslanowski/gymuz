package com.example.gymuz.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.gymuz.R
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

class SimpleChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class ChartPoint(val x: Float, val y: Float, val label: String)

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.main_color)
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.main_color)
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }

    private val labelTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private var chartPoints: List<ChartPoint> = emptyList()
    private var chartTitle: String = ""
    private var yAxisLabel: String = ""

    private val padding = 80f
    private val pointRadius = 8f

    fun setData(points: List<ChartPoint>, title: String = "", yLabel: String = "") {
        chartPoints = points
        chartTitle = title
        yAxisLabel = yLabel
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (chartPoints.isEmpty()) {
            drawEmptyChart(canvas)
            return
        }

        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding - 60f // Extra space for title

        // Draw title
        if (chartTitle.isNotEmpty()) {
            canvas.drawText(chartTitle, width / 2f, 40f, textPaint)
        }

        // Calculate min and max values
        val minY = chartPoints.minOf { it.y }
        val maxY = chartPoints.maxOf { it.y }
        val range = maxY - minY
        val adjustedMinY = if (range == 0f) minY - 1 else minY - range * 0.1f
        val adjustedMaxY = if (range == 0f) maxY + 1 else maxY + range * 0.1f
        val adjustedRange = adjustedMaxY - adjustedMinY

        // Draw grid lines
        drawGrid(canvas, chartWidth, chartHeight, adjustedMinY, adjustedMaxY)

        // Draw chart line and points
        if (chartPoints.size > 1) {
            drawChartLine(canvas, chartWidth, chartHeight, adjustedMinY, adjustedRange)
        }
        drawChartPoints(canvas, chartWidth, chartHeight, adjustedMinY, adjustedRange)

        // Draw Y-axis labels
        drawYAxisLabels(canvas, chartHeight, adjustedMinY, adjustedMaxY)

        // Draw X-axis labels
        drawXAxisLabels(canvas, chartWidth, chartHeight)
    }

    private fun drawEmptyChart(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f

        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("No data available", centerX, centerY, textPaint)
    }

    private fun drawGrid(canvas: Canvas, chartWidth: Float, chartHeight: Float, minY: Float, maxY: Float) {
        val gridLines = 5

        // Horizontal grid lines
        for (i in 0..gridLines) {
            val y = padding + 60f + (chartHeight * i / gridLines)
            canvas.drawLine(padding, y, padding + chartWidth, y, gridPaint)
        }

        // Vertical grid lines
        if (chartPoints.isNotEmpty()) {
            val segments = min(chartPoints.size - 1, 4)
            for (i in 0..segments) {
                val x = padding + (chartWidth * i / segments)
                canvas.drawLine(x, padding + 60f, x, padding + 60f + chartHeight, gridPaint)
            }
        }
    }

    private fun drawChartLine(canvas: Canvas, chartWidth: Float, chartHeight: Float, minY: Float, range: Float) {
        val path = Path()

        chartPoints.forEachIndexed { index, point ->
            val x = padding + (chartWidth * index / (chartPoints.size - 1))
            val y = padding + 60f + chartHeight - ((point.y - minY) / range * chartHeight)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        canvas.drawPath(path, linePaint)
    }

    private fun drawChartPoints(canvas: Canvas, chartWidth: Float, chartHeight: Float, minY: Float, range: Float) {
        chartPoints.forEachIndexed { index, point ->
            val x = padding + (chartWidth * index / max(chartPoints.size - 1, 1))
            val y = padding + 60f + chartHeight - ((point.y - minY) / range * chartHeight)

            canvas.drawCircle(x, y, pointRadius, pointPaint)
        }
    }

    private fun drawYAxisLabels(canvas: Canvas, chartHeight: Float, minY: Float, maxY: Float) {
        val gridLines = 5
        val decimalFormat = DecimalFormat("#.#")

        textPaint.textAlign = Paint.Align.RIGHT

        for (i in 0..gridLines) {
            val value = minY + ((maxY - minY) * (gridLines - i) / gridLines)
            val y = padding + 60f + (chartHeight * i / gridLines) + 10f
            canvas.drawText(decimalFormat.format(value), padding - 10f, y, labelTextPaint)
        }
    }

    private fun drawXAxisLabels(canvas: Canvas, chartWidth: Float, chartHeight: Float) {
        if (chartPoints.isEmpty()) return

        textPaint.textAlign = Paint.Align.CENTER

        val maxLabels = min(chartPoints.size, 5)
        val step = max(1, chartPoints.size / maxLabels)

        for (i in chartPoints.indices step step) {
            val point = chartPoints[i]
            val x = padding + (chartWidth * i / max(chartPoints.size - 1, 1))
            val y = padding + 60f + chartHeight + 30f

            // Shorten long labels
            val label = if (point.label.length > 6) {
                point.label.substring(0, 6) + "..."
            } else {
                point.label
            }

            canvas.drawText(label, x, y, labelTextPaint)
        }
    }
}