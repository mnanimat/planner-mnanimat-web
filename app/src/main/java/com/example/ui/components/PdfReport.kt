package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.RitVidaHour
import com.example.data.StudySubject
import com.example.data.CustomCronogramaItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReport {
    fun generateAndSharePdf(
        context: Context,
        hoursList: List<RitVidaHour>,
        subjectsList: List<StudySubject>,
        customCronogramaList: List<CustomCronogramaItem>
    ) {
        try {
            val pdfDocument = PdfDocument()
            val paint = Paint()

            // ==================== PAGE 1 ====================
            val pageInfo1 = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page1 = pdfDocument.startPage(pageInfo1)
            val canvas1 = page1.canvas

            // Header Background Accent Bar
            paint.color = 0xFF4A148C.toInt() // Deep Obsidian/Violet
            canvas1.drawRect(0f, 0f, 595f, 95f, paint)

            // Header Title
            paint.color = 0xFFFFFFFF.toInt()
            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas1.drawText("FocoVest & RitVida - Relatório de Desempenho", 30f, 40f, paint)

            // Header Subtitle
            paint.textSize = 11f
            paint.isFakeBoldText = false
            canvas1.drawText("Acompanhamento local, soberano e privado das suas atividades", 30f, 65f, paint)

            // Generation Date
            val sdfDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val generationDateStr = sdfDate.format(Date())
            paint.textAlign = Paint.Align.RIGHT
            canvas1.drawText("Gerado em: $generationDateStr", 565f, 65f, paint)
            paint.textAlign = Paint.Align.LEFT

            // Section: Resumo de Rotina
            paint.color = 0xFF000000.toInt()
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas1.drawText("1. Resumo de Horas da Rotina (RitVida)", 30f, 130f, paint)

            // Line Separator
            paint.color = 0xFFE0E0E0.toInt()
            canvas1.drawLine(30f, 140f, 565f, 140f, paint)

            // Metrics Box
            paint.color = 0xFFF3E5F5.toInt() // Ultra light violet
            canvas1.drawRoundRect(30f, 155f, 565f, 215f, 8f, 8f, paint)

            paint.color = 0xFF4A148C.toInt()
            paint.textSize = 11f
            paint.isFakeBoldText = true
            canvas1.drawText("Métricas de Tempo Consolidado", 45f, 175f, paint)

            paint.color = 0xFF333333.toInt()
            paint.isFakeBoldText = false
            val totalHours = hoursList.sumOf { it.hours.toDouble() }.toFloat()
            val uniqueDays = hoursList.map { it.dateString }.distinct().size
            canvas1.drawText("Total de horas registradas: %.1f h".format(totalHours), 45f, 195f, paint)
            canvas1.drawText("Dias com registro de atividades: $uniqueDays dias", 320f, 195f, paint)

            // Activity distribution
            paint.color = 0xFF000000.toInt()
            paint.textSize = 13f
            paint.isFakeBoldText = true
            canvas1.drawText("Distribuição de Horas por Atividade:", 30f, 245f, paint)

            val groupedHours = hoursList.groupBy { it.functionName }
                .mapValues { entry -> entry.value.sumOf { it.hours.toDouble() }.toFloat() }
            val totalHoursCoerced = totalHours.coerceAtLeast(1f)

            var yOffset = 275f
            val baseCategories = listOf("Estudante", "Trabalho", "Saúde", "Administrativo")
            val allCategoriesInList = hoursList.map { it.functionName }.distinct().filter { it.isNotBlank() }
            val mergedCategories = (baseCategories + allCategoriesInList).distinct()

            mergedCategories.forEach { categoryName ->
                val hoursForCategory = groupedHours[categoryName] ?: 0f
                val percent = (hoursForCategory / totalHoursCoerced).coerceIn(0f, 1f)

                paint.color = 0xFF222222.toInt()
                paint.textSize = 11f
                paint.isFakeBoldText = true
                canvas1.drawText(categoryName, 30f, yOffset, paint)

                paint.textAlign = Paint.Align.RIGHT
                canvas1.drawText("%.1f h (%.0f%%)".format(hoursForCategory, percent * 100), 565f, yOffset, paint)
                paint.textAlign = Paint.Align.LEFT

                // Progress track
                paint.color = 0xFFEEEEEE.toInt()
                canvas1.drawRoundRect(30f, yOffset + 5f, 565f, yOffset + 12f, 4f, 4f, paint)

                // Fill color
                paint.color = when (categoryName) {
                    "Estudante" -> 0xFFAB47BC.toInt() // Purple
                    "Trabalho" -> 0xFFFF7043.toInt()  // Coral
                    "Saúde" -> 0xFF26A69A.toInt()     // Teal
                    "Administrativo" -> 0xFF42A5F5.toInt() // Blue
                    else -> 0xFF78909C.toInt() // Slate Grey
                }
                val progressWidth = 30f + (535f * percent)
                if (progressWidth > 30f) {
                    canvas1.drawRoundRect(30f, yOffset + 5f, progressWidth, yOffset + 12f, 4f, 4f, paint)
                }

                yOffset += 32f
            }

            // Recent activity list
            yOffset += 15f
            paint.color = 0xFF000000.toInt()
            paint.textSize = 13f
            paint.isFakeBoldText = true
            canvas1.drawText("Atividades Recentes Registradas:", 30f, yOffset, paint)

            paint.color = 0xFFE0E0E0.toInt()
            canvas1.drawLine(30f, yOffset + 8f, 565f, yOffset + 8f, paint)
            yOffset += 24f

            val recentHours = hoursList.take(8)
            if (recentHours.isEmpty()) {
                paint.color = 0xFF777777.toInt()
                paint.textSize = 11f
                paint.isFakeBoldText = false
                canvas1.drawText("Nenhum registro de horas adicionado ainda.", 45f, yOffset, paint)
            } else {
                recentHours.forEach { entry ->
                    paint.color = 0xFF333333.toInt()
                    paint.textSize = 11f
                    paint.isFakeBoldText = true
                    canvas1.drawText(entry.functionName, 45f, yOffset, paint)

                    paint.isFakeBoldText = false
                    canvas1.drawText("Horas: ${entry.hours} h", 220f, yOffset, paint)
                    canvas1.drawText("Data: ${entry.dateString}", 420f, yOffset, paint)

                    paint.color = 0xFFF5F5F5.toInt()
                    canvas1.drawLine(30f, yOffset + 6f, 565f, yOffset + 6f, paint)

                    yOffset += 22f
                }
            }

            // Page 1 Footer
            paint.color = 0xFF999999.toInt()
            paint.textSize = 9f
            paint.isFakeBoldText = false
            canvas1.drawText("Gerado localmente e criptografado por RitVida • Página 1 de 2", 30f, 815f, paint)

            pdfDocument.finishPage(page1)

            // ==================== PAGE 2 ====================
            val pageInfo2 = PdfDocument.PageInfo.Builder(595, 842, 2).create()
            val page2 = pdfDocument.startPage(pageInfo2)
            val canvas2 = page2.canvas

            // Header page 2
            paint.color = 0xFF0D47A1.toInt() // Dark Blue accent
            canvas2.drawRect(0f, 0f, 595f, 95f, paint)

            paint.color = 0xFFFFFFFF.toInt()
            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas2.drawText("FocoVest & RitVida - Relatório de Desempenho", 30f, 40f, paint)

            paint.textSize = 11f
            paint.isFakeBoldText = false
            canvas2.drawText("Acompanhamento das Trilhas de Estudo e Cronogramas Oficiais", 30f, 65f, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas2.drawText("Gerado em: $generationDateStr", 565f, 65f, paint)
            paint.textAlign = Paint.Align.LEFT

            // Section: Trilhas de Estudo FocoVest
            paint.color = 0xFF000000.toInt()
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas2.drawText("2. Progresso nas Trilhas de Estudo (FocoVest)", 30f, 130f, paint)

            paint.color = 0xFFE0E0E0.toInt()
            canvas2.drawLine(30f, 140f, 565f, 140f, paint)

            var yOffset2 = 165f
            if (subjectsList.isEmpty()) {
                paint.color = 0xFF777777.toInt()
                paint.textSize = 11f
                paint.isFakeBoldText = false
                canvas2.drawText("Nenhum assunto cadastrado nas Trilhas de Estudo.", 45f, yOffset2, paint)
                yOffset2 += 25f
            } else {
                subjectsList.take(8).forEach { subject ->
                    val stepsList = listOf(
                        subject.stepAula, subject.stepResumo, subject.stepAutoexplicacao,
                        subject.stepExercicios, subject.stepCadernoErros, subject.stepRevisao,
                        subject.stepSimulado
                    )
                    val completedCount = stepsList.count { it }
                    val progressPercent = (completedCount.toFloat() / 7f)

                    paint.color = 0xFF222222.toInt()
                    paint.textSize = 11f
                    paint.isFakeBoldText = true
                    canvas2.drawText(subject.title, 30f, yOffset2, paint)

                    paint.textAlign = Paint.Align.RIGHT
                    paint.color = 0xFF0D47A1.toInt()
                    canvas2.drawText("$completedCount / 7 etapas concluídas", 565f, yOffset2, paint)
                    paint.textAlign = Paint.Align.LEFT

                    // Progress bar track
                    paint.color = 0xFFEEEEEE.toInt()
                    canvas2.drawRoundRect(30f, yOffset2 + 5f, 565f, yOffset2 + 11f, 3f, 3f, paint)

                    // Fill progress bar
                    paint.color = 0xFF1976D2.toInt()
                    val progressWidth = 30f + (535f * progressPercent)
                    if (progressWidth > 30f) {
                        canvas2.drawRoundRect(30f, yOffset2 + 5f, progressWidth, yOffset2 + 11f, 3f, 3f, paint)
                    }

                    // Metadata label
                    paint.color = 0xFF666666.toInt()
                    paint.textSize = 9f
                    paint.isFakeBoldText = false
                    canvas2.drawText("Matéria: ${subject.category}", 30f, yOffset2 + 22f, paint)

                    yOffset2 += 36f
                }
            }

            // Section: Cronograma de Estudos Customizados
            yOffset2 += 15f
            paint.color = 0xFF000000.toInt()
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas2.drawText("3. Cronograma de Estudos Personalizado (FocoVest)", 30f, yOffset2, paint)

            paint.color = 0xFFE0E0E0.toInt()
            canvas2.drawLine(30f, yOffset2 + 10f, 565f, yOffset2 + 10f, paint)
            yOffset2 += 30f

            if (customCronogramaList.isEmpty()) {
                paint.color = 0xFF777777.toInt()
                paint.textSize = 11f
                paint.isFakeBoldText = false
                canvas2.drawText("Nenhum item cadastrado no seu cronograma personalizado.", 45f, yOffset2, paint)
            } else {
                customCronogramaList.take(12).forEach { cronoItem ->
                    paint.color = if (cronoItem.isCompleted) 0xFF2E7D32.toInt() else 0xFF777777.toInt()
                    paint.textSize = 11f
                    paint.isFakeBoldText = true
                    val statusBox = if (cronoItem.isCompleted) "[Sim]" else "[Não]"
                    canvas2.drawText(statusBox, 45f, yOffset2, paint)

                    paint.color = 0xFF333333.toInt()
                    paint.isFakeBoldText = false
                    canvas2.drawText(cronoItem.content, 90f, yOffset2, paint)

                    paint.color = 0xFF666666.toInt()
                    paint.textSize = 10f
                    canvas2.drawText("${cronoItem.week} • ${cronoItem.dateInterval}", 410f, yOffset2, paint)

                    paint.color = 0xFFF5F5F5.toInt()
                    canvas2.drawLine(30f, yOffset2 + 6f, 565f, yOffset2 + 6f, paint)

                    yOffset2 += 22f
                }
            }

            // Page 2 Footer
            paint.color = 0xFF999999.toInt()
            paint.textSize = 9f
            paint.isFakeBoldText = false
            canvas2.drawText("Gerado localmente e criptografado por RitVida • Página 2 de 2", 30f, 815f, paint)

            pdfDocument.finishPage(page2)

            // Save the document to cache directory
            val file = File(context.cacheDir, "relatorio_desempenho_ritvida.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // Sharing/Viewing Action Intent
            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Compartilhar Relatório PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            Toast.makeText(context, "Relatório PDF gerado com sucesso!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao gerar PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
