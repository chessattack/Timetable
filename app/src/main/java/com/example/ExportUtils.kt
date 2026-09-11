package com.example

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ExportUtils {
    fun shareArrangementSheet(context: Context, sheetData: ArrangementSheetData) {
        val bitmap = generateBitmap(sheetData)
        
        val exportsDir = File(context.cacheDir, "exports")
        exportsDir.mkdirs()
        
        val sanitizedDate = sheetData.dateString.replace("/", "-")
        
        // 1. Save PNG
        val pngFile = File(exportsDir, "ArrangementSheet_$sanitizedDate.png")
        FileOutputStream(pngFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        
        // 2. Save PDF
        val pdfFile = File(exportsDir, "ArrangementSheet_$sanitizedDate.pdf")
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        
        // 3. Share Intent
        val pngUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pngFile)
        val pdfUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(pngUri, pdfUri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Arrangement Sheet"))
    }
    
    private fun generateBitmap(sheetData: ArrangementSheetData): Bitmap {
        val rowHeight = 60f
        val headerHeight = 160f
        val padding = 40f
        val width = 1200
        val height = (headerHeight + rowHeight + (sheetData.rows.size * rowHeight) + (padding * 2)).toInt()
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Background
        canvas.drawColor(android.graphics.Color.parseColor("#FFF9C4")) // Light yellow
        
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 32f
            isAntiAlias = true
        }
        
        val boldPaint = Paint(paint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        // Title
        val title1 = "TIME TABLE (2026-27)"
        val title2 = "ARRANGEMENT SHEET"
        paint.textAlign = Paint.Align.CENTER
        boldPaint.textAlign = Paint.Align.CENTER
        boldPaint.textSize = 36f
        canvas.drawText(title1, width / 2f, padding + 36f, boldPaint)
        canvas.drawText(title2, width / 2f, padding + 80f, boldPaint)
        
        // Date
        boldPaint.textAlign = Paint.Align.LEFT
        boldPaint.textSize = 28f
        canvas.drawText("DATE: ${sheetData.dateString}", padding, padding + 140f, boldPaint)
        
        // Draw Table
        val startY = padding + 160f
        val startX = padding
        val endX = width - padding
        
        val tableWidth = endX - startX
        val partW = tableWidth / 12f
        
        val cols = floatArrayOf(
            startX,
            startX + partW * 1, // Sl
            startX + partW * 3, // Teacher
            startX + partW * 4, // 1
            startX + partW * 5, // 2
            startX + partW * 6, // 3
            startX + partW * 7, // 4
            startX + partW * 8, // Recess
            startX + partW * 9, // 5
            startX + partW * 10, // 6
            startX + partW * 11, // 7
            startX + partW * 12  // 8
        )
        
        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        
        fun drawCell(text: String, left: Float, top: Float, right: Float, bottom: Float, isBold: Boolean) {
            canvas.drawRect(left, top, right, bottom, linePaint)
            val p = if (isBold) boldPaint else paint
            p.textAlign = Paint.Align.CENTER
            p.textSize = 20f
            
            val fontMetrics = p.fontMetrics
            val textY = top + (bottom - top) / 2f - (fontMetrics.descent + fontMetrics.ascent) / 2f
            canvas.drawText(text, left + (right - left) / 2f, textY, p)
        }
        
        var y = startY
        // Header row
        drawCell("Sl.No", cols[0], y, cols[1], y + rowHeight, true)
        drawCell("TEACHER", cols[1], y, cols[2], y + rowHeight, true)
        drawCell("1", cols[2], y, cols[3], y + rowHeight, true)
        drawCell("2", cols[3], y, cols[4], y + rowHeight, true)
        drawCell("3", cols[4], y, cols[5], y + rowHeight, true)
        drawCell("4", cols[5], y, cols[6], y + rowHeight, true)
        drawCell("REC", cols[6], y, cols[7], y + rowHeight, true)
        drawCell("5", cols[7], y, cols[8], y + rowHeight, true)
        drawCell("6", cols[8], y, cols[9], y + rowHeight, true)
        drawCell("7", cols[9], y, cols[10], y + rowHeight, true)
        drawCell("8", cols[10], y, cols[11], y + rowHeight, true)
        
        y += rowHeight
        
        // Rows
        for (row in sheetData.rows) {
            drawCell(row.slNo.toString(), cols[0], y, cols[1], y + rowHeight, false)
            drawCell(row.absentTeacherName, cols[1], y, cols[2], y + rowHeight, false)
            drawCell(row.periodAssignments[0] ?: "", cols[2], y, cols[3], y + rowHeight, false)
            drawCell(row.periodAssignments[1] ?: "", cols[3], y, cols[4], y + rowHeight, false)
            drawCell(row.periodAssignments[2] ?: "", cols[4], y, cols[5], y + rowHeight, false)
            drawCell(row.periodAssignments[3] ?: "", cols[5], y, cols[6], y + rowHeight, false)
            drawCell("", cols[6], y, cols[7], y + rowHeight, false)
            drawCell(row.periodAssignments[4] ?: "", cols[7], y, cols[8], y + rowHeight, false)
            drawCell(row.periodAssignments[5] ?: "", cols[8], y, cols[9], y + rowHeight, false)
            drawCell(row.periodAssignments[6] ?: "", cols[9], y, cols[10], y + rowHeight, false)
            drawCell(row.periodAssignments[7] ?: "", cols[10], y, cols[11], y + rowHeight, false)
            y += rowHeight
        }
        
        return bitmap
    }
}
