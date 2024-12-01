package com.amarnafais.measureit

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var categorySpinner: Spinner
    private lateinit var fromUnitSpinner: Spinner
    private lateinit var toUnitSpinner: Spinner
    private lateinit var inputValue: EditText
    private lateinit var convertButton: Button
    private lateinit var resultLabel: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Initializing the components for the UI
        categorySpinner = findViewById(R.id.spnCategory)
        fromUnitSpinner = findViewById(R.id.spnUnitOne)
        toUnitSpinner = findViewById(R.id.spnUnitTwo)
        inputValue = findViewById(R.id.txtValueOne)
        convertButton = findViewById(R.id.btnConvert)
        resultLabel = findViewById(R.id.lblResult)

        // Data for category Spinner
        val categories = listOf("Temperature", "Weight", "Distance")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        //Mapping categories with their units
        val unitMap = mapOf(
            "Temperature" to listOf("Celsius", "Fahrenheit", "Kelvin"),
            "Weight" to listOf("Kilograms", "Pounds", "Ounces", "Grams"),
            "Distance" to listOf("Meters", "Kilometers", "Miles")
        )

        // Update units according to the selected category
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedCategory = categories[position]
                val units = unitMap[selectedCategory]

                if (units != null) {
                    val unitAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, units)
                    unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    fromUnitSpinner.adapter = unitAdapter
                    toUnitSpinner.adapter = unitAdapter
                    toUnitSpinner.setSelection(1)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Handling the convert button
        convertButton.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem.toString()
            val fromUnit = fromUnitSpinner.selectedItem.toString()
            val toUnit = toUnitSpinner.selectedItem.toString()
            val value = inputValue.text.toString().toDoubleOrNull()

            if (value == null) {
                Toast.makeText(this, "Please enter a valid value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Do the converting process for the selected category
            val result = when (selectedCategory) {
                "Temperature" -> convertTemperature(value, fromUnit, toUnit)
                "Weight" -> convertWeight(value, fromUnit, toUnit)
                "Distance" -> convertDistance(value, fromUnit, toUnit)
                else -> null
            }

            if (result != null) {
                //Displaying the final result with 2 decimal places
                val formattedResult = String.format("%.2f", result)
                resultLabel.text = "$formattedResult"
            } else {
                Toast.makeText(this, "Conversion error", Toast.LENGTH_SHORT).show()
            }

            //Hide the keyboard after the convert button is clicked. Did this because I tested the ui with a physical device without the inbuilt virtual device.
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    //Converting between the temperature units
    private fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
        return when (fromUnit to toUnit) {
            "Celsius" to "Fahrenheit" -> value * 9 / 5 + 32
            "Celsius" to "Kelvin" -> value + 273.15
            "Fahrenheit" to "Celsius" -> (value - 32) * 5 / 9
            "Fahrenheit" to "Kelvin" -> (value - 32) * 5 / 9 + 273.15
            "Kelvin" to "Celsius" -> value - 273.15
            "Kelvin" to "Fahrenheit" -> (value - 273.15) * 9 / 5 + 32
            else -> value
        }
    }

    //Converting between weight units
    private fun convertWeight(value: Double, fromUnit: String, toUnit: String): Double {
        val kilograms = when (fromUnit) {
            "Kilograms" -> value
            "Grams" -> value / 1000
            "Pounds" -> value * 0.453592
            "Ounces" -> value * 0.0283495
            else -> value
        }
        return when (toUnit) {
            "Kilograms" -> kilograms
            "Grams" -> kilograms * 1000
            "Pounds" -> kilograms / 0.453592
            "Ounces" -> kilograms / 0.0283495
            else -> kilograms
        }
    }

    //Converting between distance units
    private fun convertDistance(value: Double, fromUnit: String, toUnit: String): Double {
        val meters = when (fromUnit) {
            "Meters" -> value
            "Kilometers" -> value * 1000
            "Miles" -> value * 1609.34
            else -> value
        }
        return when (toUnit) {
            "Meters" -> meters
            "Kilometers" -> meters / 1000
            "Miles" -> meters / 1609.34
            else -> meters
        }
    }
}