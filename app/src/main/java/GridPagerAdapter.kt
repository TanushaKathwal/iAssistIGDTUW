package com.example.iassistdatabase

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.iassistdatabase.GridItem
import com.example.iassistdatabase.R

class GridPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Example data: 2 pages, each with 4 items
    private val pages = listOf(
        listOf(
            GridItem(R.drawable.eventfinder, "Event Finder"),
            GridItem(R.drawable.lostandfound, "Lost and Found"),
            GridItem(R.drawable.roomfinder, "Room Finder"),
            GridItem(R.drawable.sgpacalc, "SGPA Calculator")
        ),
        listOf(
            GridItem(R.drawable.goaltracker, "Goal Tracker"),
            GridItem(R.drawable.academiccorner, "Academic Corner"),
            GridItem(R.drawable.circulars, "Circulars"),
            GridItem(R.drawable.lifeinmotion, "Life In Motion")
        )
    )

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = GridPagerFragment.newInstance(pages[position])
}