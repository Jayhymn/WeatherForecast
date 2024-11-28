package com.wakeupdev.weatherforecast.utils

import android.content.Context
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.AxisBase
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.data.HourlyTemperature

object ChartHelper {

    fun updateLineChart(
        context: Context,
        lineChart: LineChart,
        temperatureData: List<HourlyTemperature>
    ) {
        // Map temperature data to entries
        val entries = temperatureData.take(24).mapIndexed { index, temperature ->
            Entry(index.toFloat(), temperature.temperature!!.toFloat())
        }

        // Create the dataset (LineDataSet)
        val dataSet = LineDataSet(entries, "Hourly Temperature").apply {
            color = context.resources.getColor(R.color.primary, null)
            valueTextColor = context.resources.getColor(R.color.textPrimary, null)
            lineWidth = 2f
            setDrawCircles(true)
            circleRadius = 4f
            setDrawCircleHole(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = context.resources.getColor(R.color.primary, null)
            fillAlpha = 50
        }

        // Set up the line chart data
        val lineData = LineData(dataSet)

        // Configure the LineChart
        lineChart.apply {
            data = lineData
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            // Customize X-axis to show time labels in increments of 3
            val timeLabels = temperatureData.take(24).map { it.date }

            // Ensure that we only show every 3rd value
            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    val index = value.toInt()
                    return if (index % 3 == 0) {
                        timeLabels.getOrNull(index) ?: ""
                    } else {
                        ""
                    }
                }
            }

            axisLeft.apply {
                setDrawLabels(false)
                setDrawGridLines(false)
            }

            xAxis.granularity = 1f
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.labelRotationAngle = -45f
        }

        lineChart.invalidate() // Refresh the chart
    }
}
