package com.github.browsermovies.presenters

import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Layout
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
import com.github.browsermovies.models.Site
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class SiteViewTextPresenter : Presenter() {
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
        val movie = item as Site
        val cardView = viewHolder.view as TextView
        cardView.width = CARD_WIDTH
        cardView.height = CARD_HEIGHT
        cardView.textSize = 24f
        //cardView.textAlignment = Layout.Alignment.ALIGN_CENTER
        //cardView.text = movie.title+"\n"+movie.link+"\n"+movie.description
        var showlink = movie.link!!.replace("http://","").replace("https://","")
        cardView.setText(Html.fromHtml("<h1>"+movie.title+"</h1><span>"+showlink+"<br/>"+movie.description+"</span>"))
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

        private val CARD_WIDTH = 300
        private val CARD_HEIGHT = 220
    }
}
