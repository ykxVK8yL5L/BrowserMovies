package com.github.browsermovies


import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import com.github.browsermovies.m_detail.MovieDetailFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class MovieDetailActivity : Activity() {
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_detail)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        if (savedInstanceState == null) {
            val fragement = MovieDetailFragment() as Fragment
            fragmentManager.beginTransaction().add(R.id.videoDetiaFragment,fragement , "moviedetail").commit();
        }
        super.onCreate(savedInstanceState)

    }
}
