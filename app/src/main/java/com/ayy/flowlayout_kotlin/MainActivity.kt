package com.ayy.flowlayout_kotlin

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var flowLayout: FlowLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        flowLayout = findViewById(R.id.flow)
        for (i in 0..20) {
            val textView = TextView(this)
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textView.text = "数据$i"
            flowLayout.addView(textView)
        }
    }

}
