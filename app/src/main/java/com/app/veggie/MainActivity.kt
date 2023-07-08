package com.app.veggie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.veggie.calPage.calPage
import com.app.veggie.foodPage.foodPage
import com.app.veggie.mainPage.mainPage
import com.app.veggie.recipePage.recipePage
import com.app.veggie.restSearch.restMap
import kotlinx.android.synthetic.main.activity_main.*

public val fragmentMain by lazy { mainPage() }
public val fragmentRest by lazy { restMap() }
public val fragmentFood by lazy { foodPage() }
public val fragmentRecipe by lazy { recipePage() }
public val fragmentCal by lazy { calPage() }


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNavigationBar()
    }

    private fun initNavigationBar() {
        bottom_navi.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.rest_menu -> {
                        try{
                            change(fragmentRest)
                        }catch (e: IllegalStateException){
                            Handler().postDelayed({
                                change(fragmentRest)
                            },500L)
                        }

                    }
                    R.id.food_menu -> {
                        try{
                            change(fragmentFood)
                        }catch (e: IllegalStateException){
                            Handler().postDelayed({
                                change(fragmentFood)
                            },500L)
                        }

                    }
                    R.id.home_menu -> {
                        try{
                            change(fragmentMain)
                        }catch (e: IllegalStateException){
                            Handler().postDelayed({
                                change(fragmentMain)
                            },500L)
                        }
                    }
                    R.id.cal_menu -> {
                        try{
                            change(fragmentCal)
                        }catch (e: IllegalStateException){
                            Handler().postDelayed({
                                change(fragmentCal)
                            },500L)
                        }
                    }
                    R.id.recipe_menu -> {
                        try{
                            change(fragmentRecipe)
                        }catch (e: IllegalStateException){
                            Handler().postDelayed({
                                change(fragmentRecipe)
                            },500L)
                        }
                    }
                }
                true
            }
            selectedItemId = R.id.home_menu
        }
    }

    public fun change(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.changeFragment, fragment)
            .commitNow()
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