package com.github.browsermovies.presenters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowPresenter
import com.github.browsermovies.R
import com.github.browsermovies.models.Site
import kotlin.properties.Delegates
import androidx.core.content.ContextCompat

class SiteRowPresenter : Presenter() {
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()
    private lateinit var mcontext: Context
//    constructor(){
//
//    }
//    constructor(context: Context){
//        mcontext = context
//    }


    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        sDefaultBackgroundColor = ContextCompat.getColor(parent!!.context, R.color.default_background)
        sSelectedBackgroundColor = ContextCompat.getColor(parent.context, R.color.selected_background)
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


    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val movie = item as Site
        val cardView = viewHolder!!.view as TextView
        cardView.text = movie.title
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val cardView = viewHolder!!.view as TextView
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