package com.asimin.speedofbinarytree

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.inflate
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    //    val view = View.inflate(this, R.layout.activity_main, null)
//    private lateinit var binding: MainActivity
    private lateinit var numberOfItems: EditText
    private lateinit var search: EditText
    private lateinit var btGenerateArray: Button
    private lateinit var btGenerateBT: Button
    private lateinit var btSearch: Button
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = inflate(this, R.layout.activity_main, null)
        numberOfItems = findViewById<EditText>(R.id.etNumberOfItems)
        search = findViewById<EditText>(R.id.etSearch)
        btGenerateArray = findViewById<Button>(R.id.btnGenerateArray)
        btGenerateBT = findViewById<Button>(R.id.btnGenerateBT)
        btSearch = findViewById<Button>(R.id.btnSearch)
        val tvArrayCreationTime = findViewById<TextView>(R.id.tvArrayCreationTime)
        val tvBtCreationTime = findViewById<TextView>(R.id.tvBinaryTreeCreationTime)
        var numberOfItemsValidator = false

        numberOfItems.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (numberOfItems.text.toString().isEmpty()) { //if the field is empty  - show error
                    numberOfItemsValidator = false
                    Toast.makeText(this, "Please enter a number OFCL", Toast.LENGTH_SHORT).show()
                } else {
                    numberOfItemsValidator = true
                    Toast.makeText(
                        this,
                        "LOST FOCUS - Number of items: ${numberOfItems.text}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                search.clearFocus()
                Toast.makeText(this, "GOT FOCUS - clear Search Focus", Toast.LENGTH_SHORT).show()
            }
        }
        numberOfItems.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty()) { //if the field is empty  - show error
                numberOfItemsValidator = false
                tvArrayCreationTime.visibility = View.INVISIBLE
                tvBtCreationTime.visibility = View.INVISIBLE
                Toast.makeText(this, "Please enter a number OTC", Toast.LENGTH_SHORT).show()
            } else {
                numberOfItemsValidator = true
            }
        }

        btGenerateArray.setOnClickListener {
            if (!numberOfItemsValidator) {
                Toast.makeText(this, "Please enter a number Array-OCL", Toast.LENGTH_SHORT).show()
            } else {
                hideKeyboard()
                Toast.makeText(this, "Generate Array", Toast.LENGTH_SHORT).show()
                tvArrayCreationTime.visibility = View.VISIBLE
            }
        }
        btGenerateBT.setOnClickListener {
            if (!numberOfItemsValidator) {
                Toast.makeText(this, "Please enter a number BT-OCL", Toast.LENGTH_SHORT).show()
            } else {
                hideKeyboard()
                Toast.makeText(this, "Generate BT", Toast.LENGTH_SHORT).show()
                tvBtCreationTime.visibility = View.VISIBLE
            }
        }

        search.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !numberOfItemsValidator) {
                numberOfItems.requestFocus()
            }
        }

        btSearch.setOnClickListener {
            if (numberOfItems.text.toString().isEmpty()) { //if the field is empty  - show error
                numberOfItemsValidator = false
                search.clearFocus()
                numberOfItems.requestFocus()
                Toast.makeText(this, "Please enter a number in SEARCH", Toast.LENGTH_SHORT).show()
            } else {
                hideKeyboard()
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}

