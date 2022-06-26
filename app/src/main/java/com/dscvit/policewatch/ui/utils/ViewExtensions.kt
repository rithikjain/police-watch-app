package com.dscvit.policewatch.ui.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun View.showErrorSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setTextColor(Color.WHITE)
        .setBackgroundTint(Color.RED)
        .show()
}

fun View.showSuccessSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setTextColor(Color.WHITE)
        .setBackgroundTint(Color.parseColor("#008000"))
        .show()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}