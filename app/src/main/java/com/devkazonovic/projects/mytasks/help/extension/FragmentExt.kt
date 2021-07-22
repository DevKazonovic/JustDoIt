package com.devkazonovic.projects.mytasks.help.extension

import android.view.View
import androidx.fragment.app.Fragment
import com.devkazonovic.projects.mytasks.help.view.ScrollChildSwipeRefreshLayout

fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {

    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}