package com.github.browsermovies


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.renderscript.Script
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseFragment
import androidx.leanback.widget.*
import com.github.browsermovies.libs.ApiService
import com.github.browsermovies.libs.GridFragment
import com.github.browsermovies.libs.HistoryManager
import com.github.browsermovies.models.*
import com.github.browsermovies.presenters.CardPresenter
import com.github.browsermovies.presenters.TextPresenter
import com.github.browsermovies.utils.LogUtil
import com.github.browsermovies.utils.WebFetcher
import com.github.browsermovies.utils.XwalkBrowserFetcher
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Entities
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern
import java.util.*
import javax.script.ScriptEngineManager


class ListFragment : BrowseFragment() {

    private var mBackgroundManager: BackgroundManager? = null
    private var mRowsAdapter: ArrayObjectAdapter?=null
    private var mSelectedSite: Site? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        headersState = BrowseFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = resources.getColor(R.color.colorPrimary)
        mSelectedSite = activity.intent.getSerializableExtra("site") as Site
        title = mSelectedSite!!.title
        setOnSearchClickedListener {
            //Toast.makeText(activity, getString(R.string.app_name), Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("category",  mSelectedSite!!.name)
            intent.putExtra("site", mSelectedSite)
            startActivity(intent)
        }
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        createRows()
        adapter = mRowsAdapter
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager!!.attach(activity.window)
        mainFragmentRegistry.registerFragment(PageRow::class.java,PageRowFragmentFactory(mBackgroundManager!!))
    }
    private fun createRows() {
        for (i in 0 until mSelectedSite!!.category!!.size) {
            val header = HeaderItem(i.toLong(), mSelectedSite!!.category!![i].name)
            header.description = mSelectedSite!!.category!![i].link
            header.contentDescription = mSelectedSite!!.category!![i].next
            val pageRow = PageRow(header)
            mRowsAdapter!!.add(pageRow)
        }
    }
    private class PageRowFragmentFactory internal constructor(private val mBackgroundManager: BackgroundManager) : BrowseFragment.FragmentFactory<android.app.Fragment>() {

        override fun createFragment(rowObj: Any): android.app.Fragment {
            val row = rowObj as Row
            mBackgroundManager.drawable = null
            val cid = row.headerItem.id
            val mlist = ThumbListFragment()
            mlist.fetchUrl = row.headerItem.description.toString()
            mlist.next = row.headerItem.contentDescription.toString()
            return mlist
            throw IllegalArgumentException(String.format("Invalid row %s", rowObj))
        }


    }



    class ThumbListFragment : GridFragment(),OnItemViewSelectedListener{
        private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL
        private var mAdapter: ArrayObjectAdapter? = null
        private var pageNO: Int = 1
        private lateinit var mSelectedSite:Site
        public var pagesize = 0
        public var fetchUrl:String = ""
        public var next:String = "detail"
        private var useragent:String = ""
        private lateinit var processBar: ProgressBar
        private lateinit var fetchBrowser: XwalkBrowserFetcher
        private lateinit var defaultFetcher:WebFetcher
        private lateinit var sysconfig:ApiService
        private lateinit var cardPresenter:Presenter
        private lateinit var history: History
        private var mSelectedMovie: Movie? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            mSelectedSite = activity.intent.getSerializableExtra("site") as Site
            setupAdapter()
            Handler().postDelayed({
                loadNextPage()
            }, 1000)
            mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
        }

        override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        private fun setupAdapter() {
            val presenter = VerticalGridPresenter(ZOOM_FACTOR,false)
            var showNum = COLUMNS
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val ns = prefs.getString("prefs_listnum_key", "c4")!!.replace("c","")
            if (ns!=null){
                showNum = ns.toInt()
            }
            presenter.numberOfColumns =showNum
            gridPresenter = presenter

            if(mSelectedSite.presenter=="Text"){
                cardPresenter = TextPresenter()
            }else{
                cardPresenter = CardPresenter()
            }

            mAdapter = ArrayObjectAdapter(cardPresenter)
            sysconfig = ApiService()

            fetchBrowser = XwalkBrowserFetcher(activity)
            defaultFetcher = WebFetcher()
            useragent = mSelectedSite.list!!.useragent!!



            adapter = mAdapter
            onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
                val card = item as Movie
                //Log.d("Movie Url",card.linkUrl)
                //Toast.makeText(activity,"加载下一页数据", Toast.LENGTH_SHORT).show()
                Toast.makeText(activity,card.videoUrl,Toast.LENGTH_LONG).show()
                if(mSelectedSite.next=="play"||next=="play"){
                    mSelectedMovie = card
                    history = History()
                    history.category = mSelectedSite!!.name
                    history.playindex = 0
                    history.playlink = card.videoUrl
                    history.title = card.title
                    history.playMovie = mSelectedMovie
                    history.playSite = mSelectedSite
                    getPlayUrl(card.videoUrl!!)
                }else{
                    val intent = Intent(mainFragmentAdapter.fragment.activity,MovieDetailActivity::class.java)
                    intent.putExtra("movie", card)
                    intent.putExtra("category", mSelectedSite.name)
                    intent.putExtra("site", mSelectedSite)
                    startActivity(intent)
                }

            }

            this.processBar = mainFragmentAdapter.fragment.activity!!.findViewById<ProgressBar>(R.id.videos_progressbar)
            setOnItemViewSelectedListener(this)

        }
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?,item: Any?,rowViewHolder: RowPresenter.ViewHolder?,row: Row? ) {
            //Log.d("Adapter index is",item.toString())
            if(item!=null){
                val card = item as Movie
                val itemIndex = mAdapter!!.indexOf(card)
                if ((mAdapter!!.size()-itemIndex)<mSelectedSite.list!!.pagesize) {
                    Toast.makeText(activity,"加载下一页数据",Toast.LENGTH_SHORT).show()
                    loadNextPage();
                }
            }

        }
        private fun loadNextPage(){
            processBar.setVisibility(View.VISIBLE);
            if(!this.fetchUrl.contains("#pagerule")){
                this.fetchUrl = this.fetchUrl+"#pagerule"
            }
            var pagerule = ""

            if(!this.mSelectedSite.list!!.pagefilter.isNullOrEmpty() && this.pageNO!=1){
                var pagefilter = this.mSelectedSite.list!!.pagefilter!!.replace("#page",this.pageNO.toString())
                //val engine: ScriptEngine = ScriptEngineManager().getEngineByName("JavaScript")
                Log.d("pagefilteris",pagefilter)
                val engine = ScriptEngineManager().getEngineByName("rhino")
                val res = engine.eval(pagefilter).toString().toFloatOrNull()
                this.pageNO = res!!.toInt()

            }



            if(this.pageNO==1){
                pagerule = this.mSelectedSite.list!!.pageone!!
            }else{
                pagerule = this.mSelectedSite.list!!.pagerule!!.replace("#page",this.pageNO.toString())
            }
            var fetchvideoUrl = this.fetchUrl.replace("#pagerule",pagerule)

            if(this.mSelectedSite.list!!.fetchtype=="default"){
                defaultFetcher.fetchHtmlFromUrl(fetchvideoUrl,useragent,{result->handlePageResult(result)})
            }else{
                fetchBrowser.fetchHtmlFromUrl(fetchvideoUrl,useragent,{result->handlePageResult(result)})
            }

        }

        private fun handlePageResult(result:String){
            LogUtil().loge("listresultis",result)
            processBar.setVisibility(View.GONE);

            try {
                var doc = Jsoup.parse(result)
                var obj = JSONObject()
                var array = JSONArray()
                var listselector = mSelectedSite.list!!
                val videos = doc.select(listselector.videoscontainer!!.selector)
                for (video in videos){
                    var videotitle = ""
                    var thumbsrc = mSelectedSite.thumb
                    var linkhref = ""

                    var titleinfo = video
                    if(!listselector.title!!.selector.isNullOrEmpty()){
                        titleinfo = video.select(listselector!!.title!!.selector)[0]
                    }
                    if (listselector!!.title!!.attrName=="text"){
                        videotitle = titleinfo.text()
                    }else{
                        videotitle = titleinfo.attr(listselector!!.title!!.attrName)
                    }

                    if(!listselector.title!!.filter.isNullOrEmpty()){
                        var partn = listselector.title!!.filter
                        val findgroup = Pattern.compile(partn).matcher(videotitle)
                        while (findgroup.find()){
                            videotitle = findgroup.group(1)
                            break
                        }
                    }

                    var thumbinfo = video
                    if(!listselector.thumb!!.selector.isNullOrEmpty()){
                        thumbinfo = video.select(listselector!!.thumb!!.selector)[0]
                    }
                    if (!listselector.thumb!!.attrName.isNullOrEmpty()){
                        var thumburl = thumbinfo.attr(listselector!!.thumb!!.attrName)

                        if(!listselector.thumb!!.filter.isNullOrEmpty()){
                            var partn = listselector.thumb!!.filter
                            val findgroup = Pattern.compile(partn).matcher(thumburl)
                            while (findgroup.find()){
                                thumburl = findgroup.group(1)
                                break
                            }
                        }

                        if(!thumburl.isNullOrEmpty()){
                            if (thumburl.substring(0,4)!="http"){
                                thumburl = mSelectedSite.link+thumburl
                            }
                            thumbsrc = thumburl
                        }
                    }

                    var linkinfo = video
                    if(!listselector.link!!.selector.isNullOrEmpty()){
                        linkinfo = video.select(listselector!!.link!!.selector)[0]
                    }
                    var templink = ""
                    if (listselector.link!!.attrName=="text"){
                        templink = linkinfo.text()
                    }else{
                        templink = linkinfo.attr(listselector!!.link!!.attrName)
                    }

                    if(!listselector.link!!.filter.isNullOrEmpty()){
                        var partn = listselector.link!!.filter
                        val findgroup = Pattern.compile(partn).matcher(templink)
                        while (findgroup.find()){
                            templink = findgroup.group(1)
                            break
                        }
                    }

                    if(!templink.isNullOrEmpty()){
                        if (templink.substring(0,4)!="http"){
                            templink = mSelectedSite.link+templink
                        }
                        linkhref = templink
                    }


                    //Log.d("suolvetuis",thumbsrc)


                    var item = JSONObject()
                    //item.put("type", "Thumb_GRID")
                    item.put("title", videotitle)
                    item.put("description", videotitle)
                    item.put("cardImageUrl", thumbsrc)
                    item.put("backgroundImageUrl", thumbsrc)
                    item.put("videoUrl", linkhref)
                    array.put(item)

                }
                obj.put("movies", array)
                val json = obj.toString()
                val cardRow = Gson().fromJson(json, CardRow::class.java)
                mAdapter!!.addAll(mAdapter!!.size(), cardRow.movies!!)
                this.pageNO+=1
            }catch (e:java.lang.Exception){
                Toast.makeText(activity,"无法解析视频页面，请检查网站源", Toast.LENGTH_SHORT).show()
            }

        }

        private fun getPlayUrl(url:String){
            processBar.setVisibility(View.VISIBLE);
            fetchBrowser.fetchResourceFromUrl(url,this.mSelectedSite!!.play!!.useragent!!,{result->handlePlayResult(result)})
        }


        private fun handlePlayResult(result:String){
            //Log.d("playurlis",result)
            processBar.setVisibility(View.GONE)

            var playurl = result
            if(!mSelectedSite!!.play!!.filter.isNullOrEmpty()){
                var partn = mSelectedSite!!.play!!.filter
                val findgroup = Pattern.compile(partn).matcher(playurl)
                while (findgroup.find()){
                    playurl = findgroup.group(1)
                    break
                }
                playurl = playurl.unescapeUrl()
            }

            try {
                if(!playurl.isNullOrEmpty()){
                    Toast.makeText(activity,playurl, Toast.LENGTH_SHORT).show()
                    val historyManager = HistoryManager(activity)
                    historyManager.setHistory(history)
                    historyManager.saveWithTime(0)
                    var intent = Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(playurl), "video/*");
                    startActivity(intent)
                }else{
                    Toast.makeText(activity,"未找到播放源，请稍后重试", Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                Toast.makeText(activity,"无法解析播放地址，请稍后重试", Toast.LENGTH_SHORT).show()
            }
        }

        @PublishedApi
        internal fun String.unescapeHtml(): String {
            return Entities.unescape(this)
        }


        @PublishedApi
        internal fun String.unescapeUrl(): String {
            return URLDecoder.decode(this.replace("%u2026", "..."), Charset.forName("UTF-8").toString())
        }


        override fun onDestroy() {
            this.fetchBrowser.destory()
            super.onDestroy()
        }



        companion object {
            private val COLUMNS = 4
            private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
            private val PAGESIZE = 40
            private val HOST = "https://www.66s.cc"
        }


    }



    companion object {
        private val HEADER_ID_SETTING: Long = 20
        private val HEADER_NAME_SETTING = "设置"

    }


    }