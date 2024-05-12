package com.example.flexiblerecipes

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var textView: TextView
    private lateinit var fetchButton: Button
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.urltxt)
        textView = findViewById(R.id.textview)
        fetchButton = findViewById(R.id.fetch)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        findViewById<BottomNavigationView>(R.id.nav_view).setupWithNavController(navController)

        fetchButton.setOnClickListener {
            var url = editText.text.toString().trim()
            if (url.isNotEmpty()) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://$url"
                }
                try {
                    val urlObject = URL(url)
                    scrapeRecipeFromWeb(url)
                } catch (e: MalformedURLException) {
                    textView.text = "Error: Malformed URL: ${e.message}"
                    Log.e("MainActivity", "Malformed URL: ${e.message}")
                }
            } else {
                textView.text = "Please enter a valid URL"
            }
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                fetchButton.performClick()
                true
            } else {
                false
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (::navController.isInitialized && navController.currentDestination?.id == R.id.navigation_dashboard) {
            navController.navigate(R.id.navigation_home)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (::navController.isInitialized) {
            return when (item.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun scrapeRecipeFromWeb(url: String) {
        Thread {
            val builder = StringBuilder()
            try {
                val doc: Document = Jsoup.connect(url).get()
                val recipeData = extractRecipeData(doc)
                runOnUiThread {
                    displayRecipeData(recipeData)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    textView.text = "Error: ${e.message}"
                    Log.e("MainActivity", "Error occurred: ${e.message}")
                }
            }
        }.start()
    }

    private fun extractRecipeData(doc: Document): RecipeData {
        // Extract recipe details using CSS selectors
        val prepTime = doc.select("div:nth-child(1) > div.mntl-recipe-details__value").text()
        val cookTime = doc.select("div:nth-child(2) > div.mntl-recipe-details__value").text()
        val additionalTime = doc.select("div > div:nth-child(3) > div.mntl-recipe-details__value").text()
        val totalTime = doc.select("div > div:nth-child(4) > div.mntl-recipe-details__value").text()
        val servings = doc.select("div > div:nth-child(5) > div.mntl-recipe-details__value").text()

        // Extract ingredients using CSS selector
        val ingredients = doc.select("#mntl-structured-ingredients_1-0 > ul li").map { it.text() }

        // Extract directions using CSS selector
        val directions = doc.select("#recipe__steps-content_1-0 ol, #recipe__steps-content_1-0 ul")
            .flatMap { it.select("li").map { li -> li.text() } }

        return RecipeData(
            prepTime,
            cookTime,
            additionalTime,
            totalTime,
            servings,
            ingredients,
            directions
        )
    }

    internal fun displayRecipeData(recipeData: RecipeData) {
        textView.text = "Prep Time: ${recipeData.prepTime}\n" +
                "Cook Time: ${recipeData.cookTime}\n" +
                "Additional Time: ${recipeData.additionalTime}\n" +
                "Total Time: ${recipeData.totalTime}\n" +
                "Servings: ${recipeData.servings}\n\n" +
                "Ingredients:\n${recipeData.ingredients.joinToString("\n")}\n\n" +
                "Directions:\n${recipeData.directions.joinToString("\n")}"
    }

    data class RecipeData(
        val prepTime: String,
        val cookTime: String,
        val additionalTime: String,
        val totalTime: String,
        val servings: String,
        val ingredients: List<String>,
        val directions: List<String>
    )
}
