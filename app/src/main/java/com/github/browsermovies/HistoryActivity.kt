package com.github.browsermovies

import android.app.Activity
import android.app.Fragment
import android.os.Bundle


class HistoryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_history)
        fragmentManager.beginTransaction().add(R.id.historyFragment,HistroyFragment() as Fragment, "HISTORY").commit();
        super.onCreate(savedInstanceState)
    }
}
