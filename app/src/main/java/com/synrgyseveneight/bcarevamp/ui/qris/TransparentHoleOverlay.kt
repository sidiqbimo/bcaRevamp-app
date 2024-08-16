package com.synrgyseveneight.bcarevamp.ui.qris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.synrgyseveneight.bcarevamp.R

class TransparentHoleOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#80000000") // Semi-transparent gray
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val holeRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the semi-transparent gray overlay
        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        // Cover the entire view with the overlay
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        // Find the position of the QR code scanner square
        val holeView = (parent as View).findViewById<View>(R.id.qrCodeReaderSquare)
        val location = IntArray(2)
        holeView.getLocationInWindow(location)

        // Set the position and size of the rounded rectangle hole
        val cornerRadius = 50f // Adjust this value to change the roundness of the corners
        holeRect.set(
            location[0].toFloat(),
            location[1].toFloat(),
            (location[0] + holeView.width).toFloat(),
            (location[1] + holeView.height).toFloat()
        )

        // Clear the rounded rectangle area
        canvas.drawRoundRect(holeRect, cornerRadius, cornerRadius, clearPaint)

        canvas.restoreToCount(saveCount)
    }
}
