package com.github.browsermovies

import android.app.Application
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.leanback.app.VerticalGridFragment
import androidx.leanback.widget.*
import com.github.browsermovies.models.Site
import com.github.browsermovies.presenters.NoTitleVerticalGridPresenter
import com.github.browsermovies.presenters.SiteViewPresenter
import com.github.browsermovies.presenters.SiteViewTextPresenter
import com.github.browsermovies.settings.SysSettingsActivity
import com.github.browsermovies.utils.Utils
import com.google.gson.Gson
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class MainFragment : VerticalGridFragment() {
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL
    private var mAdapter: ArrayObjectAdapter? = null
    private lateinit var hisBtn: Button
    private lateinit var serverBtn: Button
    private lateinit var reloadBtn: Button
    private lateinit var delallBtn: Button
    private lateinit var settingBtn: Button
    private lateinit var inputBtn: Button
    private lateinit var searchBtn: Button
    private lateinit var showPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val presenter = NoTitleVerticalGridPresenter()
        //val presenter = VerticalGridPresenter(ZOOM_FACTOR,false)
        var showNum = COLUMNS
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val ns = prefs.getString("prefs_listnum_key", "c4")!!.replace("c","")
        if (ns!=null){
            showNum = ns.toInt()
        }
        //title = "如不能使用请人工授权"
        presenter.numberOfColumns =showNum
        hisBtn = activity.findViewById(R.id.btn_history)
        hisBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            startActivity(intent)
        })

        serverBtn = activity.findViewById(R.id.btn_servicer)
        serverBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, ServerActivity::class.java)
            startActivity(intent)
        })

        reloadBtn = activity.findViewById(R.id.btn_reload)
        reloadBtn.setOnClickListener(View.OnClickListener {
            mAdapter!!.clear()
            loadData()
        })


        settingBtn = activity.findViewById(R.id.btn_setting)
        settingBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, SysSettingsActivity::class.java)
            startActivity(intent)
        })


        inputBtn = activity.findViewById(R.id.btn_input)
        inputBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity,InputSettingActivity::class.java)
            startActivity(intent)
        })

        delallBtn = activity.findViewById(R.id.btn_del)
        delallBtn.setOnClickListener(View.OnClickListener {
            try {
                val file = File(activity.filesDir,"sites.json")
                file.delete()
                mAdapter!!.clear()
                loadData()
            }catch (e: Exception){
                Toast.makeText(activity,"文件删除失败，请检查权限", Toast.LENGTH_LONG).show()
            }

        })


//        searchBtn = activity.findViewById(R.id.btn_search)
//        searchBtn.setOnClickListener(View.OnClickListener {
//            val intent = Intent(activity,SearchActivity::class.java)
//            startActivity(intent)
//        })



        val listType = prefs.getString("prefs_main_list_type_key", "c0")!!.replace("c","")

        gridPresenter = presenter

        if (listType.toInt()==1){
            showPresenter = SiteViewTextPresenter()
        }else{
            showPresenter = SiteViewPresenter(activity)
        }

        //val presenterSelector = SiteViewPresenter(activity)
        mAdapter = ArrayObjectAdapter(showPresenter)
        adapter =mAdapter
        onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            val card = item as Site
            //Toast.makeText(activity,"Clicked on " + card.title,Toast.LENGTH_SHORT).show()
            val intent = Intent(activity,ListActivity::class.java)
            intent.putExtra("site", card)
            startActivity(intent)
        }
        loadData()
    }




    private fun loadData(){
        try {
            val json = Utils.inputStreamToString(File(activity.filesDir,"sites.json").inputStream())
            Log.d("filepathis",activity.filesDir.absolutePath)
            Log.d("jsonis",json)
            //val json = Utils.inputStreamToString(getResources().openRawResource(R.raw.sites))
            val sites:List<Site> = Gson().fromJson<Array<Site>>(json, Array<Site>::class.java).toMutableList()
            for (site in sites) {
                Log.d("createsite site:",site.toString())
                mAdapter!!.add(site)
            }
            setSelectedPosition(0)
        }catch (e: Exception){
            Toast.makeText(activity,"文件不存在或格式错误,请先更新网站源", Toast.LENGTH_LONG).show()
        }

    }


    companion object {
        private val COLUMNS = 4
    }



}
