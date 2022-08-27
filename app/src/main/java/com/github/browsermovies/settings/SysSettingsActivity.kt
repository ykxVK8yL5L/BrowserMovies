package com.github.browsermovies.settings

import android.app.Activity
import android.os.Bundle
import com.github.browsermovies.R


class SysSettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sys_settings)
        fragmentManager.beginTransaction().add(R.id.mainfragment,SysSettingsFragment(),"MainActivity").commit()
    }
}
