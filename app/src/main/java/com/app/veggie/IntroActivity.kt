package com.app.veggie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class IntroActivity : AppCompatActivity() {
    val SPLASH_TIME: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()

        }, SPLASH_TIME)
    }

    private var lastTimeBackPressed: Long = 0


    override fun onBackPressed() { //뒤로가기 처리
        if (System.currentTimeMillis() - lastTimeBackPressed >= 1500) {
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finishAffinity()
        }
    }
}