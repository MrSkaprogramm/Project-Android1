package com.mitra.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.mitra.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * Created by owais.ali on 6/20/2016.
 */
open class RangeSeekbar : View {


    //private static final int DEFAULT_THUMB_WIDTH = 80;
    //private static final int DEFAULT_THUMB_HEIGHT = 80;
    private val NO_STEP = -1f
    private val NO_FIXED_GAP = -1f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.RangeSeekbar)
        try {
            cornerRadius = getCornerRadius(array)
            minValue = getMinValue(array)
            maxValue = getMaxValue(array)
            minStartValue = getMinStartValue(array)
            maxStartValue = getMaxStartValue(array)
            steps = getSteps(array)
            gap = getGap(array)
            fixGap = getFixedGap(array)
            barColor = getBarColor(array)
            barHeight = getBarHeight(array)
            barHighlightColor = getBarHighlightColor(array)
            leftThumbColorNormal = getLeftThumbColor(array)
            rightThumbColorNormal = getRightThumbColor(array)
            leftThumbColorPressed = getLeftThumbColorPressed(array)
            rightThumbColorPressed = getRightThumbColorPressed(array)
            leftDrawable = getLeftDrawable(array)
            rightDrawable = getRightDrawable(array)
            leftDrawablePressed = getLeftDrawablePressed(array)
            rightDrawablePressed = getRightDrawablePressed(array)
            dataType = getDataType(array)
        } finally {
            array.recycle()
        }
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    //////////////////////////////////////////
    // PUBLIC CONSTANTS CLASS
    //////////////////////////////////////////
    object DataType {
        const val LONG = 0
        const val DOUBLE = 1
        const val INTEGER = 2
        const val FLOAT = 3
        const val SHORT = 4
        const val BYTE = 5
    }

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////
    private var onRangeSeekbarChangeListener: OnRangeSeekbarChangeListener? = null
    private var onRangeSeekbarFinalValueListener: OnRangeSeekbarFinalValueListener? = null
    private var absoluteMinValue = 0f
    private var absoluteMaxValue = 0f
    private var absoluteMinStartValue = 0f
    private var absoluteMaxStartValue = 0f
    private var minValue = 0f
    private var maxValue = 0f
    private var minStartValue = 0f
    private var maxStartValue = 0f
    private var steps = 0f
    private var gap = 0f
    private var fixGap = 0f
    private var mActivePointerId: Int = INVALID_POINTER_ID
    private var dataType = 0
    private var cornerRadius = 0f
    private var barColor = 0
    private var barHighlightColor = 0
    private var leftThumbColor = 0
    private var rightThumbColor = 0
    private var leftThumbColorNormal = 0
    private var leftThumbColorPressed = 0
    private var rightThumbColorNormal = 0
    private var rightThumbColorPressed = 0
    private var barPadding = 0f
    private var barHeight = 0f
    private var thumbWidth = 0f

    private var thumbHeight = 0f
    private var leftDrawable: Drawable? = null
    private var rightDrawable: Drawable? = null
    private var leftDrawablePressed: Drawable? = null
    private var rightDrawablePressed: Drawable? = null
    private var leftThumb: Bitmap? = null
    private var leftThumbPressed: Bitmap? = null
    private var rightThumb: Bitmap? = null
    private var rightThumbPressed: Bitmap? = null

    //////////////////////////////////////////
    // PROTECTED METHODS
    //////////////////////////////////////////
    protected var pressedThumb: Thumb? = null
        private set
    private var normalizedMinValue = 0.0
    private var normalizedMaxValue = 100.0
    private var pointerIndex = 0
    private var _rect: RectF? = null
    private var _paint: Paint? = null
    protected var leftThumbRect: RectF? = null
        private set
    protected var rightThumbRect: RectF? = null
        private set
    private var mIsDragging = false

    //////////////////////////////////////////
    // ENUMERATION
    //////////////////////////////////////////
    enum class Thumb {
        MIN, MAX
    }

    //////////////////////////////////////////
    // INITIALIZING
    //////////////////////////////////////////
    fun initView() {
        absoluteMinValue = minValue
        absoluteMaxValue = maxValue
        leftThumbColor = leftThumbColorNormal
        rightThumbColor = rightThumbColorNormal
        leftThumb = getBitmap(leftDrawable)
        rightThumb = getBitmap(rightDrawable)
        leftThumbPressed = getBitmap(leftDrawablePressed)
        rightThumbPressed = getBitmap(rightDrawablePressed)
        leftThumbPressed = if (leftThumbPressed == null) leftThumb else leftThumbPressed
        rightThumbPressed = if (rightThumbPressed == null) rightThumb else rightThumbPressed
        gap = Math.max(0f, Math.min(gap, absoluteMaxValue - absoluteMinValue))
        gap = gap / (absoluteMaxValue - absoluteMinValue) * 100
        if (fixGap != NO_FIXED_GAP) {
            fixGap = Math.min(fixGap, absoluteMaxValue)
            fixGap = fixGap / (absoluteMaxValue - absoluteMinValue) * 100
            addFixGap(true)
        }
        thumbWidth = getThumbWidth()
        thumbHeight = getThumbHeight()

        //thumbHalfWidth = thumbWidth / 2;
        //thumbHalfHeight = thumbHeight / 2;
        //barHeight = getBarHeight()
        barPadding = getBarPadding()
        _paint = Paint(Paint.ANTI_ALIAS_FLAG)
        _rect = RectF()
        leftThumbRect = RectF()
        rightThumbRect = RectF()
        pressedThumb = null
        setMinStartValue()
        setMaxStartValue()
    }

    //////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////
    fun setCornerRadius(cornerRadius: Float): RangeSeekbar {
        this.cornerRadius = cornerRadius
        return this
    }

    fun setMinValue(minValue: Float): RangeSeekbar {
        this.minValue = minValue
        absoluteMinValue = minValue
        return this
    }

    fun setMaxValue(maxValue: Float): RangeSeekbar {
        this.maxValue = maxValue
        absoluteMaxValue = maxValue
        return this
    }

    fun setMinStartValue(minStartValue: Float): RangeSeekbar {
        this.minStartValue = minStartValue
        absoluteMinStartValue = minStartValue
        return this
    }

    fun setMaxStartValue(maxStartValue: Float): RangeSeekbar {
        this.maxStartValue = maxStartValue
        absoluteMaxStartValue = maxStartValue
        return this
    }

    fun setSteps(steps: Float): RangeSeekbar {
        this.steps = steps
        return this
    }

    fun setGap(gap: Float): RangeSeekbar {
        this.gap = gap
        return this
    }

    fun setFixGap(fixGap: Float): RangeSeekbar {
        this.fixGap = fixGap
        return this
    }

    fun setBarColor(barColor: Int): RangeSeekbar {
        this.barColor = barColor
        return this
    }

    fun setBarHighlightColor(barHighlightColor: Int): RangeSeekbar {
        this.barHighlightColor = barHighlightColor
        return this
    }

    fun setLeftThumbColor(leftThumbColorNormal: Int): RangeSeekbar {
        this.leftThumbColorNormal = leftThumbColorNormal
        return this
    }

    fun setLeftThumbHighlightColor(leftThumbColorPressed: Int): RangeSeekbar {
        this.leftThumbColorPressed = leftThumbColorPressed
        return this
    }

    fun setLeftThumbDrawable(resId: Int): RangeSeekbar {
        setLeftThumbDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setLeftThumbDrawable(drawable: Drawable?): RangeSeekbar {
        setLeftThumbBitmap(getBitmap(drawable))
        return this
    }

    fun setLeftThumbBitmap(bitmap: Bitmap?): RangeSeekbar {
        leftThumb = bitmap
        return this
    }

    fun setLeftThumbHighlightDrawable(resId: Int): RangeSeekbar {
        setLeftThumbHighlightDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setLeftThumbHighlightDrawable(drawable: Drawable?): RangeSeekbar {
        setLeftThumbHighlightBitmap(getBitmap(drawable))
        return this
    }

    fun setLeftThumbHighlightBitmap(bitmap: Bitmap?): RangeSeekbar {
        leftThumbPressed = bitmap
        return this
    }

    fun setRightThumbColor(rightThumbColorNormal: Int): RangeSeekbar {
        this.rightThumbColorNormal = rightThumbColorNormal
        return this
    }

    fun setRightThumbHighlightColor(rightThumbColorPressed: Int): RangeSeekbar {
        this.rightThumbColorPressed = rightThumbColorPressed
        return this
    }

    fun setRightThumbDrawable(resId: Int): RangeSeekbar {
        setRightThumbDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setRightThumbDrawable(drawable: Drawable?): RangeSeekbar {
        setRightThumbBitmap(getBitmap(drawable))
        return this
    }

    fun setRightThumbBitmap(bitmap: Bitmap?): RangeSeekbar {
        rightThumb = bitmap
        return this
    }

    fun setRightThumbHighlightDrawable(resId: Int): RangeSeekbar {
        setRightThumbHighlightDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setRightThumbHighlightDrawable(drawable: Drawable?): RangeSeekbar {
        setRightThumbHighlightBitmap(getBitmap(drawable))
        return this
    }

    fun setRightThumbHighlightBitmap(bitmap: Bitmap?): RangeSeekbar {
        rightThumbPressed = bitmap
        return this
    }

    fun setDataType(dataType: Int): RangeSeekbar {
        this.dataType = dataType
        return this
    }

    fun setOnRangeSeekbarChangeListener(onRangeSeekbarChangeListener: OnRangeSeekbarChangeListener?) {
        this.onRangeSeekbarChangeListener = onRangeSeekbarChangeListener
        if (this.onRangeSeekbarChangeListener != null) {
            this.onRangeSeekbarChangeListener!!.valueChanged(
                selectedMinValue,
                selectedMaxValue
            )
        }
    }

    fun getOnRangeSeekbarChangeListener() = this.onRangeSeekbarChangeListener

    fun setOnRangeSeekbarFinalValueListener(onRangeSeekbarFinalValueListener: OnRangeSeekbarFinalValueListener?) {
        this.onRangeSeekbarFinalValueListener = onRangeSeekbarFinalValueListener
    }

    val selectedMinValue: Number
        get() {
            var nv = normalizedMinValue
            if (steps > 0 && steps <= absoluteMaxValue / 2) {
                val stp = steps / (absoluteMaxValue - absoluteMinValue) * 100
                val half_step = stp / 2.toDouble()
                val mod = nv % stp
                if (mod > half_step) {
                    nv = nv - mod
                    nv = nv + stp
                } else {
                    nv = nv - mod
                }
            } else {
                check(steps == NO_STEP) { "steps out of range $steps" }
            }
            return formatValue(normalizedToValue(nv))
        }

    val selectedMaxValue: Number
        get() {
            var normalizedValue = normalizedMaxValue
            if (steps > 0 && steps <= absoluteMaxValue / 2) {
                val stp = steps / (absoluteMaxValue - absoluteMinValue) * 100
                val halfStep = stp / 2.toDouble()
                val mod = normalizedValue % stp
                if (mod > halfStep) {
                    normalizedValue -= mod
                    normalizedValue += stp
                } else {
                    normalizedValue -= mod
                }
            } else {
                check(steps == NO_STEP) { "steps out of range $steps" }
            }
            return formatValue(normalizedToValue(normalizedValue))
        }

    fun apply() {

        // reset normalize min and max value
        normalizedMinValue = 0.0
        normalizedMaxValue = 100.0
        gap = max(0f, min(gap, absoluteMaxValue - absoluteMinValue))
        gap = gap / (absoluteMaxValue - absoluteMinValue) * 100

        if (fixGap != NO_FIXED_GAP) {
            fixGap = min(fixGap, absoluteMaxValue)
            fixGap = fixGap / (absoluteMaxValue - absoluteMinValue) * 100
            addFixGap(true)
        }

        thumbWidth = leftThumb?.width?.toFloat() ?: resources.getDimension(R.dimen.thumb_width)
        thumbHeight = rightThumb?.height?.toFloat() ?: resources.getDimension(R.dimen.thumb_height)

        //barHeight = thumbHeight * 0.5f * 0.3f
        barPadding = thumbWidth * 0.5f

        // set min start value
        if (minStartValue <= absoluteMinValue) {
            minStartValue = 0f
            setNormalizedMinValue(minStartValue.toDouble())
        } else if (minStartValue >= absoluteMaxValue) {
            minStartValue = absoluteMaxValue
            setMinStartValue()
        } else {
            setMinStartValue()
        }

        // set max start value
        if (maxStartValue <= absoluteMinStartValue || maxStartValue <= absoluteMinValue) {
            maxStartValue = 0f
            setNormalizedMaxValue(maxStartValue.toDouble())
        } else if (maxStartValue >= absoluteMaxValue) {
            maxStartValue = absoluteMaxValue
            setMaxStartValue()
        } else {
            setMaxStartValue()
        }
        invalidate()
        if (onRangeSeekbarChangeListener != null) {
            onRangeSeekbarChangeListener?.valueChanged(selectedMinValue, selectedMaxValue)
        }
    }

    protected fun getThumbWidth(): Float {
        return leftThumb?.width?.toFloat() ?: resources.getDimension(R.dimen.thumb_width)
    }

    protected fun getThumbHeight(): Float {
        return leftThumb?.height?.toFloat() ?: resources.getDimension(R.dimen.thumb_height)
    }

    private fun getBarHeight(typedArray: TypedArray): Float {
        return typedArray.getDimension(
            R.styleable.RangeSeekbar_bar_height,
            thumbHeight * 0.5f * 0.3f
        )
    }

    private fun getBarPadding(): Float {
        return thumbWidth * 0.5f
    }

    private fun getBitmap(drawable: Drawable?): Bitmap? {
        return if (drawable != null) (drawable as BitmapDrawable).bitmap else null
    }

    private fun getCornerRadius(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.RangeSeekbar_corner_radius, 0f)
    }

    private fun getMinValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.RangeSeekbar_min_value, 0f)
    }

    private fun getMaxValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.RangeSeekbar_max_value, 100f)
    }

    private fun getMinStartValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.RangeSeekbar_min_start_value, minValue)
    }

    private fun getMaxStartValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.RangeSeekbar_max_start_value, maxValue)
    }

    private fun getSteps(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.RangeSeekbar_steps, NO_STEP)
    }

    private fun getGap(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.RangeSeekbar_gap, 0f)
    }

    private fun getFixedGap(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.RangeSeekbar_fix_gap, NO_FIXED_GAP)
    }

    private fun getBarColor(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.RangeSeekbar_bar_color,
            Color.GRAY
        )
    }

    private fun getBarHighlightColor(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.RangeSeekbar_bar_highlight_color,
            Color.BLACK
        )
    }

    private fun getLeftThumbColor(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.RangeSeekbar_left_thumb_color,
            Color.BLACK
        )
    }

    private fun getRightThumbColor(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.RangeSeekbar_right_thumb_color,
            Color.BLACK
        )
    }

    private fun getLeftThumbColorPressed(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.RangeSeekbar_left_thumb_color_pressed,
            Color.DKGRAY
        )
    }

    private fun getRightThumbColorPressed(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.RangeSeekbar_right_thumb_color_pressed,
            Color.DKGRAY
        )
    }

    private fun getLeftDrawable(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.RangeSeekbar_left_thumb_image)
    }

    private fun getRightDrawable(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.RangeSeekbar_right_thumb_image)
    }

    private fun getLeftDrawablePressed(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.RangeSeekbar_left_thumb_image_pressed)
    }

    private fun getRightDrawablePressed(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.RangeSeekbar_right_thumb_image_pressed)
    }

    private fun getDataType(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.RangeSeekbar_data_type,
            RangeSeekbar.DataType.INTEGER
        )
    }

    private fun setupBar(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?
    ) {
        rect?.left = barPadding
        rect?.top = 0.5f * (height - barHeight)
        rect?.right = width - barPadding
        rect?.bottom = 0.5f * (height + barHeight)
        paint?.style = Paint.Style.FILL
        paint?.color = barColor
        paint?.isAntiAlias = true
        drawBar(canvas, paint, rect)
    }

    private fun drawBar(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?
    ) {
        canvas.drawRoundRect(rect!!, cornerRadius, cornerRadius, paint!!)
    }

    private fun setupHighlightBar(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?
    ) {
        rect!!.left = normalizedToScreen(normalizedMinValue) + getThumbWidth() / 2
        rect.right = normalizedToScreen(normalizedMaxValue) + getThumbWidth() / 2
        paint!!.color = barHighlightColor
        drawHighlightBar(canvas, paint, rect)
    }

    private fun drawHighlightBar(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?
    ) {
        canvas.drawRoundRect(rect!!, cornerRadius, cornerRadius, paint!!)
    }

    private fun setupLeftThumb(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?
    ) {
        leftThumbColor =
            if (Thumb.MIN == pressedThumb) leftThumbColorPressed else leftThumbColorNormal
        paint?.color = leftThumbColor
        leftThumbRect?.left = normalizedToScreen(normalizedMinValue)
        leftThumbRect?.right =
            (leftThumbRect?.left ?: 0 + getThumbWidth() / 2 + barPadding)
                .coerceAtMost(width.toFloat())
        leftThumbRect?.top = 0f
        leftThumbRect?.bottom = thumbHeight
        if (leftThumb != null) {
            val lThumb =
                if (Thumb.MIN == pressedThumb) leftThumbPressed else leftThumb
            drawLeftThumbWithImage(canvas, paint, leftThumbRect, lThumb)
        } else {
            drawLeftThumbWithColor(canvas, paint, leftThumbRect)
        }
    }

    protected open fun drawLeftThumbWithColor(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?
    ) {
        canvas.drawOval(rect!!, paint!!)
    }

    protected open fun drawLeftThumbWithImage(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?,
        image: Bitmap?
    ) {
        canvas.drawBitmap(image!!, rect!!.left, rect.top, paint)
    }

    private fun setupRightThumb(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?
    ) {
        rightThumbColor =
            if (RangeSeekbar.Thumb.MAX == pressedThumb) rightThumbColorPressed else rightThumbColorNormal
        paint!!.color = rightThumbColor

        //float leftR = normalizedToScreen(normalizedMaxValue);
        //float rightR = Math.min(leftR + thumbHalfWidth + barPadding, getWidth());
        rightThumbRect!!.left = normalizedToScreen(normalizedMaxValue)
        rightThumbRect!!.right = Math.min(
            rightThumbRect!!.left + getThumbWidth() / 2 + barPadding,
            width.toFloat()
        )
        rightThumbRect!!.top = 0f
        rightThumbRect!!.bottom = thumbHeight
        if (rightThumb != null) {
            val rThumb =
                if (RangeSeekbar.Thumb.MAX == pressedThumb) rightThumbPressed else rightThumb
            drawRightThumbWithImage(canvas, paint, rightThumbRect, rThumb)
        } else {
            drawRightThumbWithColor(canvas, paint, rightThumbRect)
        }
    }

    protected open fun drawRightThumbWithColor(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?
    ) {
        canvas.drawOval(rect!!, paint!!)
    }

    protected open fun drawRightThumbWithImage(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?,
        image: Bitmap?
    ) {
        canvas.drawBitmap(image!!, rect!!.left, rect.top, paint)
    }

    protected fun trackTouchEvent(event: MotionEvent) {
        val pointerIndex = event.findPointerIndex(mActivePointerId)
        try {
            val x = event.getX(pointerIndex)
            if (RangeSeekbar.Thumb.MIN == pressedThumb) {
                setNormalizedMinValue(screenToNormalized(x))
            } else if (RangeSeekbar.Thumb.MAX == pressedThumb) {
                setNormalizedMaxValue(screenToNormalized(x))
            }
        } catch (ignored: Exception) {
        }
    }

    protected open fun touchDown(x: Float, y: Float) {}
    protected fun touchMove(x: Float, y: Float) {}
    protected open fun touchUp(x: Float, y: Float) {
        setMinCurrentValue(selectedMinValue.toFloat())
        setMaxCurrentValue(selectedMaxValue.toFloat())
    }

    protected fun getMeasureSpecWith(widthMeasureSpec: Int): Int {
        var width = 200
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        }
        return width
    }

    protected fun getMeasureSpecHeight(heightMeasureSpec: Int): Int {
        var height = thumbHeight.roundToInt()
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = height.coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        }
        return height
    }

    protected fun log(`object`: Any) {
        Log.d("CRS=>", `object`.toString())
    }

    //////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////
    private fun setMinStartValue() {
        setMinCurrentValue(minStartValue)
    }

    private fun setMinCurrentValue(value : Float) {
        var tempValue = value
        if (tempValue in minValue..maxValue) {
            tempValue = min(tempValue, absoluteMaxValue)
            tempValue -= absoluteMinValue
            tempValue = tempValue / (absoluteMaxValue - absoluteMinValue) * 100
            setNormalizedMinValue(tempValue.toDouble())
        }
    }

    private fun setMaxStartValue() {
        setMaxCurrentValue(maxStartValue)
    }

    private fun setMaxCurrentValue(value: Float) {
        var tempValue = value
        if (tempValue in absoluteMinValue..absoluteMaxValue && tempValue > absoluteMinStartValue) {
            tempValue = tempValue.coerceAtLeast(absoluteMinValue)
            tempValue -= absoluteMinValue
            tempValue = tempValue / (absoluteMaxValue - absoluteMinValue) * 100
            setNormalizedMaxValue(tempValue.toDouble())
        }
    }

    private fun evalPressedThumb(touchX: Float): RangeSeekbar.Thumb? {
        var result: RangeSeekbar.Thumb? = null
        val minThumbPressed = isInThumbRange(touchX, normalizedMinValue)
        val maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue)
        if (minThumbPressed && maxThumbPressed) {
            // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
            result =
                if (touchX / width > 0.5f) RangeSeekbar.Thumb.MIN else RangeSeekbar.Thumb.MAX
        } else if (minThumbPressed) {
            result = RangeSeekbar.Thumb.MIN
        } else if (maxThumbPressed) {
            result = RangeSeekbar.Thumb.MAX
        }
        return result
    }

    private fun isInThumbRange(
        touchX: Float,
        normalizedThumbValue: Double
    ): Boolean {
        val thumbPos = normalizedToScreen(normalizedThumbValue)
        val left = thumbPos - getThumbWidth() / 2
        val right = thumbPos + getThumbWidth() / 2
        var x = touchX - getThumbWidth() / 2
        if (thumbPos > width - thumbWidth) x = touchX
        return x >= left && x <= right
        //return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbWidth;
    }

    private fun onStartTrackingTouch() {
        mIsDragging = true
    }

    private fun onStopTrackingTouch() {
        mIsDragging = false
    }

    private fun normalizedToScreen(normalizedCoord: Double): Float {
        val width = width - barPadding * 2
        return normalizedCoord.toFloat() / 100f * width
    }

    private fun screenToNormalized(screenCoord: Float): Double {
        var width = width.toDouble()
        return if (width <= 2 * barPadding) {
            // prevent division by zero, simply return 0.
            0.0
        } else {
            width = width - barPadding * 2
            var result = screenCoord / width * 100.0
            result = result - barPadding / width * 100.0
            result = Math.min(100.0, Math.max(0.0, result))
            result
        }
    }

    private fun setNormalizedMinValue(value: Double) {
        normalizedMinValue = Math.max(
            0.0,
            Math.min(100.0, Math.min(value, normalizedMaxValue))
        )
        if (fixGap != NO_FIXED_GAP && fixGap > 0) {
            addFixGap(true)
        } else {
            addMinGap()
        }
        invalidate()
    }

    private fun setNormalizedMaxValue(value: Double) {
        normalizedMaxValue = Math.max(
            0.0,
            Math.min(100.0, Math.max(value, normalizedMinValue))
        )
        if (fixGap != NO_FIXED_GAP && fixGap > 0) {
            addFixGap(false)
        } else {
            addMaxGap()
        }
        invalidate()
    }

    private fun addFixGap(leftThumb: Boolean) {
        if (leftThumb) {
            normalizedMaxValue = normalizedMinValue + fixGap
            if (normalizedMaxValue >= 100) {
                normalizedMaxValue = 100.0
                normalizedMinValue = normalizedMaxValue - fixGap
            }
        } else {
            normalizedMinValue = normalizedMaxValue - fixGap
            if (normalizedMinValue <= 0) {
                normalizedMinValue = 0.0
                normalizedMaxValue = normalizedMinValue + fixGap
            }
        }
    }

    private fun addMinGap() {
        if (normalizedMinValue + gap > normalizedMaxValue) {
            val g = normalizedMinValue + gap
            normalizedMaxValue = g
            normalizedMaxValue = Math.max(
                0.0,
                Math.min(100.0, Math.max(g, normalizedMinValue))
            )
            if (normalizedMinValue >= normalizedMaxValue - gap) {
                normalizedMinValue = normalizedMaxValue - gap
            }
        }
    }

    private fun addMaxGap() {
        if (normalizedMaxValue - gap < normalizedMinValue) {
            val g = normalizedMaxValue - gap
            normalizedMinValue = g
            normalizedMinValue = Math.max(
                0.0,
                Math.min(100.0, Math.min(g, normalizedMaxValue))
            )
            if (normalizedMaxValue <= normalizedMinValue + gap) {
                normalizedMaxValue = normalizedMinValue + gap
            }
        }
    }

    private fun normalizedToValue(normalized: Double): Double {
        var `val` = normalized / 100 * (maxValue - minValue)
        `val` = `val` + minValue
        return `val`
    }

    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun <T : Number?> formatValue(value: T): Number {
        val v = value as Double
        if (dataType == RangeSeekbar.DataType.LONG) {
            return v.toLong()
        }
        if (dataType == RangeSeekbar.DataType.DOUBLE) {
            return v
        }
        if (dataType == RangeSeekbar.DataType.INTEGER) {
            return Math.round(v)
        }
        if (dataType == RangeSeekbar.DataType.FLOAT) {
            return v.toFloat()
        }
        if (dataType == RangeSeekbar.DataType.SHORT) {
            return v.toShort()
        }
        if (dataType == RangeSeekbar.DataType.BYTE) {
            return v.toByte()
        }
        throw IllegalArgumentException("Number class '" + value.javaClass.name + "' is not supported")
    }

    //////////////////////////////////////////
    // OVERRIDE METHODS
    //////////////////////////////////////////
    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // setup bar
        setupBar(canvas, _paint, _rect)

        // setup seek bar active range line
        setupHighlightBar(canvas, _paint, _rect)

        // draw left thumb
        setupLeftThumb(canvas, _paint, _rect)

        // draw right thumb
        setupRightThumb(canvas, _paint, _rect)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getMeasureSpecWith(widthMeasureSpec),
            getMeasureSpecHeight(heightMeasureSpec)
        )
    }

    /**
     * Handles thumb selection and movement. Notifies listener callback on certain events.
     */
    @Synchronized
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = event.getPointerId(event.pointerCount - 1)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                val mDownMotionX = event.getX(pointerIndex)
                pressedThumb = evalPressedThumb(mDownMotionX)
                if (pressedThumb == null) return super.onTouchEvent(event)
                touchDown(event.getX(pointerIndex), event.getY(pointerIndex))
                isPressed = true
                invalidate()
                onStartTrackingTouch()
                trackTouchEvent(event)
                attemptClaimDrag()
            }
            MotionEvent.ACTION_MOVE -> if (pressedThumb != null) {
                if (mIsDragging) {
                    touchMove(event.getX(pointerIndex), event.getY(pointerIndex))
                    trackTouchEvent(event)
                }
                if (onRangeSeekbarChangeListener != null) {
                    onRangeSeekbarChangeListener!!.valueChanged(
                        selectedMinValue,
                        selectedMaxValue
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex))
                    if (onRangeSeekbarFinalValueListener != null) {
                        onRangeSeekbarFinalValueListener!!.finalValue(
                            selectedMinValue,
                            selectedMaxValue
                        )
                    }
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should be interpreted as a tap-seek to that location.
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                pressedThumb = null
                invalidate()
                if (onRangeSeekbarChangeListener != null) {
                    onRangeSeekbarChangeListener!!.valueChanged(
                        selectedMinValue,
                        selectedMaxValue
                    )
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
            }
            MotionEvent.ACTION_POINTER_UP ->                 /*onSecondaryPointerUp(event);*/invalidate()
            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex))
                }
                invalidate() // see above explanation
            }
        }
        return true
    }

    companion object {
        //////////////////////////////////////////
        // PRIVATE CONSTANTS
        //////////////////////////////////////////
        public const val INVALID_POINTER_ID = 255
    }
}

interface OnRangeSeekbarChangeListener {
    fun valueChanged(minValue: Number?, maxValue: Number?)
}

interface OnRangeSeekbarFinalValueListener {
    fun finalValue(minValue: Number?, maxValue: Number?)
}