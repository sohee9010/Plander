package com.example.timecapsule.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.timecapsule.R
import com.example.timecapsule.data.model.GoalCapsule
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.Random
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.abs

class BottleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), SensorEventListener {

    interface OnMarbleClickListener {
        fun onMarbleClick(goal: GoalCapsule)
    }
    var onMarbleClickListener: OnMarbleClickListener? = null

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val tossfaceTypeface: Typeface by lazy {
        ResourcesCompat.getFont(context, R.font.tossface_emoji) ?: Typeface.DEFAULT
    }

    private val marbles = mutableListOf<Marble>()
    private var cachedGoals: List<GoalCapsule>? = null
    private val random = Random()

    private var draggingMarble: Marble? = null
    private var touchDownX = 0f
    private var touchDownY = 0f
    private var isDragging = false

    private val defaultColors by lazy { context.resources.getIntArray(R.array.marble_colors_default) }
    private val dday10Colors by lazy { context.resources.getIntArray(R.array.marble_colors_dday10) }
    private val dday5Colors by lazy { context.resources.getIntArray(R.array.marble_colors_dday5) }

    private var gravityX = 0f
    private var gravityY = 250f

    private val updateHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (!isDragging) {
                updatePhysics()
            }
            invalidate()
            updateHandler.postDelayed(this, 16)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width == 0 || height == 0) return

        if (marbles.isEmpty() && !cachedGoals.isNullOrEmpty()) {
            createMarbles()
        }

        drawBottle(canvas)
        marbles.forEach { it.draw(canvas, tossfaceTypeface) }
    }

    private fun createMarbles() {
        val bottleWidth = width * 0.8f
        val bottleLeft = (width - bottleWidth) / 2
        cachedGoals?.forEach { goal ->
            val dday = ChronoUnit.DAYS.between(LocalDate.now(), goal.targetDate)
            val colorArray = when {
                dday <= 5 -> dday5Colors
                dday <= 10 -> dday10Colors
                else -> defaultColors
            }
            val randomColor = colorArray[random.nextInt(colorArray.size)]
            val randomRadius = random.nextFloat() * 40f + 90f
            val randomX = bottleLeft + randomRadius + random.nextFloat() * (bottleWidth - 2 * randomRadius)
            marbles.add(Marble(x = randomX, y = height * 0.8f, radius = randomRadius, color = randomColor, goal = goal))
        }
    }

    private fun drawBottle(canvas: Canvas) {
        val bottleHeight = height * 0.75f
        val bottleTop = height * 0.20f
        val bottleWidth = width * 0.8f
        val bottleLeft = (width - bottleWidth) / 2
        val bottleRect = RectF(bottleLeft, bottleTop, bottleLeft + bottleWidth, bottleTop + bottleHeight)

        // Bottle Gradient
        val bottleGradient = LinearGradient(bottleLeft, bottleTop, bottleLeft + bottleWidth, bottleTop, intArrayOf(Color.parseColor("#DDEFFD"), Color.parseColor("#A8D8F0")), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        val bottlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = bottleGradient; style = Paint.Style.FILL }
        canvas.drawRoundRect(bottleRect, 40f, 40f, bottlePaint)

        // Cap Gradient
        val capHeight = 60f
        val capWidth = bottleWidth * 0.6f
        val capLeft = (width - capWidth) / 2
        val capRect = RectF(capLeft, bottleTop - capHeight, capLeft + capWidth, bottleTop)
        val capGradient = LinearGradient(capLeft, bottleTop - capHeight, capLeft, bottleTop, intArrayOf(Color.parseColor("#A1887F"), Color.parseColor("#795548")), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        val capPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = capGradient; style = Paint.Style.FILL }
        canvas.drawRoundRect(capRect, 15f, 15f, capPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        val dragThreshold = 10f

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                findMarbleAt(touchX, touchY)?.let {
                    draggingMarble = it
                    touchDownX = touchX
                    touchDownY = touchY
                    isDragging = false
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                draggingMarble?.let {
                    val dx = abs(touchX - touchDownX)
                    val dy = abs(touchY - touchDownY)
                    if (dx > dragThreshold || dy > dragThreshold) {
                        isDragging = true
                    }
                    if(isDragging) {
                        it.x = touchX
                        it.y = touchY
                        invalidate()
                    }
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                if (draggingMarble != null) {
                    if (!isDragging) {
                        // It's a click
                        onMarbleClickListener?.onMarbleClick(draggingMarble!!.goal)
                    }
                    // End of drag or click
                    draggingMarble = null
                    isDragging = false
                    invalidate()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun findMarbleAt(x: Float, y: Float): Marble? {
        return marbles.asReversed().find { marble ->
            val dx = x - marble.x
            val dy = y - marble.y
            dx * dx + dy * dy < marble.radius * marble.radius
        }
    }

    private fun updatePhysics() {
        if (width == 0 || height == 0 || draggingMarble != null) return

        val bottleHeight = height * 0.75f
        val bottleTop = height * 0.20f
        val bottleWidth = width * 0.8f
        val bottleLeft = (width - bottleWidth) / 2
        val bottleRight = bottleLeft + bottleWidth
        val bottleBottom = bottleTop + bottleHeight

        marbles.forEach { marble ->
            marble.velocityX += gravityX * 0.016f
            marble.velocityY += gravityY * 0.016f
            marble.velocityX *= 0.98f
            marble.velocityY *= 0.98f
            marble.x += marble.velocityX
            marble.y += marble.velocityY

            if (marble.x - marble.radius < bottleLeft) { marble.x = bottleLeft + marble.radius; marble.velocityX = -marble.velocityX * 0.7f }
            if (marble.x + marble.radius > bottleRight) { marble.x = bottleRight - marble.radius; marble.velocityX = -marble.velocityX * 0.7f }
            if (marble.y - marble.radius < bottleTop) { marble.y = bottleTop + marble.radius; marble.velocityY = -marble.velocityY * 0.7f }
            if (marble.y + marble.radius > bottleBottom) { marble.y = bottleBottom - marble.radius; marble.velocityY = -marble.velocityY * 0.7f }
        }

        for (i in marbles.indices) {
            for (j in i + 1 until marbles.size) {
                checkCollision(marbles[i], marbles[j])
            }
        }
    }

    private fun checkCollision(m1: Marble, m2: Marble) {
        val dx = m2.x - m1.x
        val dy = m2.y - m1.y
        val distance = sqrt(dx * dx + dy * dy)
        val minDistance = m1.radius + m2.radius

        if (distance < minDistance) {
            val angle = atan2(dy, dx)
            val overlap = minDistance - distance
            val totalMass = m1.mass + m2.mass

            m1.x -= cos(angle) * overlap * (m2.mass / totalMass)
            m1.y -= sin(angle) * overlap * (m2.mass / totalMass)
            m2.x += cos(angle) * overlap * (m1.mass / totalMass)
            m2.y += sin(angle) * overlap * (m1.mass / totalMass)

            val tempVx = m1.velocityX
            val tempVy = m1.velocityY
            m1.velocityX = m2.velocityX * 0.8f
            m1.velocityY = m2.velocityY * 0.8f
            m2.velocityX = tempVx * 0.8f
            m2.velocityY = tempVy * 0.8f
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val tiltMultiplier = 80f
            gravityX = -event.values[0] * tiltMultiplier
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun resumeSensor() {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        updateHandler.post(updateRunnable)
    }

    fun pauseSensor() {
        sensorManager.unregisterListener(this)
        updateHandler.removeCallbacks(updateRunnable)
    }

    fun updateMarbles(goals: List<GoalCapsule>) {
        cachedGoals = goals
        marbles.clear()
        invalidate()
    }
}

data class Marble(
    var x: Float, var y: Float, val radius: Float, val color: Int, val goal: GoalCapsule,
    var velocityX: Float = 0f, var velocityY: Float = 0f
) {
    val mass = radius * radius
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
    }

    fun draw(canvas: Canvas, typeface: Typeface) {
        val lightX = x - radius * 0.3f
        val lightY = y - radius * 0.3f
        val lighterColor = Color.argb(255, (Color.red(color) * 1.2f).coerceAtMost(255f).toInt(), (Color.green(color) * 1.2f).coerceAtMost(255f).toInt(), (Color.blue(color) * 1.2f).coerceAtMost(255f).toInt())

        paint.shader = RadialGradient(lightX, lightY, radius * 1.5f, lighterColor, color, Shader.TileMode.CLAMP)
        canvas.drawCircle(x, y, radius, paint)
        paint.shader = null

        val initial = goal.goalName.take(3)
        val isNumeric = initial.all { it.isDigit() }

        textPaint.typeface = if (isNumeric) Typeface.DEFAULT_BOLD else typeface
        textPaint.color = Color.BLACK
        val textSize = radius * 0.5f
        textPaint.textSize = textSize

        val yOffset = textPaint.textSize / 3

        canvas.drawText(initial, x, y + yOffset, textPaint)
    }
}
