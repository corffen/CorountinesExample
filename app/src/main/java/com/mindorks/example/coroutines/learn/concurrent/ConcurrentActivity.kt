package com.mindorks.example.coroutines.learn.concurrent

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mindorks.example.coroutines.R

class ConcurrentActivity : AppCompatActivity() {
    private lateinit var mBtnOn: Button
    private lateinit var mBtnOff: Button
    private lateinit var textView: TextView
    private lateinit var textShow: TextView
    private val viewModel: ConcurrentViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_concurrent)
        textView = findViewById(R.id.tv_sort_list)
        textShow = findViewById(R.id.tv_show)
        mBtnOn = findViewById(R.id.btn_on)
        mBtnOff = findViewById(R.id.btn_off)

        mBtnOn.setOnClickListener {
            viewModel.onSortAsc()
            textShow.text = "click asc"
        }
        mBtnOff.setOnClickListener {
            viewModel.onSortDes()
            textShow.text = "click des"
        }

        viewModel.sortList.observe(this) {
            textView.text = it
        }
    }
}