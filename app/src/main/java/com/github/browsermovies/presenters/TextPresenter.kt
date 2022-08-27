/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.browsermovies.presenters

import android.graphics.drawable.Drawable
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.TextureView
import android.view.ViewGroup
import android.widget.TextView


import com.bumptech.glide.Glide
import com.github.browsermovies.R
import com.github.browsermovies.models.Movie
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class TextPresenter : Presenter() {
    //private var mDefaultCardImage: Drawable? = null
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {


        sDefaultBackgroundColor = ContextCompat.getColor(parent.context,
            R.color.default_background
        )
        sSelectedBackgroundColor =
            ContextCompat.getColor(parent.context,
                R.color.selected_background
            )


        val textView = object : TextView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        textView.isFocusable = true
        textView.setPadding(20,20,20,20)
        textView.setTextColor(parent.context.getResources().getColor(R.color.text_source))
        textView.isFocusableInTouchMode = true
        updateCardBackgroundColor(textView, false)
        return Presenter.ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val movie = item as Movie
        val cardView = viewHolder.view as TextView

        cardView.text = movie.title

    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val cardView = viewHolder.view as TextView
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBackgroundResource(R.color.default_background)
    }

    private fun updateCardBackgroundColor(view: TextView, selected: Boolean) {
        val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
    }

    companion object {
        private val TAG = "CardPresenter"

        private val CARD_WIDTH = 313
        private val CARD_HEIGHT = 176
    }
}
