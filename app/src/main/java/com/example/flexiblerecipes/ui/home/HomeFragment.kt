package com.example.flexiblerecipes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.flexiblerecipes.MainActivity
import com.example.flexiblerecipes.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.MalformedURLException
import java.net.URL

class HomeFragment : Fragment() {

    private lateinit var editText: EditText
    private lateinit var fetchButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        editText = root.findViewById(R.id.urltxt)
        fetchButton = root.findViewById(R.id.fetch)

        fetchButton.setOnClickListener {
            val url = editText.text.toString()
            fetchRecipeData(url)
        }

        return root
    }

    private fun fetchRecipeData(url: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val urlObject = URL(url)
                val doc = Jsoup.parse(urlObject, 3000)
                val recipeData = extractRecipeData(doc)
                requireActivity().runOnUiThread {
                    (requireActivity() as MainActivity).displayRecipeData(recipeData)
                }
            } catch (e: MalformedURLException) {
                requireActivity().runOnUiThread {
                    // Handle invalid URL
                    // Show error message to the user
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    // Handle other exceptions
                    // Show error message to the user
                }
            }
        }
    }

    private fun extractRecipeData(doc: org.jsoup.nodes.Document): MainActivity.RecipeData {
        // Extract recipe details using CSS selectors and get outer HTML
        val prepTimeElement = doc.select(".mntl-recipe-details__value").firstOrNull()
        val prepTime = prepTimeElement?.outerHtml() ?: ""

        val cookTimeElement = doc.select(".mntl-recipe-details__value").getOrNull(1)
        val cookTime = cookTimeElement?.outerHtml() ?: ""

        val additionalTimeElement = doc.select(".mntl-recipe-details__value").getOrNull(2)
        val additionalTime = additionalTimeElement?.outerHtml() ?: ""

        val totalTimeElement = doc.select(".mntl-recipe-details__value").getOrNull(3)
        val totalTime = totalTimeElement?.outerHtml() ?: ""

        val servingsElement = doc.select(".mntl-recipe-details__value").getOrNull(4)
        val servings = servingsElement?.outerHtml() ?: ""

        // Extract ingredients excluding image-related elements and get outer HTML
        val ingredientsElements = doc.select("#mntl-structured-ingredients_1-0 > ul > li")
        val ingredients = ingredientsElements.map { it.outerHtml() }

        // Extract directions excluding image-related elements and get outer HTML
        val directionsElements = doc.select("#recipe__steps-content_1-0 ol, #recipe__steps-content_1-0 ul").select("li")
        val directions = directionsElements.map { it.outerHtml() }

        return MainActivity.RecipeData(
            prepTime,
            cookTime,
            additionalTime,
            totalTime,
            servings,
            ingredients,
            directions
        )
    }


}
