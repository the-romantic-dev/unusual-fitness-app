package org.theromanticdev.unusualfitnessapp.presentation.view.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.databinding.ViewWorkoutResultsLineBinding
import org.theromanticdev.unusualfitnessapp.util.getVectorResourceAsScaledBitmap


class WorkoutResultsLine(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var attrsArray: TypedArray

    private val binding: ViewWorkoutResultsLineBinding

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context) :
            this(context, null)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_workout_results_line, this, true)
        binding = ViewWorkoutResultsLineBinding.bind(this)
        if (attrs != null) {
            attrsArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.WorkoutResultsLine,
                defStyleAttr,
                defStyleRes
            )
            initAttributes()
            attrsArray.recycle()
        }

    }

    var resultValue
        get() = run {
            binding.resultValue.text.split(" ")[0]
        }
        set(value) {
            binding.resultValue.text = "$value $resultUnits"
        }

    private val resultUnits: String
        get() = run {
            val split = binding.resultValue.text.split(" ")
            if (split.size > 1) split[1]
            else ""
        }

    private fun initAttributes() {
        initResultValueAndUnits()
        initResultLabel()
        initIcon()
    }

    private fun initIcon() {
        val srcId = attrsArray.getResourceId(
            R.styleable.WorkoutResultsLine_iconSrc,
            R.mipmap.ic_launcher
        )

        val sizeId = attrsArray.getResourceId(
            R.styleable.WorkoutResultsLine_textAndImageSize,
            R.dimen.result_description_text_and_image_size
        )

        val size = resources.getDimensionPixelSize(sizeId)

        binding.resultLabel.setCompoundDrawablesWithIntrinsicBounds(
            context.getVectorResourceAsScaledBitmap(srcId, size, size).toDrawable(resources),
            null,
            null,
            null
        )
    }

    private fun initResultValueAndUnits() {
        val resultValueText = attrsArray.getString(R.styleable.WorkoutResultsLine_resultValue)
        val resultUnitsText = attrsArray.getString(R.styleable.WorkoutResultsLine_resultUnits)
        binding.resultValue.text = "${resultValueText ?: "NaN"} ${resultUnitsText ?: ""}"
    }

    private fun initResultLabel() {
        val resultLabelText =
            attrsArray.getString(R.styleable.WorkoutResultsLine_resultLabel)
        binding.resultLabel.text = "${resultLabelText ?: "?"}:"
    }
}