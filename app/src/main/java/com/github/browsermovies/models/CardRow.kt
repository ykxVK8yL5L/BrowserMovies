package com.github.browsermovies.models

import com.google.gson.annotations.SerializedName

class CardRow {

    @SerializedName("type")
    val type = TYPE_DEFAULT
    // Used to determine whether the row shall use shadows when displaying its cards or not.
    @SerializedName("shadow") private val mShadow = true
    @SerializedName("title")
    val title: String? = null

    @SerializedName("cards")
    val cards: List<Card>? = null

    @SerializedName("movies")
    val movies: List<Movie>? = null

    fun useShadow(): Boolean {
        return mShadow
    }

    companion object {

        // default is a list of cards
        val TYPE_DEFAULT = 0
        // section header
        val TYPE_SECTION_HEADER = 1
        // divider
        val TYPE_DIVIDER = 2
    }
}