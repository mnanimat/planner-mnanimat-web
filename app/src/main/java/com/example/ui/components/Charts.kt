package com.example.ui.components

import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RitVidaFinance
import com.example.data.RitVidaHour

@Composable
fun FinanceLineChart(
    transactions: List<RitVidaFinance>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val lineColor = MaterialTheme.colorScheme.primary
    val areaColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Calculate cumulative balances
    val sortedTx = transactions.sortedBy { it.dateString }
    val balances = remember(transactions) {
        var runningSum = 0f
        val list = mutableListOf<Float>()
        if (sortedTx.isEmpty()) {
            list.add(0f)
        } else {
            for (tx in sortedTx) {
                if (tx.type == "REVENUE") {
                    runningSum += tx.amount
                } else {
                    runningSum -= tx.amount
                }
                list.add(runningSum)
            }
        }
        list
    }

    val maxVal = remember(balances) { balances.maxOrNull()?.coerceAtLeast(100f) ?: 100f }
    val minVal = remember(balances) { balances.minOrNull()?.coerceAtMost(0f) ?: 0f }
    val range = maxVal - minVal

    var animationProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(balances) {
        animationProgress = 0f
        delay(100)
        animationProgress = 1f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 800)
    )

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height

            // Draw horizontal grids
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = height * i / gridLines
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Label
                val gridVal = maxVal - (range * i / gridLines)
                val labelText = "R$ %.0f".format(gridVal)
                drawText(
                    textMeasurer = textMeasurer,
                    text = labelText,
                    topLeft = Offset(8.dp.toPx(), y - 18.dp.toPx()),
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 10.sp,
                        color = labelColor
                    )
                )
            }

            if (balances.size > 1) {
                val points = balances.mapIndexed { index, balance ->
                    val x = width * index / (balances.size - 1)
                    val y = height - ((balance - minVal) / range) * height
                    Offset(x, y)
                }

                // Smooth Path (Cubic Bezier)
                val strokePath = Path().apply {
                    val p0 = points.first()
                    moveTo(p0.x, p0.y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val current = points[i]
                        val cpX1 = prev.x + (current.x - prev.x) / 2
                        val cpY1 = prev.y
                        val cpX2 = prev.x + (current.x - prev.x) / 2
                        val cpY2 = current.y
                        cubicTo(cpX1, cpY1, cpX2, cpY2, current.x, current.y)
                    }
                }

                val animatedPath = Path()
                // Clip path according to animation progress
                strokePath.let { path ->
                    // Just draw standard path, simple scale for vertical animation
                    val scaleY = animatedProgress
                    points.forEachIndexed { i, pt ->
                        val animY = height - ((balances[i] - minVal) / range) * height * scaleY
                        if (i == 0) {
                            animatedPath.moveTo(pt.x, animY)
                        } else {
                            val prevPt = points[i - 1]
                            val prevAnimY = height - ((balances[i - 1] - minVal) / range) * height * scaleY
                            val cpX1 = prevPt.x + (pt.x - prevPt.x) / 2
                            val cpY1 = prevAnimY
                            val cpX2 = prevPt.x + (pt.x - prevPt.x) / 2
                            val cpY2 = animY
                            animatedPath.cubicTo(cpX1, cpY1, cpX2, cpY2, pt.x, animY)
                        }
                    }
                }

                // Draw Area (fill)
                val fillPath = Path().apply {
                    addPath(animatedPath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(areaColor, Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw Line
                drawPath(
                    path = animatedPath,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw interactive dots
                points.forEachIndexed { i, pt ->
                    val animY = height - ((balances[i] - minVal) / range) * height * animatedProgress
                    drawCircle(
                        color = lineColor,
                        radius = 4.dp.toPx(),
                        center = Offset(pt.x, animY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(pt.x, animY)
                    )
                }
            }
        }
        
        // Dates legend
        if (sortedTx.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = sortedTx.first().dateString,
                    fontSize = 10.sp,
                    color = labelColor
                )
                if (sortedTx.size > 2) {
                    Text(
                        text = sortedTx[sortedTx.size / 2].dateString,
                        fontSize = 10.sp,
                        color = labelColor
                    )
                }
                Text(
                    text = sortedTx.last().dateString,
                    fontSize = 10.sp,
                    color = labelColor
                )
            }
        }
    }
}

@Composable
fun HourDistributionBars(
    hoursList: List<RitVidaHour>,
    modifier: Modifier = Modifier
) {
    val grouped = remember(hoursList) {
        hoursList.groupBy { it.functionName }
            .mapValues { entry -> entry.value.sumOf { it.hours.toDouble() }.toFloat() }
    }
    
    val totalHours = grouped.values.sum().coerceAtLeast(1f)
    val items = remember(hoursList) {
        val base = listOf("Estudante", "Trabalho", "Saúde", "Administrativo")
        val inList = hoursList.map { it.functionName }.distinct().filter { it.isNotBlank() }
        (base + inList).distinct()
    }
    val colors = listOf(
        Color(0xFFAB47BC), // Purple (Estudante)
        Color(0xFFFF7043), // Coral (Trabalho)
        Color(0xFF26A69A), // Teal (Saúde)
        Color(0xFF42A5F5), // Blue (Administrativo)
        Color(0xFF78909C)  // Slate Grey (others)
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEachIndexed { index, name ->
            val hours = grouped[name] ?: 0f
            val percent = (hours / totalHours).coerceIn(0f, 1f)
            
            val animatedPercent by animateFloatAsState(
                targetValue = percent,
                animationSpec = tween(durationMillis = 600, delayMillis = index * 100)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "%.1f h (%.0f%%)".format(hours, percent * 100),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Beautiful sleek progress track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedPercent)
                            .background(
                                color = colors[index % colors.size],
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyStudyHoursChart(
    hoursList: List<RitVidaHour>,
    modifier: Modifier = Modifier
) {
    val weeklyData = remember(hoursList) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        hoursList
            .filter { it.functionName.equals("Estudante", ignoreCase = true) }
            .groupBy { hour ->
                try {
                    val date = sdf.parse(hour.dateString)
                    val cal = java.util.Calendar.getInstance()
                    if (date != null) {
                        cal.time = date
                        val weekNum = cal.get(java.util.Calendar.WEEK_OF_YEAR)
                        "Sem. $weekNum"
                    } else {
                        "Geral"
                    }
                } catch (e: Exception) {
                    "Geral"
                }
            }
            .mapValues { entry -> entry.value.sumOf { it.hours.toDouble() }.toFloat() }
            .toList()
            .sortedBy { it.first }
    }

    if (weeklyData.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum dado de estudo registrado para gerar o gráfico semanal.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val maxHours = weeklyData.maxOf { it.second }.coerceAtLeast(1f)

        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyData.forEachIndexed { index, (week, hours) ->
                    val ratio = hours / maxHours
                    val animatedRatio by animateFloatAsState(
                        targetValue = ratio,
                        animationSpec = tween(durationMillis = 600, delayMillis = index * 50)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${hours.toInt()}h",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(0.5f)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                                .fillMaxHeight(animatedRatio)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = week,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

