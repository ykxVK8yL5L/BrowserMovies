package com.github.browsermovies.presenters

import android.content.Context
import androidx.leanback.widget.ImageCardView
import android.view.ContextThemeWrapper
import com.bumptech.glide.Glide
import com.github.browsermovies.R
import com.github.browsermovies.models.Card

/**
 * Created by susnata on 3/17/18.
 */
open class ImageCardViewPresenter @JvmOverloads constructor(context: Context, cardThemeResId: Int = R.style.DefaultCardTheme) : AbstractCardPresenter<ImageCardView>(ContextThemeWrapper(context, cardThemeResId)) {

    override protected fun onCreateView(): ImageCardView {
//        imageCardView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                Toast.makeText(getContext(), "Clicked on ImageCardView", Toast.LENGTH_SHORT).show();
        //            }
        //        });
        return ImageCardView(context)
    }

    override fun onBindViewHolder(card: Card, cardView: ImageCardView) {
        cardView.tag = card
        cardView.titleText = card.title
        cardView.contentText = card.description
        if (card.getLocalImageResourceName() != null) {
            val resourceId = context.getResources()
                    .getIdentifier(card.getLocalImageResourceName(),
                            "drawable", context.getPackageName())
            Glide.with(context)
                    .asBitmap()
                    .load(resourceId)
                    .into(cardView.mainImageView)
        }
    }

}