package com.agromall.clockin.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun addFragmentToBackStack(
    fragmentManager: FragmentManager, fragment: Fragment,
    tag: String, frameId: Int
) {
    val transaction = fragmentManager.beginTransaction()
    transaction.add(frameId, fragment, tag)
        .addToBackStack(tag).commit()
}

fun addFragment(
    fragmentManager: FragmentManager, fragment: Fragment,
    tag: String, frameId: Int
) {
    val transaction = fragmentManager.beginTransaction()
    transaction.add(frameId, fragment, tag)
        .commit()
}

fun replaceFragmentToBackStack(
    fragmentManager: FragmentManager, fragment: Fragment,
    tag: String, frameId: Int
) {
    val transaction = fragmentManager.beginTransaction()
    transaction.replace(frameId, fragment, tag)
        .addToBackStack(tag).commit()
}

fun replaceFragment(
    fragmentManager: FragmentManager, fragment: Fragment,
    tag: String, frameId: Int
) {
    val transaction = fragmentManager.beginTransaction()
    transaction.replace(frameId, fragment, tag)
        .commit()
}

fun clearFragmentBackstack(fragmentManager: FragmentManager) {
    val count = fragmentManager.backStackEntryCount
    for (i in 0 until count) {
        fragmentManager.popBackStack()
    }
}