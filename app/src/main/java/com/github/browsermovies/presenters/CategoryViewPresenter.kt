package com.github.browsermovies.presenters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.leanback.widget.ImageCardView
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.github.browsermovies.R
import com.github.browsermovies.models.Category
import kotlin.properties.Delegates

/**
 * Created by susnata on 3/17/18.
 */



class CategoryViewPresenter : Presenter {
    private var mDefaultCardImage: Drawable? = null
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()
    private lateinit var mcontext: Context
    constructor(){

    }
    constructor(context:Context){
        mcontext = context
    }


    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {


        sDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)
        sSelectedBackgroundColor = ContextCompat.getColor(parent.context,R.color.selected_background)
        mDefaultCardImage = ContextCompat.getDrawable(parent.context,
            R.drawable.movie
        )

        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return Presenter.ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val movie = item as Category
        val cardView = viewHolder.view as ImageCardView
        if (movie.localImageResource != null) {
            cardView.titleText = movie.title
            cardView.contentText = movie.description
            cardView.setMainImageDimensions(
                CARD_WIDTH,
                CARD_HEIGHT
            )
            val resourceId = mcontext.getResources().getIdentifier(movie.getLocalImageResourceName(),"drawable", mcontext.getPackageName())
            Glide.with(mcontext)
                .asBitmap()
                .load(resourceId)
                .into(cardView.mainImageView)
        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        //Log.d(TAG, "onUnbindViewHolder")
        val cardView = viewHolder.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }

    companion object {
        private val TAG = "CardPresenter"

        private val CARD_WIDTH = 313
        private val CARD_HEIGHT = 176
    }
}