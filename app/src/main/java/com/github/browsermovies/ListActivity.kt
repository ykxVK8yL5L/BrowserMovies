package com.github.browsermovies

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ListActivity : Activity() {
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_category)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.main_browse_fragment,ListFragment() , "movielist").commit();
        }
        super.onCreate(savedInstanceState)
    }
}
