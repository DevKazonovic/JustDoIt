package com.devkazonovic.projects.mytasks.presentation.tasks.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class TasksViewPagerAdapter(
    activity: FragmentActivity,
    manager: FragmentManager,
    private val fragments: Array<Fragment>,
) : FragmentStateAdapter(manager, activity.lifecycle) {


    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}