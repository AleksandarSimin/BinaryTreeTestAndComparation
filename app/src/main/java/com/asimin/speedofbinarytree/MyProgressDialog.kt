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
    private val progressBarArray: ProgressBar
    private val progressBarBT: ProgressBar
    private val progressTitle: TextView
    private val progressProgressPercentageArray: TextView
    private val progressProgressPercentageBT: TextView


    init {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.progress_dialog_layout, null)
        progressTitle = view.findViewById(R.id.tvTitle)
        progressBarArray = view.findViewById(R.id.progressBarArray)
        progressBarBT = view.findViewById(R.id.progressBarBT)
        progressProgressPercentageArray = view.findViewById(R.id.tvProgressPercentageArray)
        progressProgressPercentageBT = view.findViewById(R.id.tvProgressPercentageBT)
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
    suspend fun updateProgressDialogArray(progress: Int) {
        withContext(Dispatchers.Main) {
            progressBarArray.progress = progress
            progressProgressPercentageArray.text = "$progress%"
        }
    }

    suspend fun updateProgressDialogBT(progress: Int) {
        withContext(Dispatchers.Main) {
            progressBarBT.progress = progress
            progressProgressPercentageBT.text = "$progress%"
        }
    }
}
