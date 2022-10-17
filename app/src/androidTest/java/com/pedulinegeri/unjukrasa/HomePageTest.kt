package com.pedulinegeri.unjukrasa

import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.pedulinegeri.unjukrasa.home.HomePageFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomePageTest {

    @Test
    fun trendingListDisplayed() {
        launchFragmentInContainer<HomePageFragment>()

        onView(withText("Trending")).check(matches(isDisplayed()))

        onView(withId(R.id.rvTrending)).check(matches(isDisplayed()))

    }
}
