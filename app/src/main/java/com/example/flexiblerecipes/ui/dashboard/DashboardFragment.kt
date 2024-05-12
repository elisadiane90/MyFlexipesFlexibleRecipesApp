package com.example.flexiblerecipes.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.flexiblerecipes.MainActivity
import com.example.flexiblerecipes.R

class DashboardFragment : Fragment() {

    private lateinit var ingredientsTextView: TextView
    private lateinit var directionsTextView: TextView
    private lateinit var prepTimeTextView: TextView
    private lateinit var cookTimeTextView: TextView
    private lateinit var additionalTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var servingsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        ingredientsTextView = root.findViewById(R.id.ingredientsTextView)
        directionsTextView = root.findViewById(R.id.directionsTextView)
        prepTimeTextView = root.findViewById(R.id.prepTimeTextView)
        cookTimeTextView = root.findViewById(R.id.cookTimeTextView)
        additionalTimeTextView = root.findViewById(R.id.additionalTimeTextView)
        totalTimeTextView = root.findViewById(R.id.totalTimeTextView)
        servingsTextView = root.findViewById(R.id.servingsTextView)

        prepTimeTextView.visibility = View.VISIBLE
        cookTimeTextView.visibility = View.VISIBLE
        additionalTimeTextView.visibility = View.VISIBLE
        totalTimeTextView.visibility = View.VISIBLE
        servingsTextView.visibility = View.VISIBLE

        return root
    }

    fun updateRecipeData(recipeData: Pair<String, String>) {
    }

    fun displayRecipeData(recipeData: MainActivity.RecipeData) {
        // Populate TextViews with recipe data
        prepTimeTextView.text = getString(R.string.prep_time, recipeData.prepTime)
        cookTimeTextView.text = getString(R.string.cook_time, recipeData.cookTime)
        additionalTimeTextView.text = getString(R.string.additional_time, recipeData.additionalTime)
        totalTimeTextView.text = getString(R.string.total_time, recipeData.totalTime)
        servingsTextView.text = getString(R.string.servings, recipeData.servings)
        ingredientsTextView.text = getString(R.string.ingredients, recipeData.ingredients.joinToString("\n"))
        directionsTextView.text = getString(R.string.directions, recipeData.directions.joinToString("\n"))
    }

}
