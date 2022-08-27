package com.github.browsermovies.presenters

import androidx.leanback.widget.VerticalGridPresenter

class NoTitleVerticalGridPresenter: VerticalGridPresenter() {
    override fun initializeGridViewHolder(vh: ViewHolder?) {
        super.initializeGridViewHolder(vh)
        var gridView = vh!!.gridView
        val top = 20 //this is the new value for top padding

        val bottom: Int = gridView.getPaddingBottom()
        val right: Int = gridView.getPaddingRight()
        val left: Int = gridView.getPaddingLeft()
        gridView.setPadding(left, top, right, bottom)
    }
}