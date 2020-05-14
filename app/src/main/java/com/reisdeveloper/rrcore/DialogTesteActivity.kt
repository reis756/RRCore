package com.reisdeveloper.rrcore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.reisdeveloper.rrcore.enuns.RRAlertType
import com.reisdeveloper.rrcore.style.RRDialog
import kotlinx.android.synthetic.main.activity_dialog_teste.*

class DialogTesteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_teste)

        dialogteste_bt1.setOnClickListener { simpleDialogTest(null) }
        dialogteste_bt2.setOnClickListener { customDialogTest() }
        dialogteste_bt3.setOnClickListener { customDialogTest2() }
        dialogteste_bt4.setOnClickListener { simpleDialogTest(RRAlertType.QUESTION_TYPE) }
        dialogteste_bt5.setOnClickListener { simpleDialogTest(RRAlertType.PERMISSION_TYPE) }
        dialogteste_bt6.setOnClickListener { simpleDialogTest(RRAlertType.SUCCESS_TYPE) }
        dialogteste_bt7.setOnClickListener { simpleDialogTest(RRAlertType.ERROR_TYPE) }
    }

    private fun simpleDialogTest(alerType: RRAlertType?) {
        RRDialog(this).showSimpleDialog(
            "Liberação de permissão necessária",
            "Teste com titulo",
            alerType
        )
    }

    private fun customDialogTest() {
        getDrawable(R.drawable.img_question)?.let {
            RRDialog(this).showCustomDialog(
                title = "Precisamos de sua permissão",
                message = "Mensagem de teste de dialogo customizado",
                imageTitle = it,
                confirmText = "Aceitar",
                negativeText = "Cancelar"
            ) { texto, dialog ->
                val t = texto
                val d = dialog
            }
        }
    }

    private fun customDialogTest2() {
        RRDialog(this).showCustomDialog(
            title = "Precisamos de sua permissão",
            message = "Mensagem de teste de dialogo customizado",
            alertType = RRAlertType.PERMISSION_TYPE,
            confirmText = "Aceitar",
            negativeText = "Cancelar"
        ) { texto, dialog ->
            val t = texto
            val d = dialog
        }
    }
}