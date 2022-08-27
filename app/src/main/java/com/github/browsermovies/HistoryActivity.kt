package com.github.browsermovies

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class HistoryActivity : Activity() {
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_history)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        fragmentManager.beginTransaction().add(R.id.historyFragment,HistroyFragment() as Fragment, "HISTORY").commit();
        super.onCreate(savedInstanceState)
    }
}
