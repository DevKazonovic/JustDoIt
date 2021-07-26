package com.devkazonovic.projects.mytasks.help.extension

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.devkazonovic.projects.mytasks.domain.holder.Event
import com.google.android.material.snackbar.Snackbar

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
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
    timeLength: Int
) {

    snackBarEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let {
            showSnackBar(context.getString(it), timeLength)
        }
    })
}