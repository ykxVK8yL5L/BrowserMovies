package com.github.browsermovies


import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import com.github.browsermovies.m_detail.MovieDetailFragment


class MovieDetailActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_detail)
        if (savedInstanceState == null) {
            val fragement = MovieDetailFragment() as Fragment
            fragmentManager.beginTransaction().add(R.id.videoDetiaFragment,fragement , "moviedetail").commit();
        }
        super.onCreate(savedInstanceState)

    }
}
