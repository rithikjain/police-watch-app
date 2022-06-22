package com.dscvit.policewatch.ui.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import com.dscvit.policewatch.R
import com.dscvit.policewatch.databinding.LoadingDialogViewBinding

class LoadingDialog(context: Context) {

    private var binding: LoadingDialogViewBinding
    private var dialog: CustomDialog

    fun start(message: String = "") {
        if (message.isEmpty()) {
            binding.text.visibility = View.GONE
        } else {
            binding.text.text = message
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun stop() {
        dialog.dismiss()
    }

    init {
        val inflater = (context as Activity).layoutInflater
        binding = LoadingDialogViewBinding.inflate(inflater)

        binding.card.setCardBackgroundColor(Color.parseColor("#70000000"))
        dialog = CustomDialog(context)
        dialog.setContentView(binding.root)
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.dialogBackground)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}