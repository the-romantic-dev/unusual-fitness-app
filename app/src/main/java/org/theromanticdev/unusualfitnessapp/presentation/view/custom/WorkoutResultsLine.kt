package org.theromanticdev.unusualfitnessapp.presentation.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.databinding.ViewWorkoutResultsLineBinding

class WorkoutResultsLine(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

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
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    var resultValue
        get() = run {
            binding.resultValue.text.split(" ")[0]
        }
        @SuppressLint("SetTextI18n")
        set(value) {
            binding.resultValue.text = "$value $resultUnits"
        }

    private val resultUnits: String
        get() = run {
            val split = binding.resultValue.text.split(" ")
            if (split.size > 1) split[1]
            else ""
        }

    @SuppressLint("SetTextI18n")
    private fun initAttributes(
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.WorkoutResultsLine,
            defStyleAttr,
            defStyleRes
        )

        with(binding) {
            val resultValueText =
                typedArray.getString(R.styleable.WorkoutResultsLine_resultValue)
            val resultUnitsText =
                typedArray.getString(R.styleable.WorkoutResultsLine_resultUnits)
            resultValue.text = "${resultValueText ?: "NaN"} ${resultUnitsText ?: ""}"

            val resultLabelText =
                typedArray.getString(R.styleable.WorkoutResultsLine_resultLabel)
            resultLabel.text = "${resultLabelText ?: "?"}:"


            val resultIconSrc = typedArray.getResourceId(
                R.styleable.WorkoutResultsLine_iconSrc,
                R.drawable.default_text_drawable
            )
            resultLabel.setCompoundDrawablesWithIntrinsicBounds(resultIconSrc, 0, 0, 0)
        }

        typedArray.recycle()
    }
}