package com.asimin.speedofbinarytree

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    //    val view = View.inflate(this, R.layout.activity_main, null)
//    private lateinit var binding: MainActivity
    private lateinit var numberOfItems: EditText
    private lateinit var search: EditText
    private lateinit var btGenerateArray: Button
    private lateinit var btGenerateBT: Button
    private lateinit var btSearch: Button
//    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberOfItems = findViewById<EditText>(R.id.etNumberOfItems)
        search = findViewById<EditText>(R.id.etSearch)
        btGenerateArray = findViewById<Button>(R.id.btnGenerateArray)
        btGenerateBT = findViewById<Button>(R.id.btnGenerateBT)
        btSearch = findViewById<Button>(R.id.btnSearch)
        val tvArrayCreationTime = findViewById<TextView>(R.id.tvArrayCreationTime)
        val tvArraySearchTime = findViewById<TextView>(R.id.tvArraySearchTime)
        val tvBTSearchTime = findViewById<TextView>(R.id.tvBinaryTreeSearchTime)
        val tvBtCreationTime = findViewById<TextView>(R.id.tvBinaryTreeCreationTime)
        var numberOfItemsValidator = false
        val binaryTree = BinaryTree<String>()

        numberOfItems.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (numberOfItems.text.toString().isEmpty()) { //if the field is empty  - show error
                    numberOfItemsValidator = false
                    Toast.makeText(this, "Please enter a number OFCL", Toast.LENGTH_SHORT).show()
                } else {
                    numberOfItemsValidator = true
                }
            } else {
                search.clearFocus()
            }
        }
        numberOfItems.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty()) { //if the field is empty  - show error
                numberOfItemsValidator = false
                tvArrayCreationTime.visibility = View.INVISIBLE
                tvBtCreationTime.visibility = View.INVISIBLE
            } else {
                numberOfItemsValidator = true
            }
        }

        btGenerateArray.setOnClickListener {
            if (!numberOfItemsValidator) {
                Toast.makeText(this, "Please enter a number Array-OCL", Toast.LENGTH_SHORT).show()
            } else {
                hideKeyboard()
                tvArrayCreationTime.visibility = View.VISIBLE
            }
        }
        btGenerateBT.setOnClickListener {
            if (numberOfItemsValidator) {
                hideKeyboard()
                tvBtCreationTime.visibility = View.VISIBLE
                generateBinaryTree(binaryTree, tvBtCreationTime)
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
            } else {
                hideKeyboard()
                //create string searchPrefix which will be created from the search text as text until fist space
                //if there is no space, then searchPrefix will be the same as search text
                val searchPrefix = search.text.toString().substringBefore(" ")
                val startTime = System.currentTimeMillis()
                search.setText(searchPrefix + " " + binaryTree.searchNode(searchPrefix))
                val endTime = System.currentTimeMillis()
                val timeTaken = endTime - startTime
                tvBTSearchTime.visibility = View.VISIBLE
                "Search Time: $timeTaken ms".also { tvBTSearchTime.text = it }
            }
        }

    }

    private fun generateBinaryTree(binaryTree: BinaryTree<String>, tvBtCreationTime: TextView) {
        lifecycleScope.launch {
            binaryTree.clear()
            val startTime = System.currentTimeMillis()
            withContext(Dispatchers.Default) {
                for (i in 1..numberOfItems.text.toString().toInt()) {
                    binaryTree.addNode(generateRandomString())
                }
            }
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            "Generated in: $timeTaken ms".also { tvBtCreationTime.text = it }
            search.setText(binaryTree.getRandomNode(numberOfItems.text.toString().toInt()-1))
        }
        Toast.makeText(this, "Binary Tree creation in progress...", Toast.LENGTH_SHORT).show()
    }


    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    private fun generateRandomString(): String {
        val charPool: List<Char> = ('A'..'Z') + ('a'..'z')
        return (1..10).map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }



}

