package com.zheng.home.features.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.jakewharton.rxbinding2.view.RxView
import com.zheng.home.R
import com.zheng.home.features.quiz.QuizActivity
import kotlinx.android.synthetic.main.activity_welcome.*
import java.util.concurrent.TimeUnit

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        RxView.clicks(bt_start).debounce(300, TimeUnit.MILLISECONDS).subscribe { view ->
            startActivity(Intent(this, QuizActivity::class.java))
        }
    }

}
