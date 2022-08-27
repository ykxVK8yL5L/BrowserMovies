/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.content.Context
import androidx.leanback.widget.ImageCardView
import android.view.View
import com.github.browsermovies.R

/**
 * Simple presenter implementation to represent settings icon as cards.
 */
class SettingsIconPresenter(context: Context) : ImageCardViewPresenter(context, R.style.IconCardTheme) {

    override fun onCreateView(): ImageCardView {
        val imageCardView = super.onCreateView()
        imageCardView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setImageBackground(imageCardView, R.color.settings_card_background_focussed)
            } else {
                setImageBackground(imageCardView, R.color.settings_card_background)
            }
        }
        setImageBackground(imageCardView, R.color.settings_card_background)
        return imageCardView
    }

    private fun setImageBackground(imageCardView: ImageCardView, colorId: Int) {
        imageCardView.setBackgroundColor(context.resources.getColor(colorId))
    }
}
