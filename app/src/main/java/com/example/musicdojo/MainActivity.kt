package com.example.musicdojo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.viewpager.widget.ViewPager
import com.example.musicdojo.MainPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity. Intialise bottom navigation.
 */
class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewPager : ViewPager
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var mainPagerAdapter : MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.view_pager)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        mainPagerAdapter = MainPagerAdapter(
            supportFragmentManager
        )

        mainPagerAdapter.setItems(arrayListOf(MainScreen.TRAINING,
            MainScreen.METRONOME, MainScreen.TIMER))

        val defaultScreen = MainScreen.TRAINING
        scrollToScreen(defaultScreen)
        selectBottomNavigationViewMenuItem(defaultScreen.menuItemId)


        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(defaultScreen.titleStringId)

        // Listener for item selection in bottom nav menu
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        // Attach adapter to view pager which selects the bottom nav item and syncs
        viewPager.adapter = mainPagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val selectedScreen = mainPagerAdapter.getItems()[position]
                selectBottomNavigationViewMenuItem(selectedScreen.menuItemId)
                supportActionBar?.setTitle(selectedScreen.titleStringId)
            }
        })
    }

    /**
     * Selects the specified item in the bottom nav menu.
     */
    private fun selectBottomNavigationViewMenuItem(@IdRes menuItemId: Int) {
        bottomNavigationView.setOnNavigationItemSelectedListener(null)
        bottomNavigationView.selectedItemId = menuItemId
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    /**
     * Uses the ViewPager to scroll to the selected screen.
     */
    private fun scrollToScreen(mainScreen : MainScreen) {
        val screenPosition = mainPagerAdapter.getItems().indexOf(mainScreen)
        if (screenPosition != viewPager.currentItem) {
            viewPager.currentItem = screenPosition
        }
    }

    /**
     * Listener for registering bottom nav clicks.
     */
    override fun onNavigationItemSelected(menuItem : MenuItem): Boolean {
        getMainScreenForMenuItem(menuItem.itemId)?.let {
            scrollToScreen(it)
            supportActionBar?.setTitle(it.titleStringId)
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_items, menu)
        return true
    }



}

