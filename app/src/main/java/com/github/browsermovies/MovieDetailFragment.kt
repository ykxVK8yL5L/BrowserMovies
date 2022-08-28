package com.github.browsermovies.m_detail

import android.os.Bundle
import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.github.browsermovies.R
import androidx.leanback.app.VerticalGridFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.VerticalGridPresenter
import com.bumptech.glide.Glide
import com.github.browsermovies.BrowserActivity
import com.github.browsermovies.libs.ApiService
import com.github.browsermovies.libs.HistoryManager
import com.github.browsermovies.libs.SSLSocketFactoryCompat
import com.github.browsermovies.models.CardRow
import com.github.browsermovies.models.History
import com.github.browsermovies.models.Movie
import com.github.browsermovies.models.Site
import com.github.browsermovies.presenters.TextPresenter
import com.github.browsermovies.utils.FetchBrowser
import com.github.browsermovies.utils.LogUtil
import com.github.browsermovies.utils.WebFetcher
import com.github.browsermovies.utils.XwalkBrowserFetcher
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Entities
import java.lang.Exception
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 */
class MovieDetailFragment : VerticalGridFragment() {

    private var mAdapter: ArrayObjectAdapter? = null
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL
    private lateinit var processBar: ProgressBar
    private var mSelectedMovie: Movie? = null
    private lateinit var thumbImageView: ImageView
    private lateinit var sysConfig: ApiService
    private var useragent:String = ""
    private lateinit var fetchBrowser: XwalkBrowserFetcher
    //private lateinit var fetchBrowser: FetchBrowser
    private lateinit var defaultFetcher: WebFetcher
    private lateinit var history: History
    private var mSelectedSite: Site? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sysConfig = ApiService()
        mSelectedMovie = activity.intent.getSerializableExtra("movie") as Movie
        title = mSelectedMovie!!.title
        this.processBar = activity!!.findViewById<ProgressBar>(R.id.detial_progressbar)
        this.thumbImageView = activity!!.findViewById(R.id.thumb)
        thumbImageView.isSelected = false
        thumbImageView.isFocusable = false
        Glide.with(activity)
            .load(mSelectedMovie!!.cardImageUrl)
            .into(thumbImageView)

        mSelectedSite = activity.intent.getSerializableExtra("site") as Site
        useragent = mSelectedSite!!.detail!!.useragent!!
        setupRowAdapter()

        onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            val card = item as Movie
            Toast.makeText(activity,"开始爬取视频播放信息", Toast.LENGTH_SHORT).show()
            Toast.makeText(activity,card.videoUrl,Toast.LENGTH_LONG).show()
            processBar.setVisibility(View.VISIBLE)
            history = History()
            history.category = mSelectedSite!!.name
            history.playindex = mAdapter!!.indexOf(card)
            history.playlink = card.videoUrl
            history.title = card.title
            history.playMovie = mSelectedMovie
            history.playSite = mSelectedSite

            if(mSelectedSite!!.openBrowser == "yes"){
                var intent = Intent(activity,BrowserActivity::class.java)
                intent.putExtra("title",mSelectedMovie!!.title)
                intent.putExtra("history",history)
                intent.putExtra("movie",mSelectedMovie)
                intent.putExtra("site",mSelectedSite)
                intent.putExtra("loadUrl",card.videoUrl)
                startActivity(intent)
            }else{
                if(this.mSelectedSite!!.detail!!.onclick=="yes"){
                    val jscmd = "document.querySelectorAll('"+mSelectedSite!!.detail!!.clickcontainer!!.selector+"')["+mAdapter!!.indexOf(card)+"].click()"
                    //Log.d("jscmdis",jscmd)
                    fetchBrowser.fetchHtmlAndClickFromUrl(mSelectedMovie?.videoUrl!!,this.mSelectedSite!!.detail!!.useragent!!,jscmd,{result->handlePlayResult(result)})
                }else {
                    getPlayUrl(card.videoUrl as String)
                }
            }
        }
    }

    private fun setupRowAdapter() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        var showNum = 8
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val ns = prefs.getString("prefs_sourcesnum_key", "c8")!!.replace("c","")
        if (ns!=null){
            showNum = ns.toInt()
        }
        gridPresenter.numberOfColumns = showNum
        setGridPresenter(gridPresenter)
        val cardPresenterSelector = TextPresenter()
        mAdapter = ArrayObjectAdapter(cardPresenterSelector)
        adapter = mAdapter

        //fetchBrowser = FetchBrowser(activity)
        fetchBrowser = XwalkBrowserFetcher(activity)
        defaultFetcher = WebFetcher()
        fetchBrowser.inwords = mSelectedSite!!.inword!!
        fetchBrowser.uninwords = mSelectedSite!!.uninword!!
        fetchSources()
    }

    private fun getPlayUrl(url:String){
        processBar.setVisibility(View.VISIBLE);
        try {
            if(mSelectedSite!!.detail!!.play=="yes"){
                Toast.makeText(activity,url, Toast.LENGTH_SHORT).show()
                processBar.setVisibility(View.GONE)
                val historyManager = HistoryManager(activity)
                historyManager.setHistory(history)
                historyManager.saveWithTime(0)
                var intent = Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                startActivity(intent)
            }else{

                if(mSelectedSite!!.play!!.type=="default"){
                    fetchBrowser.fetchResourceFromUrl(url,this.mSelectedSite!!.play!!.useragent!!,{result->handlePlayResult(result)})
                }else{
                    defaultFetcher.fetchHtmlFromUrl(url!!,this.mSelectedSite!!.play!!.useragent!!,{result->handleHtmlPlayResult(result)})
                }

            }
        }catch (e:Exception){
            Toast.makeText(activity,"获取播放信息失败，请检查配置",Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleHtmlPlayResult(result:String){
        //Log.d("playurlis",result)
        processBar.setVisibility(View.GONE)
        try {
            var playurl = result
            if(!mSelectedSite!!.play!!.filter.isNullOrEmpty()){
                var partn = mSelectedSite!!.play!!.filter
                val findgroup = Pattern.compile(partn).matcher(playurl)
                while (findgroup.find()){
                    playurl = findgroup.group(1)
                    break
                }
                //playurl = playurl.unescapeUrl()
                playurl = URLDecoder.decode(playurl).replace("\\","")
                //Log.d("playurlis",playurl)
            }
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
        }catch (e:java.lang.Exception){
            Toast.makeText(activity,"无法解析视频地址，请稍后再试或检查是否安装播放器", Toast.LENGTH_SHORT).show()
        }

    }

    private fun handlePlayResult(result:String){
        //Log.d("playurlis",result)
        processBar.setVisibility(View.GONE)
        try {
            var playurl = result
            if(!mSelectedSite!!.play!!.filter.isNullOrEmpty()){
                var partn = mSelectedSite!!.play!!.filter
                val findgroup = Pattern.compile(partn).matcher(playurl)
                while (findgroup.find()){
                    playurl = findgroup.group(1)
                    break
                }
                //playurl = playurl.unescapeUrl()
                playurl = URLDecoder.decode(playurl).replace("\\","")
                //Log.d("playurlis",playurl)
            }
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
        }catch (e:java.lang.Exception){
            Toast.makeText(activity,"无法解析视频地址，请稍后再试或检查是否安装播放器", Toast.LENGTH_SHORT).show()
        }

    }


    private fun fetchSources() {
        processBar.setVisibility(View.VISIBLE);
        val fetchVideoUrl = mSelectedMovie?.videoUrl
        try {
            if(this.mSelectedSite!!.detail!!.fetchtype=="default"){
                defaultFetcher.fetchHtmlFromUrl(fetchVideoUrl!!,useragent,{result->handlePageResult(result)})
            }else{
                fetchBrowser.fetchHtmlFromUrl(fetchVideoUrl!!,useragent, { result -> handlePageResult(result) })
            }
        }catch (e: Exception){
            Toast.makeText(activity,"未找到播放源，请稍后重试", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePageResult(result:String){
        //LogUtil().loge("sourceresultis",result)
        processBar.setVisibility(View.GONE);
        try {
            var doc = Jsoup.parse(result)
            var obj = JSONObject()
            var array = JSONArray()
            var detailselector = mSelectedSite!!.detail!!
            val videos = doc.select(detailselector.videoscontainer!!.selector)
            for (video in videos){
                var videotitle = ""
                var linkhref = ""

                var titleinfo = video
                if(!detailselector.title!!.selector.isNullOrEmpty()){
                    titleinfo = video.select(detailselector!!.title!!.selector)[0]
                }
                if (detailselector!!.title!!.attrName=="text"){
                    videotitle = titleinfo.text()
                }else{
                    videotitle = titleinfo.attr(detailselector!!.title!!.attrName)
                }

                if(!detailselector.title!!.filter.isNullOrEmpty()){
                    var partn = detailselector.title!!.filter
                    val findgroup = Pattern.compile(partn).matcher(videotitle)
                    while (findgroup.find()){
                        videotitle = findgroup.group(1)
                        break
                    }
                }


                var linkinfo = video
                if(!detailselector.link!!.selector.isNullOrEmpty()){
                    linkinfo = video.select(detailselector!!.link!!.selector)[0]
                }
                var templink = ""
                if (detailselector.link!!.attrName=="text"){
                    templink = linkinfo.text()
                }else{
                    templink = linkinfo.attr(detailselector!!.link!!.attrName)
                }

                if(!detailselector.link!!.filter.isNullOrEmpty()){
                    var partn = detailselector.link!!.filter
                    val findgroup = Pattern.compile(partn).matcher(templink)
                    while (findgroup.find()){
                        templink = findgroup.group(1)
                        break
                    }
                }


                if(!templink.isNullOrEmpty()){
                    if (templink.substring(0,4)!="http"){
                        templink = mSelectedSite!!.link+templink
                    }
                    linkhref = templink
                }

                var item = JSONObject()
                //item.put("type", "Thumb_GRID")
                item.put("title", videotitle)
                item.put("description", videotitle)
                item.put("cardImageUrl", mSelectedMovie!!.cardImageUrl)
                item.put("backgroundImageUrl", mSelectedMovie!!.cardImageUrl)
                item.put("videoUrl", linkhref)
                array.put(item)

            }
            obj.put("movies", array)
            val json = obj.toString()
            val cardRow = Gson().fromJson(json, CardRow::class.java)
            mAdapter!!.addAll(mAdapter!!.size(), cardRow.movies!!)
            var position = 0
            if(activity.intent.hasExtra("playindex")){
                //查看有没有指定的key
                position = activity.intent.getIntExtra("playindex",0).toInt()
            }
            setSelectedPosition(position)
        }catch (e:java.lang.Exception){
            Toast.makeText(activity,"无法解析视频地址，请稍后再试", Toast.LENGTH_SHORT).show()
        }
    }


    fun removeUTFCharacters(data: String): String{
        val p = Pattern.compile("\\\\u(\\p{XDigit}{4})")
        val m = p.matcher(data)
        val buf = StringBuffer(data.length)
        while (m.find()) {
            val ch = Integer.parseInt(m.group(1), 16).toChar().toString()
            m.appendReplacement(buf, Matcher.quoteReplacement(ch))
        }
        m.appendTail(buf)
        return buf.toString().replace("\\\"", "\"");
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



}
