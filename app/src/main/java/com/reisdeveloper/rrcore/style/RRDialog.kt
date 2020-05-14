package com.reisdeveloper.rrcore.style

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.reisdeveloper.rrcore.R
import com.reisdeveloper.rrcore.enuns.RRAlertType


class RRDialog(context: Context) : Dialog(context) {

    private lateinit var imageViewTitle : ImageView
    private lateinit var textViewTitle : TextView
    private lateinit var textViewMessage : TextView

    private val customLayout by lazy {
        layoutInflater.inflate(R.layout.rrdialog, null)
    }

    private val builder by lazy {
        AlertDialog.Builder(context)
    }

    init {
        instanceCustomLayout()
    }

    private fun instanceCustomLayout(){
        imageViewTitle = customLayout.findViewById(R.id.rrdialog_ivImage)
        textViewTitle = customLayout.findViewById(R.id.rrdialog_tvTitle)
        textViewMessage = customLayout.findViewById(R.id.rrdialog_tvMessage)
    }

    fun showSimpleDialog(title: String?, message: String, alertType: RRAlertType? = null) {
        return context.let {
            builder.setView(customLayout)

            if(alertType != null) imageViewTitle.setImageDrawable(chooseTypeDialog(alertType))
            else imageViewTitle.visibility = View.GONE

            if (title != null) textViewTitle.text = title
            else textViewTitle.visibility = View.GONE

            textViewMessage.text = message

            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }

            builder.create()
            builder.show()
        }
    }

    fun showCustomDialog(
        title: String? = null,
        message: String,
        alertType: RRAlertType,
        confirmText: String? = null,
        negativeText: String? = null,
        neltralText: String? = null,
        returnDialog: ((typeReturn: String, Dialog) -> Unit)? = null
    ): Dialog {
        return context.let { _ ->
            imageViewTitle.setImageDrawable(chooseTypeDialog(alertType))

            setCustomDialog(title, message, confirmText, negativeText, neltralText, returnDialog)
        }
    }

    fun showCustomDialog(
        title: String? = null,
        message: String,
        imageTitle: Drawable,
        confirmText: String? = null,
        negativeText: String? = null,
        neltralText: String? = null,
        returnDialog: ((typeReturn: String, Dialog) -> Unit)? = null
    ): Dialog {
        return context.let { _ ->
            imageViewTitle.setImageDrawable(imageTitle)

            setCustomDialog(title, message, confirmText, negativeText, neltralText, returnDialog)
        }
    }

    private fun setCustomDialog(
        title: String? = null,
        message: String,
        confirmText: String? = null,
        negativeText: String? = null,
        neltralText: String? = null,
        returnDialog: ((typeReturn: String, Dialog) -> Unit)? = null
    ):Dialog{
        return context.let { _ ->
            builder.setView(customLayout)

            if (title != null) textViewTitle.text = title
            else textViewTitle.visibility = View.GONE

            textViewMessage.text = message

            if (confirmText != null)
                builder.setPositiveButton(confirmText) { dialog2, dialog ->
                    returnDialog?.invoke(confirmText, this)
                }

            if (negativeText != null)
                builder.setNegativeButton(negativeText) { _, _ ->
                    returnDialog?.invoke(negativeText, this)
                }

            if (neltralText != null)
                builder.setNeutralButton(neltralText) { _, _ ->
                    returnDialog?.invoke(neltralText, this)
                }


            builder.create()
            builder.show()
        }
    }

    private fun chooseTypeDialog(alertType: RRAlertType): Drawable? {
        return when(alertType){
            RRAlertType.ERROR_TYPE -> context.getDrawable(R.drawable.img_error_title_alert)
            RRAlertType.SUCCESS_TYPE -> context.getDrawable(R.drawable.img_success_title_alert)
            RRAlertType.QUESTION_TYPE-> context.getDrawable(R.drawable.img_question_title_alert)
            RRAlertType.WARNING_TYPE -> context.getDrawable(R.drawable.img_warning_title_alert)
            RRAlertType.PERMISSION_TYPE -> context.getDrawable(R.drawable.img_permission_title_alert)
            else -> null
        }
    }
}

