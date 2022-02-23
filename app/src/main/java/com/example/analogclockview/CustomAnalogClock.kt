package com.example.analogclockview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class CustomAnalogClock : View {
    private var mHeight = 0
    private var mWidth = 0
    private val mClockHours = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private var mPadding = 0
    private val mNumeralSpacing = 0
    private var mHandTruncation = 0
    private var mHourHandTruncation = 0
    private var mRadius = 0
    private var mPaint: Paint? = null
    private val mRect = Rect()
    private var isInit = false

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        if (!isInit) {
            mPaint = Paint()
            mHeight = height
            mWidth = width
            mPadding = mNumeralSpacing + 50
            val minAttr = mHeight.coerceAtMost(mWidth)
            mRadius = minAttr / 2 - mPadding
            Log.d("TAG", mRadius.toString())

            mHandTruncation = minAttr / 14
            mHourHandTruncation = minAttr / 17
            isInit = true
        }

        canvas.drawColor(Color.WHITE)
        drawCircleBorder(canvas)
        drawNumberLines(canvas)
    }

    private fun drawHandLine(canvas: Canvas, moment: Double, isHour: Boolean, isSecond: Boolean) {
        val angle = Math.PI * moment / 30 - Math.PI / 2
        val handRadius =
            if (isHour) mRadius - mHandTruncation - mHourHandTruncation else mRadius - mHandTruncation
        if (isSecond) mPaint!!.color = Color.BLACK
        canvas.drawLine((mWidth / 2).toFloat(), (mHeight / 2).toFloat(),
            (mWidth / 2 + cos(angle) * handRadius).toFloat(),
            (mHeight / 2 + sin(angle) * handRadius).toFloat(), mPaint!!)
    }

    private fun drawNumberLines(canvas: Canvas) {
        val fontSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, resources.displayMetrics)
                .toInt()
        mPaint!!.textSize = fontSize.toFloat()
        for (hour: Int in mClockHours) {
            val tmp = hour.toString()
            mPaint!!.getTextBounds(tmp, 0, tmp.length, mRect)
            val calendar = Calendar.getInstance()
            val angle = Math.PI / 6 * (hour - 3)
            val x = (mWidth / 2 + Math.cos(angle) * mRadius - mRect.width() / 2).toInt()
            val y = (mHeight / 2 + Math.sin(angle) * mRadius + mRect.height() / 2).toInt()
            canvas.drawText(hour.toString(), x.toFloat(), y.toFloat(), mPaint!!)
            Log.d("TAG", (mWidth / 2).toString() + "  " + (mHeight / 2).toString())
            var hour = calendar[Calendar.HOUR_OF_DAY].toFloat()
            hour = if (hour > 12) hour - 12 else hour
            drawHandLine(canvas,
                ((hour + calendar[Calendar.MINUTE] / 60) * 5f).toDouble(),
                true,
                false)
            drawHandLine(canvas, calendar[Calendar.MINUTE].toDouble(), false, false)
            drawHandLine(canvas, calendar[Calendar.SECOND].toDouble(), false, true)
            postInvalidateDelayed(500)
            invalidate()
        }
    }

    private fun drawCircleBorder(canvas: Canvas) {
        mPaint!!.reset()
        mPaint!!.color = Color.BLACK
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = 5f
        mPaint!!.isAntiAlias = true
        canvas.drawCircle((mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mRadius + mPadding - 10).toFloat(),
            mPaint!!)
    }
}