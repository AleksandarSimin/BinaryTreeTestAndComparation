package com.asimin.speedofbinarytree

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
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
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var numberOfItems: EditText
    private lateinit var btGenerateArray: Button
    private lateinit var btGenerateBT: Button
    private lateinit var btSearch: Button
    private val tvArraySearchTime: TextView by lazy { findViewById<TextView>(R.id.tvArraySearchTime) }
    private val tvArrayCreationTime: TextView by lazy { findViewById<TextView>(R.id.tvArrayCreationTime) }
    private val tvBTSearchTime: TextView by lazy { findViewById<TextView>(R.id.tvBinaryTreeSearchTime) }
    private val tvBTCreationTime: TextView by lazy { findViewById<TextView>(R.id.tvBinaryTreeCreationTime) }
    private val search: TextView by lazy { findViewById<TextView>(R.id.tvSearch) }
    private val tvEqual: TextView by lazy { findViewById<TextView>(R.id.tvEquals) }
    private var inSearch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberOfItems = findViewById<EditText>(R.id.etNumberOfItems)
        btGenerateArray = findViewById<Button>(R.id.btnGenerateArray)
        btGenerateBT = findViewById<Button>(R.id.btnGenerateBT)
        btSearch = findViewById<Button>(R.id.btnSearch)
        search.setTextColor(resources.getColor(R.color.search_text, null))
        var numberOfItemsValidator = false

        numberOfItems.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                numberOfItemsValidator = numberOfItems.text.toString().isNotEmpty()
            }
        }
        numberOfItems.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty()) { //if the field is empty  - show error
                numberOfItemsValidator = false
                tvArrayCreationTime.visibility = View.INVISIBLE
                tvBTCreationTime.visibility = View.INVISIBLE
                tvArraySearchTime.visibility = View.INVISIBLE
                tvBTSearchTime.visibility = View.INVISIBLE
                arrayList.clear()
                binaryTree.clear()
                searchList.clear()
                search.text = null
            } else {
                if (text.toString().toInt() > 1000000) {
                    numberOfItems.text = SpannableStringBuilder("1000000")
                    Toast.makeText(this, "Maximum is 1,000,000", Toast.LENGTH_SHORT).show()
                }
                numberOfItemsValidator = true
            }
        }

        btGenerateArray.setOnClickListener {
            if (numberOfItemsValidator) {
                hideKeyboard()
                tvArrayCreationTime.visibility = View.VISIBLE
                tvArraySearchTime.visibility = View.INVISIBLE
                tvEqual.text = null
                generateArray()
            }
        }

        btGenerateBT.setOnClickListener {
            if (numberOfItemsValidator) {
                hideKeyboard()
                tvBTCreationTime.visibility = View.VISIBLE
                tvBTSearchTime.visibility = View.INVISIBLE
                if (arrayList.isNotEmpty()) {
                    generateBinaryTreeFromArrayList(binaryTree)
                    tvEqual.text = "=="
                } else {
                    Toast.makeText(this, "Generate Array First", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btSearch.setOnClickListener {
            if (inSearch) return@setOnClickListener
            if (numberOfItems.text.toString().isEmpty()) { //if the field is empty  - show error
                numberOfItemsValidator = false
                numberOfItems.requestFocus()
            } else {
                hideKeyboard()
                if (searchList.size > 2500) Toast.makeText(this, "Searching ..., Please Wait!", Toast.LENGTH_SHORT).show()
                if (arrayList.isEmpty()) {
                    tvArrayCreationTime.text = getString(R.string.array_is_empty)
                    tvArrayCreationTime.visibility = View.VISIBLE
                    tvArraySearchTime.visibility = View.INVISIBLE
                } else {
                    tvArraySearchTime.text = null
                    searchArrayFromList()
                }
                if (binaryTree.root == null) {
                    tvBTCreationTime.text = getText(R.string.binary_tree_is_empty)
                    tvBTCreationTime.visibility = View.VISIBLE
                    tvBTSearchTime.visibility = View.INVISIBLE
                } else {
                    tvBTSearchTime.text = null
                    searchBinaryTreeFromList()
                }
            }
        }

    }

    private val searchList = ArrayList<String>()
    private fun generateSearchListFromArray(size: Int) {
        if (arrayList.isEmpty()) {
            Toast.makeText(this, "Generate Array First", Toast.LENGTH_SHORT).show()
            return
        }
        searchList.clear()
        val newSize = if (size > 5000) 5000 else size
        val random = Random()
        for (i in 0 until newSize) {
            searchList.add(arrayList[random.nextInt(arrayList.size-1)])
        }
        search.text = SpannableStringBuilder("Search from list of ${searchList.size} items")
    }

    private fun searchArrayFromList() {
        inSearch = true
        numberOfItems.isEnabled = false
        tvArraySearchTime.visibility = View.VISIBLE
        tvArraySearchTime.text = getString(R.string.searching_wait)
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            withContext(Dispatchers.Default) {
                for (i in searchList) {
                    arrayList.contains(i)
                }
            }
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            "Search Time: $timeTaken ms".also { tvArraySearchTime.text = it }
            blinkTextInSeconds(tvArraySearchTime, 1)
            inSearch = false
            numberOfItems.isEnabled = true
        }
    }

    private fun searchBinaryTreeFromList() {
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            withContext(Dispatchers.Default) {
                for (i in searchList) {
                    binaryTree.containsNode(i)
                }
            }
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            tvBTSearchTime.visibility = View.VISIBLE
            "Search Time: $timeTaken ms".also { tvBTSearchTime.text = it }
            blinkTextInSeconds(tvBTSearchTime, 1)
        }
    }

    private val arrayList = ArrayList<String>()
    private val binaryTree = BinaryTree<String>()
    private fun generateArray() {
        arrayList.clear()
        binaryTree.clear()
        tvBTSearchTime.visibility = View.INVISIBLE
        tvBTCreationTime.visibility = View.VISIBLE
        tvBTCreationTime.text = getString(R.string.binary_tree_is_empty)
        tvArrayCreationTime.visibility = View.VISIBLE
        tvArrayCreationTime.text = getString(R.string.creation_in_progress)
        search.text = null
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            withContext(Dispatchers.Default) {
                for (i in 1..numberOfItems.text.toString().toInt()) {
                    arrayList.add(generateRandomString())
                }
            }
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            "Array created for: $timeTaken ms".also {
                tvArrayCreationTime.text = it
            }
            val index = Random().nextInt(arrayList.size)
            search.text = SpannableStringBuilder(arrayList[index] + " [$index]")
            generateSearchListFromArray((numberOfItems.text.toString().toInt()*0.05).toInt())
        }
    }

    private fun generateBinaryTreeFromArrayList(binaryTree: BinaryTree<String>) {
        tvBTCreationTime.visibility = View.VISIBLE
        tvBTCreationTime.text = getString(R.string.creation_from_array_in_progress)
        lifecycleScope.launch {
            binaryTree.clear()
            val startTime = System.currentTimeMillis()
            withContext(Dispatchers.Default) {
                for (i in 0..(arrayList.size-1)) {
                    binaryTree.addNode(arrayList[i])
                }
            }
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            "Array -> BinaryTree for: $timeTaken ms".also { tvBTCreationTime.text = it }
        }
    }


    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private val charPool: List<Char> = ('A'..'Z') + ('a'..'z')
    private fun generateRandomString(): String {
        return (1..10).map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }


    private fun blinkTextInSeconds(blinkingText: TextView, i: Int) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 800 //You can manage the blinking time with this parameter
        anim.startOffset = 200
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = i
        blinkingText.startAnimation(anim)
    }

}

