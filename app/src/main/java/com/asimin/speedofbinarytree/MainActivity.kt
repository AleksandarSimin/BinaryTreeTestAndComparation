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
import java.text.SimpleDateFormat
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
    private var inSearchTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberOfItems = findViewById<EditText>(R.id.etNumberOfItems)
        btGenerateArray = findViewById<Button>(R.id.btnGenerateArray)
        btGenerateBT = findViewById<Button>(R.id.btnGenerateBT)
        btSearch = findViewById<Button>(R.id.btnSearch)
        search.setTextColor(resources.getColor(R.color.search_text, null))
        search.text = getString(R.string.automatically_generated)

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
                search.text = getString(R.string.automatically_generated)
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
                if (searchList.size > 2500) Toast.makeText(
                    this,
                    "Searching..., Please Wait!",
                    Toast.LENGTH_SHORT
                ).show()
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

        setConditionalViewFieldTimer(search)
    }

    private fun setConditionalViewFieldTimer(textView: TextView) { //set timer to update time every second
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (inSearch) {
                    textView.text =
                        SpannableStringBuilder(getString(R.string.search_time) + (System.currentTimeMillis() - inSearchTime) / 1000 + " seconds")
                }
            }
        }, 0, 1000)
    }


    private val searchList = ArrayList<String>()
    private fun generateSearchListFromArray() {
        val size = numberOfItems.text.toString().toInt()
        if (arrayList.isEmpty()) {
            Toast.makeText(this, "Generate Array First", Toast.LENGTH_SHORT).show()
            return
        }
        searchList.clear()
        val newSize = if (size < 100) size else if (size > 5000) 5000 else (size * 0.05).toInt()
        val random = Random()
        for (i in 0 until newSize) {
            searchList.add(arrayList[random.nextInt(arrayList.size - 1)])
        }
        search.text =
            SpannableStringBuilder(getString(R.string.search_list_contain) + " ${searchList.size} items")
    }

    private fun searchArrayFromList() {
        inSearch = true
        inSearchTime = System.currentTimeMillis()
        numberOfItems.isEnabled = false
        tvArraySearchTime.visibility = View.VISIBLE
        tvArraySearchTime.text = getString(R.string.searching_wait)
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            var found = 0
            withContext(Dispatchers.Default) {
                for (i in searchList) {
                    if (arrayList.contains(i)) found++
                }
            }
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            inSearch = false
            inSearchTime = 0
            search.text = SpannableStringBuilder("Found $found of ${searchList.size} items")
            blinkViewField(tvArraySearchTime, 1)
            showSearchTime(tvArraySearchTime, found, timeTaken)
            numberOfItems.isEnabled = true
        }
    }

    private fun searchBinaryTreeFromList() {
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            var found = 0
            withContext(Dispatchers.Default) {
                for (i in searchList) {
                    if (binaryTree.containsNode(i)) found++
                }
            }
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            tvBTSearchTime.visibility = View.VISIBLE
            blinkViewField(tvBTSearchTime, 1)
            showSearchTime(tvBTSearchTime, found, timeTaken)
        }
    }

    private val arrayList = ArrayList<String>()
    private val binaryTree = BinaryTree<String>()
    private fun generateArray() {
        numberOfItems.clearFocus()
        arrayList.clear()
        binaryTree.clear()
        tvBTSearchTime.visibility = View.INVISIBLE
        tvBTCreationTime.visibility = View.VISIBLE
        tvBTCreationTime.text = getString(R.string.binary_tree_is_empty)
        tvArrayCreationTime.visibility = View.VISIBLE
        tvArrayCreationTime.text = getString(R.string.creation_in_progress)
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            withContext(Dispatchers.Default) {
                for (i in 0 until numberOfItems.text.toString().toInt()) {
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
            generateSearchListFromArray()
        }
    }

    private fun generateBinaryTreeFromArrayList(binaryTree: BinaryTree<String>) {
        numberOfItems.clearFocus()
        tvBTCreationTime.visibility = View.VISIBLE
        tvBTCreationTime.text = getString(R.string.creation_from_array_in_progress)
        lifecycleScope.launch {
            binaryTree.clear()
            val startTime = System.currentTimeMillis()
            withContext(Dispatchers.Default) {
                for (i in 0..(arrayList.size - 1)) {
                    binaryTree.addNode(arrayList[i])
                }
            }
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            "Array -> BinaryTree for: $timeTaken ms".also { tvBTCreationTime.text = it }
            search.text =
                SpannableStringBuilder(getString(R.string.search_list_contain) + " ${searchList.size} items")
        }
    }


    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            numberOfItems.clearFocus()
        }
    }

    private val charPool: List<Char> = ('A'..'Z') + ('a'..'z')
    private fun generateRandomString(): String {
        return (1..10).map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun blinkViewField(textView: TextView, times: Int) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 800 //You can manage the blinking time with this parameter
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = times
        textView.startAnimation(anim)
    }

    private fun showSearchTime(textView: TextView, found: Int, timeTaken: Long) {
        "Search Time: $timeTaken ms".also { textView.text = it }
    }

    private fun getTimeFromDate(): String {
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return format.format(Date())
    }

}

