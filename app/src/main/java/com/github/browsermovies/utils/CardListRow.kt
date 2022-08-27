package com.github.browsermovies.utils

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ObjectAdapter
import com.github.browsermovies.models.CardRow


/**
 * Created by susnata on 3/17/18.
 */
class CardListRow(val header: HeaderItem, adapter: ObjectAdapter, var cardRow: CardRow?) : ListRow(header, adapter) {

//    var cardRow: CardRow? = null
//
//    init {
//        cardRow = cardRow
//    }

}
