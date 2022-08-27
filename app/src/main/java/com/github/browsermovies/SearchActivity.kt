package com.github.browsermovies

import android.app.Activity
import android.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class SearchActivity : Activity() {

    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_search)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        fragmentManager.beginTransaction().add(R.id.search_fragment,SearchFragment() , "search").commit();
        super.onCreate(savedInstanceState)
    }
}
