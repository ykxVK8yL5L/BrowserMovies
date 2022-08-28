package com.github.browsermovies

import android.app.Activity
import android.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SearchActivity : Activity() {

    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_search)
        fragmentManager.beginTransaction().add(R.id.search_fragment,GlobalSearchFragment() , "search").commit();
        super.onCreate(savedInstanceState)
    }
}
