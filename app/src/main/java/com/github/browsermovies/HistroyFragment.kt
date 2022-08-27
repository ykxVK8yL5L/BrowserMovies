package com.github.browsermovies

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.leanback.app.VerticalGridFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.VerticalGridPresenter
import com.bumptech.glide.Glide
import com.github.browsermovies.libs.ApiService
import com.github.browsermovies.libs.HistoryManager
import com.github.browsermovies.libs.SSLSocketFactoryCompat
import com.github.browsermovies.models.History
import com.github.browsermovies.models.Movie
import com.github.browsermovies.presenters.HistoryPresenter
import com.github.browsermovies.presenters.TextPresenter

/**
 * A simple [Fragment] subclass.
 */
class HistroyFragment : VerticalGridFragment() {
    private var mAdapter: ArrayObjectAdapter? = null
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL
    private lateinit var delBtn: Button
    private lateinit var historyManager:HistoryManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "播放记录"
        delBtn = activity!!.findViewById(R.id.clearall)
        delBtn.setOnClickListener(View.OnClickListener {
            historyManager.delAll()
            mAdapter!!.clear()
        })
        historyManager = HistoryManager(activity)
        setupRowAdapter()

        onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            val card = item as History
            val intent = Intent(activity,MovieDetailActivity::class.java)
            intent.putExtra("movie", card.playMovie)
            intent.putExtra("category", card.category)
            intent.putExtra("playindex", card.playindex)
            intent.putExtra("site", card.playSite)
            startActivity(intent)
        }
    }

    private fun setupRowAdapter() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        var showNum = COLUMNS
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val ns = prefs.getString("prefs_sourcesnum_key", "c4")!!.replace("c","")
        if (ns!=null){
            showNum = ns.toInt()
        }
        gridPresenter.numberOfColumns = showNum
        setGridPresenter(gridPresenter)
        val cardPresenterSelector = HistoryPresenter()
        mAdapter = ArrayObjectAdapter(cardPresenterSelector)
        adapter = mAdapter
        createRows()
    }

    fun createRows(){
        val histories = historyManager.getAll()
        mAdapter!!.addAll(0,histories)
    }

    companion object {
        private val TAG = "History"
        private val categoryName="History"
        private val COLUMNS = 8
        private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
        private val HOST = ""
    }

}
