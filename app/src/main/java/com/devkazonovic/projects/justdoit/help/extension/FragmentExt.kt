package com.devkazonovic.projects.justdoit.help.extension

import android.view.View
import androidx.fragment.app.Fragment
import com.devkazonovic.projects.justdoit.presentation.common.view.ScrollChildSwipeRefreshLayout

fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null,
) {

    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}