package com.github.browsermovies

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager.beginTransaction().add(R.id.mainfragment,MainFragment(),"MainActivity").commit()
    }
}
