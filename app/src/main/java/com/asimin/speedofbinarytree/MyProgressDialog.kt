package com.asimin.speedofbinarytree

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyProgressDialog(context: Context) {
    private val progressDialog: AlertDialog
    private val progressBar: ProgressBar
    private val progressTitle: TextView
    private val progressProgressPercentage: TextView


    init {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.progress_dialog_layout, null)
        progressBar = view.findViewById(R.id.progressBar)
        progressTitle = view.findViewById(R.id.tvTitle)
        progressProgressPercentage = view.findViewById(R.id.tvProgressPercentage)
        builder.setView(view)
        builder.setCancelable(false)
        progressDialog = builder.create()
    }

    // Show the ProgressDialog
    fun showProgressDialog() {
        progressDialog.show()
    }

    // Hide the ProgressDialog
    fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    // Update the ProgressDialog with the current progress value
    suspend fun updateProgressDialog(progress: Int) {
        withContext(Dispatchers.Main) {
            progressBar.progress = progress
            progressProgressPercentage.text = "$progress%"
        }
    }
}
