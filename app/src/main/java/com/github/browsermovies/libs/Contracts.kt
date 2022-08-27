package com.github.browsermovies.libs
import android.provider.BaseColumns
object Contracts {
    object Histories : BaseColumns {
        const val TABLE_NAME = "Histories"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_CATEGORY = "category"
        const val COLUMN_NAME_PLAYLINK = "playlink"
        const val COLUMN_NAME_PLAYINDEX = "playindex"
        const val COLUMN_NAME_PLAYMOVIE = "playMovie"
        const val COLUMN_NAME_PLAYSITE = "playSite"
        const val COLUMN_NAME_PLAYTIME = "playtime"

        const val SQL_CREATE_TABLE =
            "CREATE TABLE ${TABLE_NAME} (" +
                    "${COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${COLUMN_NAME_TITLE} TEXT," +
                    "${COLUMN_NAME_CATEGORY} TEXT," +
                    "${COLUMN_NAME_PLAYLINK} TEXT," +
                    "${COLUMN_NAME_PLAYINDEX} INTEGER," +
                    "${COLUMN_NAME_PLAYMOVIE} TEXT," +
                    "${COLUMN_NAME_PLAYSITE} TEXT," +
                    "${COLUMN_NAME_PLAYTIME} INTEGER)"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${Histories.TABLE_NAME}"
    }
}