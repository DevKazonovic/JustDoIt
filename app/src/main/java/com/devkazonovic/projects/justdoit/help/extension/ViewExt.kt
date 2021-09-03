package com.devkazonovic.projects.justdoit.help.extension

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.devkazonovic.projects.justdoit.domain.holder.Event
import com.google.android.material.snackbar.Snackbar

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.disable() {
    this.isEnabled = false
    this.isClickable = false
    this.isFocusable = false
}

fun Array<out View>.disable() {
    this.forEach { it.disable() }
}


fun View.enable() {
    this.isEnabled = true
    this.isClickable = true
    this.isFocusable = true
}

fun Array<out View>.enable() {
    this.forEach { it.enable() }
}

fun View.showSnackBar(snackBarText: String, timeLength: Int) {
    Snackbar.make(this, snackBarText, timeLength).run {
        addCallback(object : Snackbar.Callback() {
            override fun onShown(sb: Snackbar?) {
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            }
        })

    }.setAnchorView(this).show()
}

fun View.setupSnackBar(
    lifecycleOwner: LifecycleOwner,
    snackBarEvent: LiveData<Event<Int>>,
    timeLength: Int,
) {

    snackBarEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let {
            showSnackBar(context.getString(it), timeLength)
        }
    })
}