package com.devkazonovic.projects.justdoit

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matchers

fun assertViewIsDisplayed(id: Int): ViewInteraction {
    return Espresso.onView(ViewMatchers.withId(id))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}

fun assertViewIsNotDisplayed(id: Int): ViewInteraction {
    return Espresso.onView(ViewMatchers.withId(id))
        .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
}