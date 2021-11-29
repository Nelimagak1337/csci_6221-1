package com.example.imagelablekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener


class Mainpage_Activity : AppCompatActivity() {
    lateinit var btnImage:Button
    lateinit var btntext:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpage)

        btnImage=findViewById(R.id.btnimagereco)
        btntext=findViewById(R.id.btntext)

        btnImage.setOnClickListener{
            val intent=Intent(this@Mainpage_Activity,RecognitionActivity::class.java)
                startActivity(intent)

    }
        btntext.setOnClickListener(View.OnClickListener {
            val intent2=Intent(this@Mainpage_Activity,OCRActivity::class.java)
                startActivity(intent2)
        })


    }
}