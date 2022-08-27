package com.github.browsermovies.libs

import android.content.ContentValues
import android.content.Context
import com.github.browsermovies.models.History
import com.github.browsermovies.models.Movie
import com.github.browsermovies.models.Site
import com.google.gson.Gson

class HistoryManager(context: Context) {
    private val dbHelper= DbHelper(context)
    private lateinit var history:History
    private var hasplayed = false
    fun setHistory(history:History){
        this.history = history
        hasplayed = hasPlayed()
    }
    fun hasPlayed():Boolean{
        val db = dbHelper.readableDatabase
        //val cursor = db.rawQuery("select * from Histories where playlink ='"+history.playMovie!!.videoUrl+"' and playindex="+history.playindex,null)
        val cursor = db.rawQuery("select * from Histories where playlink ='"+history.playMovie!!.videoUrl+"'",null)
        var i = 0
        while (cursor.moveToNext()){
            val ptime = cursor.getLong(cursor.getColumnIndex("playtime"))
            this.history.playtime = ptime
            i++
        }
        cursor.close()
        if(i>0){
            return true
        }
        return false
    }

    fun insert(){
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(Contracts.Histories.COLUMN_NAME_TITLE, history.title)
            put(Contracts.Histories.COLUMN_NAME_CATEGORY, history.category)
            put(Contracts.Histories.COLUMN_NAME_PLAYINDEX, history.playindex)
            put(Contracts.Histories.COLUMN_NAME_PLAYLINK, history.playMovie!!.videoUrl)
            put(Contracts.Histories.COLUMN_NAME_PLAYTIME, history.playtime)
            put(Contracts.Histories.COLUMN_NAME_PLAYMOVIE, Gson().toJson(history.playMovie))
            put(Contracts.Histories.COLUMN_NAME_PLAYSITE, Gson().toJson(history.playSite))
        }
        db.insert(Contracts.Histories.TABLE_NAME,null,values)
    }

    fun update(){
        //Log.d("updatetimeis",history.playtime.toString())
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(Contracts.Histories.COLUMN_NAME_TITLE, history.title)
            put(Contracts.Histories.COLUMN_NAME_CATEGORY, history.category)
            put(Contracts.Histories.COLUMN_NAME_PLAYINDEX, history.playindex)
            put(Contracts.Histories.COLUMN_NAME_PLAYLINK, history.playMovie!!.videoUrl)
            put(Contracts.Histories.COLUMN_NAME_PLAYTIME, history.playtime)
            put(Contracts.Histories.COLUMN_NAME_PLAYMOVIE, Gson().toJson(history.playMovie))
            put(Contracts.Histories.COLUMN_NAME_PLAYSITE, Gson().toJson(history.playSite))
        }
        val selection = "${Contracts.Histories.COLUMN_NAME_PLAYLINK} = ?"
        val selectionArgs = arrayOf("${history.playMovie!!.videoUrl}")
        val count = db.update(Contracts.Histories.TABLE_NAME,values,selection,selectionArgs)
    }

    fun saveWithTime(time:Long){
        history.playtime = time
        if (hasplayed){
            update()
        }else{
            insert()
        }
    }
    fun getPlayTime():Long{
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("select * from Histories where playlink ='"+history.playMovie!!.videoUrl+"' and playindex="+history.playindex,null)
        var i = 0
        while (cursor.moveToNext()){
            val ptime = cursor.getLong(cursor.getColumnIndex("playtime"))
            this.history.playtime = ptime
            i++
        }
        cursor.close()
        return this.history.playtime
    }

    fun getAll():List<History>{
        var histories = mutableListOf<History>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("select * from Histories  ORDER BY id DESC",null)
        var i = 0
        while (cursor.moveToNext()){

            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val title = cursor.getString(cursor.getColumnIndex("title"))
            val category = cursor.getString(cursor.getColumnIndex("category"))
            val playlink = cursor.getString(cursor.getColumnIndex("playlink"))
            val playindex = cursor.getInt(cursor.getColumnIndex("playindex"))
            val playMovie = cursor.getString(cursor.getColumnIndex("playMovie"))
            val playSite = cursor.getString(cursor.getColumnIndex("playSite"))
            val playtime = cursor.getLong(cursor.getColumnIndex("playtime"))

            var history = History()
            history.id = id
            history.title = title
            history.category = category
            history.playlink = playlink
            history.playindex = playindex
            history.playMovie = Gson().fromJson(playMovie,Movie::class.java)
            history.playSite = Gson().fromJson(playSite,Site::class.java)
            history.playtime = playtime
            histories.add(i,history)
            i++
        }
        cursor.close()
        return histories
    }

    fun delAll(){
        val db = dbHelper.readableDatabase
        db.execSQL("DELETE FROM Histories")
    }


}