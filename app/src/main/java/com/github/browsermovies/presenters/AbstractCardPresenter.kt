package com.github.browsermovies.presenters

import android.content.Context

import androidx.leanback.widget.BaseCardView
import androidx.leanback.widget.Presenter
import android.view.ViewGroup
import com.github.browsermovies.models.Card

abstract class AbstractCardPresenter<T : BaseCardView>(val context: Context) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val cardView = onCreateView()
        return Presenter.ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val card = item as Card
        onBindViewHolder(card, viewHolder.view as T)
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        onUnbindViewHolder(viewHolder.view as T)
    }

    fun onUnbindViewHolder(cardView: T) {
        // Nothing to clean up. Override if necessary.
    }

    /**
     * Invoked when a new view is created.
     *
     * @return Returns the newly created view.
     */
    protected abstract fun onCreateView(): T

    /**
     * Implement this method to update your card's view with the data bound to it.
     *
     * @param card The model containing the data for the card.
     * @param cardView The view the card is bound to.
     * @see Presenter.onBindViewHolder
     */
    abstract fun onBindViewHolder(card: Card, cardView: T)

    companion object {

        private val TAG = "AbstractCardPresenter"
    }
}
