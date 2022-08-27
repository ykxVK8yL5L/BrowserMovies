package com.github.browsermovies

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class ListActivity : Activity() {
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_category)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.main_browse_fragment,ListFragment() , "movielist").commit();
        }
        super.onCreate(savedInstanceState)
    }
}
