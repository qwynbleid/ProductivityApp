package com.example.myapp.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.replaceFragment(
    container: Int,
    fragment: Fragment,
    tag: String = fragment.javaClass.simpleName
) = supportFragmentManager
    .beginTransaction()
    .replace(container, fragment, tag)
    .commit()