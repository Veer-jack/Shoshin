package com.example.shoshinapp.utils

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.shoshinapp.R
import java.io.File
import java.io.FileOutputStream

class ShareCardGenerator(private val context: Context) {

    private val cardWidth = 1080
    private val cardHeight = 1350

    fun generateCard(
        cardStyle: String,
        mainValue: String,
        subValue: String,
        kicker: String
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background - Ink Color (#1C1C1E)
        val bgPaint = Paint().apply { color = Color.parseColor("#1C1C1E") }
        canvas.drawRect(0f, 0f, cardWidth.toFloat(), cardHeight.toFloat(), bgPaint)

        // 2. Enso Motif (Decorative Arc)
        val ensoPaint = Paint().apply {
            color = Color.parseColor("#C84B31")
            alpha = 76 // ~0.3 opacity
            style = Paint.Style.STROKE
            strokeWidth = 25f
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        val ensoSize = 600f
        val rect = RectF(
            cardWidth - ensoSize / 1.5f,
            cardHeight - ensoSize / 1.5f,
            cardWidth + ensoSize / 3f,
            cardHeight + ensoSize / 3f
        )
        canvas.drawArc(rect, -90f, 310f, false, ensoPaint)

        // 3. LogoMark Placeholder (White Circle)
        val logoPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
        }
        canvas.drawCircle((cardWidth / 2).toFloat(), 200f, 40f, logoPaint)

        // 4. Kicker - Red Accent
        val kickerPaint = TextPaint().apply {
            color = Color.parseColor("#C84B31")
            textSize = 42f
            letterSpacing = 0.2f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(kicker, (cardWidth / 2).toFloat(), 340f, kickerPaint)

        // 5. Main Value (Big Number or Text)
        val mainPaint = TextPaint().apply {
            color = Color.parseColor("#F2F1EC") // ShPaper
            textSize = when {
                mainValue.length > 12 -> 80f
                mainValue.length > 5 -> 140f
                else -> 220f
            }
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(mainValue, (cardWidth / 2).toFloat(), 580f, mainPaint)

        // 6. Sub Value (Description)
        val subPaint = TextPaint().apply {
            color = Color.parseColor("#F2F1EC")
            alpha = 165 // ~0.65 opacity
            textSize = 50f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        drawMultiLineText(canvas, subValue, (cardWidth / 2).toFloat(), 680f, subPaint, 920)

        // 7. "初心" (Shoshin) in JP
        val jpPaint = TextPaint().apply {
            color = Color.parseColor("#C84B31")
            alpha = 180
            textSize = 55f
            letterSpacing = 0.3f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        canvas.drawText("初心", (cardWidth / 2).toFloat(), 950f, jpPaint)

        return bitmap
    }

    fun generateStreakCard(
        streak: Int,
        habitName: String,
        description: String,
        startDate: String,
        isMilestone: Boolean = false
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background Gradient
        val (topColor, bottomColor) = if (isMilestone) getMilestoneGradientColors(streak) else getGradientColors(streak)
        val gradient = LinearGradient(
            0f, 0f, 0f, cardHeight.toFloat(),
            topColor, bottomColor, Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply { shader = gradient }
        canvas.drawRect(0f, 0f, cardWidth.toFloat(), cardHeight.toFloat(), bgPaint)

        // 2. Draw Header
        val textPaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 60f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        if (isMilestone) {
            canvas.drawText("🎉 MILESTONE UNLOCKED", (cardWidth / 2).toFloat(), 120f, textPaint)
            val milestoneTitle = getMilestoneTitle(streak)
            textPaint.textSize = 50f
            canvas.drawText(milestoneTitle, (cardWidth / 2).toFloat(), 200f, textPaint)
        } else {
            canvas.drawText("Shoshin", (cardWidth / 2).toFloat(), 120f, textPaint)
        }

        // 3. Draw Streak Section
        textPaint.textSize = 220f
        val streakText = "$streak"
        canvas.drawText(streakText, (cardWidth / 2).toFloat(), 480f, textPaint)

        textPaint.textSize = 60f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("DAYS IN A ROW", (cardWidth / 2).toFloat(), 560f, textPaint)

        // 4. Draw Habit Name
        textPaint.textSize = 65f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(habitName, (cardWidth / 2).toFloat(), 720f, textPaint)

        // 5. Draw Start Date
        textPaint.textSize = 45f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.parseColor("#E0E0E0")
        canvas.drawText("Started: $startDate", (cardWidth / 2).toFloat(), 800f, textPaint)

        // 6. Draw Quote
        textPaint.color = Color.WHITE
        textPaint.textSize = 55f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        val quote = getQuote(streak)
        drawMultiLineText(canvas, quote, (cardWidth / 2).toFloat(), 950f, textPaint, 900)

        // 7. Draw Custom Description
        if (description.isNotEmpty()) {
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.textSize = 50f
            drawMultiLineText(canvas, description, (cardWidth / 2).toFloat(), 1120f, textPaint, 900)
        }

        // 8. Draw Footer
        textPaint.textSize = 40f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.color = Color.parseColor("#CCCCCC")
        canvas.drawText("Shoshin App #MorningHabits", (cardWidth / 2).toFloat(), 1290f, textPaint)

        return bitmap
    }

    private fun getGradientColors(streak: Int): Pair<Int, Int> {
        return when {
            streak >= 100 -> Color.parseColor("#E91E63") to Color.parseColor("#880E4F")
            streak >= 31 -> Color.parseColor("#FF9800") to Color.parseColor("#E65100")
            streak >= 8 -> Color.parseColor("#4A7C59") to Color.parseColor("#1B5E20")
            else -> Color.parseColor("#FFC107") to Color.parseColor("#FFA000")
        }
    }

    private fun getMilestoneGradientColors(streak: Int): Pair<Int, Int> {
        // Milestone gradients are more vibrant
        return when {
            streak >= 365 -> Color.parseColor("#FFD700") to Color.parseColor("#B8860B") // Gold
            streak >= 100 -> Color.parseColor("#C0C0C0") to Color.parseColor("#808080") // Silver/Platinum
            else -> Color.parseColor("#1C1C1E") to Color.parseColor("#4A7C59") // Default
        }
    }

    private fun getMilestoneTitle(streak: Int): String {
        return when {
            streak >= 365 -> "🌟 ONE YEAR UNSTOPPABLE!"
            streak >= 100 -> "👑 LEGEND STATUS!"
            streak >= 30 -> "🏆 MONTHLY MASTER!"
            streak >= 7 -> "🥈 FIRST WEEK CHAMPION!"
            else -> "MILESTONE REACHED!"
        }
    }

    private fun getQuote(streak: Int): String {
        return when {
            streak >= 100 -> "\"I am unstoppable when I commit to my goals\""
            streak >= 31 -> "\"Small daily progress adds up to big results\""
            streak >= 8 -> "\"Consistency is the foundation of excellence\""
            else -> "\"Every morning is a chance to become the person you want to be\""
        }
    }

    private fun drawMultiLineText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        paint: TextPaint,
        width: Int
    ) {
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setMaxLines(3)
            .build()

        canvas.save()
        // The x provided is the center. StaticLayout draws from 0 to width.
        // So we translate to x - width/2 to center the layout block.
        canvas.translate(x - width / 2f, y)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    fun saveBitmapToFile(bitmap: Bitmap, filename: String): File? {
        val file = File(context.cacheDir, "$filename.png")
        return try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
