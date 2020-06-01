package com.example.musicdojo

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * Screens available for display in the main screen, with their respective titles,
 * icons, and menu item IDs and fragments.
 */
enum class MainScreen(
    @IdRes val menuItemId: Int,
    @DrawableRes val menuItemIconId: Int,
    @StringRes val titleStringId: Int,
    val fragment: Fragment
) {
    TRAINING(
        R.id.bottom_navigation_training, R.drawable.ic_training,
        R.string.menu_training,
        TrainingFragment()
    ),
    METRONOME(
        R.id.bottom_navigation_metronome, R.drawable.ic_metronome,
        R.string.menu_metronome, MetronomeFragment()
    ),
    TIMER(
        R.id.bottom_navigation_timer, R.drawable.ic_timer,
        R.string.menu_timer, TimerFragment()
    ),
    LOOPER(
        R.id.bottom_navigation_looper, R.drawable.ic_looper, R.string.looper, LooperFragment()
    )
}

fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
    for (mainScreen in MainScreen.values()) {
        if (mainScreen.menuItemId == menuItemId) {
            return mainScreen
        }
    }
    return null
}
