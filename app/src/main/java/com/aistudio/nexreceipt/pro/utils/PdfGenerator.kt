package com.aistudio.nexreceipt.pro.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.aistudio.nexreceipt.pro.data.model.Receipt
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generatePdf(context: Context, receipt: Receipt): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Paints
        val paintTextDark = Paint().apply {
            color = Color.rgb(31, 41, 55) // Slate 800
            textSize = 12f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val paintTextLabel = Paint().apply {
            color = Color.rgb(100, 116, 139) // Slate 500
            textSize = 10f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val paintTextBold = Paint().apply {
            color = Color.rgb(15, 23, 42) // Slate 900
            textSize = 12f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintHeaderTitle = Paint().apply {
            color = Color.rgb(2, 138, 91) // Theme Green (Primary)
            textSize = 24f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }

        val paintHeaderSubtitle = Paint().apply {
            color = Color.rgb(13, 110, 253) // Theme Blue (Secondary)
            textSize = 14f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintLine = Paint().apply {
            color = Color.rgb(226, 232, 240) // Slate 200
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        val paintAccentLine = Paint().apply {
            color = Color.rgb(2, 138, 91)
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }

        val paintTableHeadBg = Paint().apply {
            color = Color.rgb(241, 245, 249) // Slate 100
            style = Paint.Style.FILL
        }

        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

        var currentY = 50f

        // 1. Draw Title Header
        canvas.drawText(receipt.type.uppercase(), 40f, currentY + 20f, paintHeaderTitle)
        canvas.drawText(receipt.businessName, 40f, currentY + 40f, paintHeaderSubtitle)

        // Draw Meta Info (top right)
        val formattedDate = dateFormatter.format(Date(receipt.timestamp))
        canvas.drawText("No: ${receipt.receiptNumber}", 400f, currentY + 15f, paintTextBold)
        canvas.drawText("Date: $formattedDate", 400f, currentY + 32f, paintTextLabel)
        canvas.drawText("Status: ${receipt.paymentStatus}", 400f, currentY + 49f, paintTextBold)

        currentY += 80f
        canvas.drawLine(40f, currentY, 555f, currentY, paintAccentLine)
        currentY += 25f

        // 2. Bill To Block
        canvas.drawText("CLIENT / BILL TO:", 40f, currentY, paintTextLabel)
        canvas.drawText(receipt.customerName, 40f, currentY + 18f, paintTextBold)
        canvas.drawText("Payment Method: ${receipt.paymentMethod}", 40f, currentY + 34f, paintTextDark)

        if (receipt.dueDate != null) {
            val formattedDueDate = dateFormatter.format(Date(receipt.dueDate))
            canvas.drawText("Due Date: $formattedDueDate", 400f, currentY + 18f, paintTextBold)
        }

        currentY += 60f

        // 3. Draw Table Header
        canvas.drawRect(40f, currentY, 555f, currentY + 26f, paintTableHeadBg)
        canvas.drawText("ITEM DESCRIPTION", 50f, currentY + 17f, paintTextBold)
        canvas.drawText("QTY", 320f, currentY + 17f, paintTextBold)
        canvas.drawText("UNIT PRICE", 390f, currentY + 17f, paintTextBold)
        canvas.drawText("TOTAL", 490f, currentY + 17f, paintTextBold)

        currentY += 26f

        // Draw Table Items
        for (item in receipt.items) {
            canvas.drawLine(40f, currentY, 555f, currentY, paintLine)
            currentY += 25f
            canvas.drawText(item.name, 50f, currentY - 8f, paintTextDark)
            canvas.drawText(item.quantity.toString(), 320f, currentY - 8f, paintTextDark)
            canvas.drawText(currencyFormatter.format(item.unitPrice), 390f, currentY - 8f, paintTextDark)
            canvas.drawText(currencyFormatter.format(item.total), 490f, currentY - 8f, paintTextBold)
        }

        canvas.drawLine(40f, currentY, 555f, currentY, paintAccentLine)
        currentY += 25f

        // 4. Draw Totals block
        val colLabelX = 350f
        val colValueX = 480f

        canvas.drawText("Subtotal:", colLabelX, currentY, paintTextLabel)
        canvas.drawText(currencyFormatter.format(receipt.subtotal), colValueX, currentY, paintTextDark)
        currentY += 20f

        if (receipt.discountAmount > 0) {
            canvas.drawText("Discount (${receipt.discountRate}%):", colLabelX, currentY, paintTextLabel)
            canvas.drawText("-${currencyFormatter.format(receipt.discountAmount)}", colValueX, currentY, paintTextDark)
            currentY += 20f
        }

        if (receipt.taxAmount > 0) {
            canvas.drawText("Tax (${receipt.taxRate}%):", colLabelX, currentY, paintTextLabel)
            canvas.drawText("+${currencyFormatter.format(receipt.taxAmount)}", colValueX, currentY, paintTextDark)
            currentY += 20f
        }

        canvas.drawLine(colLabelX - 10f, currentY - 5f, 555f, currentY - 5f, paintLine)

        paintTextBold.textSize = 14f
        canvas.drawText("TOTAL AMOUNT:", colLabelX, currentY + 15f, paintTextBold)
        canvas.drawText(currencyFormatter.format(receipt.totalAmount), colValueX, currentY + 15f, paintTextBold)
        paintTextBold.textSize = 12f

        currentY += 60f

        // 5. Notes Section
        if (receipt.notes.isNotEmpty()) {
            canvas.drawText("NOTES / PAYMENT INSTRUCTIONS:", 40f, currentY, paintTextLabel)
            canvas.drawText(receipt.notes, 40f, currentY + 18f, paintTextDark)
            currentY += 50f
        }

        // Draw Stamp
        if (receipt.paymentStatus == "PAID") {
            val paintStamp = Paint().apply {
                color = Color.argb(45, 2, 138, 91) // semi transparent emerald green
                strokeWidth = 3f
                style = Paint.Style.STROKE
                isAntiAlias = true
            }
            canvas.drawCircle(150f, currentY + 50f, 40f, paintStamp)
            val paintStampText = Paint().apply {
                color = Color.argb(160, 2, 138, 91)
                textSize = 18f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("PAID", 150f, currentY + 56f, paintStampText)
        }

        // 6. Footer
        val footerY = 780f
        canvas.drawLine(40f, footerY, 555f, footerY, paintLine)

        val paintFooterText = Paint().apply {
            color = Color.rgb(148, 163, 184) // Slate 400
            textSize = 9f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Generated Offline via NexReceipt Pro", 297f, footerY + 18f, paintFooterText)
        canvas.drawText("Developer: Prince AR Abdur Rahman | NexVora Lab's Ofc | Version 1.0.0", 297f, footerY + 30f, paintFooterText)

        pdfDocument.finishPage(page)

        // Write output to cache file
        val file = File(context.cacheDir, "Receipt_${receipt.receiptNumber}.pdf")
        try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }

        pdfDocument.close()
        return file
    }

    // Save PDF directly to the system Downloads directory using MediaStore for broad compatibility and security.
    fun savePdfToDownloads(context: Context, receipt: Receipt): Uri? {
        val tempFile = generatePdf(context, receipt) ?: return null
        
        val fileName = "${receipt.type}_${receipt.receiptNumber}.pdf"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/NexReceiptPro")
            }
            
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                try {
                    resolver.openOutputStream(uri)?.use { output ->
                        tempFile.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                    Toast.makeText(context, "Saved to Downloads/NexReceiptPro/!", Toast.LENGTH_LONG).show()
                    return uri
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            // Android 9 and below
            val targetDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NexReceiptPro")
            if (!targetDir.exists()) targetDir.mkdirs()
            val targetFile = File(targetDir, fileName)
            try {
                tempFile.copyTo(targetFile, overwrite = true)
                Toast.makeText(context, "Saved to Downloads/NexReceiptPro/!", Toast.LENGTH_LONG).show()
                return Uri.fromFile(targetFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}
