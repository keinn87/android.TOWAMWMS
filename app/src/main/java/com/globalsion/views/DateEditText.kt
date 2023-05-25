package com.globalsion.views

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.AttributeSet
import com.globalsion.utils.DateUtil
import com.globalsion.views.abstracts.AbstractDialogEditText
import java.text.SimpleDateFormat
import java.util.*

class DateEditText : AbstractDialogEditText {
    companion object {
        private const val STATE_SUPER = "SUPER"
        private const val STATE_DATE = "DATE"
        private const val STATE_PICKER = "PICKER"
        private const val STATE_PICKER_YEAR = "YEAR"
        private const val STATE_PICKER_MONTH = "MONTH"
        private const val STATE_PICKER_DAY = "DAY"
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var dateFormat: String = "d MMM yyyy"
    var date: Date
        set(value) {
            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
            setText(simpleDateFormat.format(value))
            field = value
        }
    private var picker: DatePickerDialog? = null

    init {
        date = DateUtil.today()
    }

    /** Perform show dialog. */
    override fun showDialog() {
        if (picker == null || !picker!!.isShowing) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            picker = DatePickerDialog(context, pickerOnDateSetListener, year, month, day)
            picker!!.show()
        }
    }

    /** Perform close dialog. */
    override fun closeDialog() {
        Handler(Looper.getMainLooper()).post {
            picker?.dismiss()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val parcelable = super.onSaveInstanceState()
        val state = Bundle()
        state.putParcelable(STATE_SUPER, parcelable)
        state.putSerializable(STATE_DATE, date)

        if (picker != null && picker!!.isShowing) {
            val pickerState = Bundle()
            pickerState.putInt(STATE_PICKER_YEAR, picker!!.datePicker.year)
            pickerState.putInt(STATE_PICKER_MONTH, picker!!.datePicker.month)
            pickerState.putInt(STATE_PICKER_DAY, picker!!.datePicker.dayOfMonth)

            state.putBundle(STATE_PICKER, pickerState)
            picker!!.dismiss()
        }
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val parcelable: Parcelable? = state.getParcelable(STATE_SUPER)
            date = state.getSerializable(STATE_DATE) as Date

            val pickerState: Bundle? = state.getBundle(STATE_PICKER)
            if (pickerState != null) {
                val year = pickerState.getInt(STATE_PICKER_YEAR)
                val month = pickerState.getInt(STATE_PICKER_MONTH)
                val day = pickerState.getInt(STATE_PICKER_DAY)

                picker = DatePickerDialog(context, pickerOnDateSetListener, year, month, day)
                picker!!.show()
            }

            super.onRestoreInstanceState(parcelable)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private val pickerOnDateSetListener = DatePickerDialog.OnDateSetListener { _, newYear, newMonth, newDay ->
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.YEAR, newYear)
        calendar.set(Calendar.MONTH, newMonth)
        calendar.set(Calendar.DAY_OF_MONTH, newDay)

        date = calendar.time
    }
}