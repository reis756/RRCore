package com.reisdeveloper.rrcore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_tvPermission.setOnClickListener { callNewIntent(PermissionTesteActivity::class.java) }

        main_tvDialog.setOnClickListener { callNewIntent(DialogTesteActivity::class.java) }
    }

    private fun callNewIntent(newClass: Class<*>){
        val intent = Intent(this@MainActivity, newClass)
        startActivity(intent)
    }
}
