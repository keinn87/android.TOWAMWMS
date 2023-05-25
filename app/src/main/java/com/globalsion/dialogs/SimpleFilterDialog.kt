package com.globalsion.dialogs

import android.os.*
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.globalsion.dialogs.interfaces.DialogResultInterface
import com.globalsion.utils.AndroidUtil
import com.globalsion.utils.DateUtil
import com.globalsion.views.DateEditText
import java.util.*

/** Simple filtering which able to filter simple value. */
class SimpleFilterDialog : AbstractDialog() {
    companion object {
        val ARGS_TITLE = "TITLE"

        fun newInstance(title: String) : SimpleFilterDialog {
            val dialog = SimpleFilterDialog()
            dialog.arguments = Bundle()
            dialog.arguments!!.putString(ARGS_TITLE, title)
            return dialog
        }
    }

    private val fields = ArrayList<Field>()
    private val inputs = ArrayList<Triple<Field, TextInputLayout, TextInputEditText>>()

    /**
     * Add filtering field.
     * @param name Unique name use to identify the value.
     * @param hint Caption for the field.
     **/
    fun addField(name: String, hint: String) {
        addField(Field(name, hint))
    }
    /**
     * Add filtering field.
     * @param name Unique name use to identify the value.
     * @param hint Caption for the field.
     * @param text Default value for the field. (Can use as remains old filter value)
     **/
    fun addField(name: String, hint: String, text: String) {
        val field = Field(name, hint)
        field.text = text
        addField(field)
    }
    /**
     * Add filtering field
     * @param field Field to add.
     **/
    fun addField(field: Field) {
        if (fields.any{ it.name == field.name }) {
            throw RuntimeException("name '${field.name}' already exists.")
        }
        fields.add(field)
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val attributes = dialog.window.attributes
        attributes.horizontalMargin = 46f
        dialog.window.attributes = attributes
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setTitle(arguments!!.getString(ARGS_TITLE))
        val textTitle: TextView = dialog.findViewById(android.support.v4.R.id.title)
        textTitle.setPadding(textTitle.paddingLeft, textTitle.paddingTop, textTitle.paddingRight,
                AndroidUtil.convertDpToPixel(16f).toInt())

        val scroll = ScrollView(inflater.context)
        scroll.apply {
            val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams = params
        }
        scroll.isFillViewport = true

        val root = LinearLayout(inflater.context)
        root.apply {
            val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams = params
        }
        root.orientation = LinearLayout.VERTICAL
        scroll.addView(root)

        val padding = AndroidUtil.convertDpToPixel(8f).toInt()
        root.setPadding(padding, padding, padding, padding)

        if (savedInstanceState != null) {
            fields.clear()
            fields.addAll(savedInstanceState.getParcelableArrayList("fields"))
        }

        inputs.clear()
        fields.forEach {
            val layout = TextInputLayout(root.context)
            val editText: TextInputEditText
            if ((it.inputType.and(InputType.TYPE_CLASS_DATETIME)) == InputType.TYPE_CLASS_DATETIME) {
                editText = DateEditText(root.context)
                editText.dateFormat = it.dateFormat
                editText.date = it.date
            } else {
                editText = TextInputEditText(root.context)
                editText.inputType = it.inputType
                editText.setText(it.text)
            }
            editText.hint = it.hint

            layout.addView(editText)
            root.addView(layout)

            layout.apply {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams = layoutParams
            }
            editText.apply {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams = layoutParams
            }
            inputs.add(Triple(it, layout, editText))
        }

        return scroll
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            val focused: String? = savedInstanceState.getString("focused")
            val handler = Handler(Looper.getMainLooper())
            inputs.forEach {
                val stateName = "input-" + it.first.name
                if (it.first.inputType.and(InputType.TYPE_CLASS_DATETIME) == InputType.TYPE_CLASS_DATETIME) {
                    val saved: Parcelable? = savedInstanceState.getParcelable(stateName)

                    handler.post {
                        (it.third as DateEditText).onRestoreInstanceState(saved)
                        if (focused == stateName) {
                            it.third.requestFocus()
                        }
                    }
                } else {
                    val saved = savedInstanceState.getString(stateName)

                    handler.post {
                        it.third.setText(saved ?: it.first.text)
                        if (focused == stateName) {
                            it.third.requestFocus()
                        }
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("fields", fields)
        var focused: String? = null

        inputs.forEach {
            val stateName = "input-" + it.first.name
            if (it.first.inputType.and(InputType.TYPE_CLASS_DATETIME) == InputType.TYPE_CLASS_DATETIME) {
                outState.putParcelable(stateName, it.third.onSaveInstanceState())
            } else {
                outState.putString(stateName, it.third.text.toString())
            }
            if (it.third.isFocused) {
                focused = stateName
            }
        }

        outState.putString("focused", focused)
    }

    override fun onDialogResult(callback: DialogResultInterface) {
        val data = Bundle()

        inputs.forEach {
            data.putString(it.first.name, it.third.text.toString())
        }

        callback.onDialogResult(this, requestCode, data)
    }

    @Suppress("CanBePrimaryConstructorProperty")
    class Field(name: String, hint: String) : Parcelable {
        /**
         * Unique name to identify the value.
         **/
        var name: String = name
        /**
         * Caption to display on the screen.
         **/
        var hint: String = hint
        /**
         * Input type for the field
         * @see InputType
         **/
        var inputType: Int = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        /**
         * Default value for the field.
         * This value will be ignored if the [inputType] is [InputType.TYPE_CLASS_DATETIME]
         **/
        var text: String = ""
        /**
         * Date format for the field when the [inputType] is [InputType.TYPE_CLASS_DATETIME].
         * This value will be ignored if the [inputType] is not [InputType.TYPE_CLASS_DATETIME].
         **/
        var dateFormat: String = "d MMM yyyy"
        /**
         * Default date for the field when the [inputType] is [InputType.TYPE_CLASS_DATETIME].
         * This value will be ignored if the [inputType] is not [InputType.TYPE_CLASS_DATETIME].
         **/
        var date: Date = DateUtil.today()

        constructor(parcel: Parcel) : this("", "") {
            name = parcel.readString()
            hint = parcel.readString()
            inputType = parcel.readInt()
            text = parcel.readString()
            dateFormat = parcel.readString()
            date = parcel.readSerializable() as Date
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(name)
            dest.writeString(hint)
            dest.writeInt(inputType)
            dest.writeString(text)
            dest.writeString(dateFormat)
            dest.writeSerializable(date)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Field> {
            override fun createFromParcel(parcel: Parcel): Field {
                return Field(parcel)
            }

            override fun newArray(size: Int): Array<Field?> {
                return arrayOfNulls(size)
            }
        }
    }
}