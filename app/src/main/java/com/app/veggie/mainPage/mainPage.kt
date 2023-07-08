package com.app.veggie.mainPage

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.veggie.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_page.*

class mainPage : Fragment() {
    var mainActivity: MainActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restBtn.setOnClickListener {
            val restButton = view.findViewById<ConstraintLayout>(R.id.restBtn)
            restButton.animate().scaleX(0.9f).scaleY(0.9f).setDuration(150L).withEndAction {
                restButton.scaleX = 1f
                restButton.scaleY = 1f
            }.start()

            Handler().postDelayed({
                mainActivity?.change(fragmentRest)
                mainActivity?.bottom_navi?.selectedItemId = R.id.rest_menu
            }, 50L)
        }

        foodBtn.setOnClickListener {
            val foodButton = view.findViewById<ConstraintLayout>(R.id.foodBtn)
            foodButton.animate().scaleX(0.9f).scaleY(0.9f).setDuration(150L).withEndAction {
                foodButton.scaleX = 1f
                foodButton.scaleY = 1f
            }.start()

            Handler().postDelayed({
                mainActivity?.change(fragmentFood)
                mainActivity?.bottom_navi?.selectedItemId = R.id.food_menu
            }, 50L)
        }

        recipeBtn.setOnClickListener {
            val recipeButton = view.findViewById<ConstraintLayout>(R.id.recipeBtn)
            recipeButton.animate().scaleX(0.9f).scaleY(0.9f).setDuration(150L).withEndAction {
                recipeButton.scaleX = 1f
                recipeButton.scaleY = 1f
            }.start()

            Handler().postDelayed({
                mainActivity?.change(fragmentRecipe)
                mainActivity?.bottom_navi?.selectedItemId = R.id.recipe_menu
            }, 50L)
        }

        calBtn.setOnClickListener {
            val calButton = view.findViewById<ConstraintLayout>(R.id.calBtn)
            calButton.animate().scaleX(0.9f).scaleY(0.9f).setDuration(150L).withEndAction {
                calButton.scaleX = 1f
                calButton.scaleY = 1f
            }.start()

            Handler().postDelayed({
                mainActivity?.change(fragmentCal)
                mainActivity?.bottom_navi?.selectedItemId = R.id.cal_menu
            }, 50L)
        }
    }

}