package com.github.browsermovies

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.leanback.app.SearchFragment
import androidx.leanback.widget.*
import com.github.browsermovies.libs.*
import com.github.browsermovies.models.CardRow
import com.github.browsermovies.models.History
import com.github.browsermovies.models.Movie
import com.github.browsermovies.models.Site
import com.github.browsermovies.presenters.CardPresenter
import com.github.browsermovies.utils.FetchBrowser
import com.github.browsermovies.utils.LogUtil
import com.github.browsermovies.utils.WebFetcher
import com.github.browsermovies.utils.XwalkBrowserFetcher
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 */
class GlobalSearchFragment : MyGlobalSearchFragment() , SearchView.OnQueryTextListener, MyGlobalSearchFragment.SearchResultProvider,
    OnItemViewSelectedListener, OnItemViewClickedListener {

    lateinit var keyword:String
    var curentPage = 1
    lateinit var mRowsAdapter: ArrayObjectAdapter
    private lateinit var processBar: ProgressBar
    private lateinit var sysConfig: ApiService
    private lateinit var sslSocketFactory: SSLSocketFactoryCompat
    private lateinit var history: History
    private var useragent:String = ""
    private var mSelectedMovie: Movie? = null
    //private lateinit var mSelectedSite: Site
    private lateinit var fetchBrowser: XwalkBrowserFetcher
    private lateinit var defaultFetcher: WebFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.processBar = activity!!.findViewById<ProgressBar>(R.id.search_progressbar)
        mRowsAdapter = ArrayObjectAdapter(CardPresenter())
        sysConfig = ApiService()
        sslSocketFactory = SSLSocketFactoryCompat(sysConfig.provideTrustManager())

        fetchBrowser = XwalkBrowserFetcher(activity)
        defaultFetcher = WebFetcher()

        //LogUtil().loge("mselectedsite",mSelectedSite.toString())

        //useragent = mSelectedSite.list!!.useragent!!

        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        setOnItemViewSelectedListener(this)

//        if(mSelectedSite.search!!.pagerule!!.isNullOrEmpty()){
//            Toast.makeText(activity,"别搜索了这个站有点难为我了", Toast.LENGTH_LONG).show()
//        }


    }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?  ) {
        //Log.d("item clicked","clicked")
        val card = item as Movie
        if(mSelectedSite.next!="play"){
            val intent = Intent(activity,MovieDetailActivity::class.java)
            intent.putExtra("movie", card)
            intent.putExtra("category", mSelectedSite.name)
            intent.putExtra("site", mSelectedSite)
            startActivity(intent)
        }else{
            mSelectedMovie = card
            history = History()
            history.category = mSelectedSite!!.name
            history.playindex = 0
            history.playlink = card.videoUrl
            history.title = card.title
            history.playMovie = mSelectedMovie
            history.playSite = mSelectedSite
            getPlayUrl(card.videoUrl!!)
        }

    }


    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?  ) {
        if(item!=null){
            val card = item as Movie
            val itemIndex = mRowsAdapter!!.indexOf(card)
            if(mRowsAdapter.size()>=7){
                if ((mRowsAdapter!!.size()-itemIndex)<5) {
                    Toast.makeText(activity,"加载下一页数据", Toast.LENGTH_SHORT).show()
                    getQueryLink();
                }
            }

        }
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        mRowsAdapter.clear();
        keyword = query.toString()
        //Toast.makeText(activity, keyword, Toast.LENGTH_SHORT).show()

        if(mSelectedSite.search!!.pagerule!!.isNullOrEmpty()){
            Toast.makeText(activity,"别搜索了这个站有点难为我了", Toast.LENGTH_LONG).show()
        }else{
            getQueryLink()
        }
        return true
    }


    private fun getQueryLink(){
        processBar.setVisibility(View.VISIBLE);
        try {
            var searchUrl = mSelectedSite.search!!.pagerule!!
            if(!mSelectedSite.search!!.pageone.isNullOrEmpty() && this.curentPage==1){
                searchUrl =  mSelectedSite.search!!.pageone!!
            }
            searchUrl = searchUrl.replace("#mpageno",curentPage.toString()).replace("#msearchword",keyword)

            if(this.mSelectedSite!!.search!!.fetchtype=="default"){
                defaultFetcher.fetchHtmlFromUrl(searchUrl!!,useragent,{result->fetchSearchResult(result)})
            }else{
                fetchBrowser.fetchHtmlFromUrl(searchUrl,useragent,{result->fetchSearchResult(result)})
            }
            this.curentPage+=1

        }catch (e:Exception){
            Toast.makeText(activity,"别搜索了这个站有点难为我了", Toast.LENGTH_LONG).show()
        }

    }



    private fun fetchSearchResult(result:String){
        processBar.setVisibility(View.GONE);

        try {
            var doc = Jsoup.parse(result)
            var obj = JSONObject()
            var array = JSONArray()
            var searchselector = mSelectedSite.search!!
            val videos = doc.select(searchselector.videoscontainer!!.selector)
            for (video in videos){
                var videotitle = ""
                var thumbsrc = mSelectedSite.thumb
                var linkhref = ""

                var titleinfo = video
                if(!searchselector.title!!.selector.isNullOrEmpty()){
                    titleinfo = video.select(searchselector!!.title!!.selector)[0]
                }
                if (searchselector!!.title!!.attrName=="text"){
                    videotitle = titleinfo.text()
                }else{
                    videotitle = titleinfo.attr(searchselector!!.title!!.attrName)
                }

                if(!searchselector.title!!.filter.isNullOrEmpty()){
                    var partn = searchselector.title!!.filter
                    val findgroup = Pattern.compile(partn).matcher(videotitle)
                    while (findgroup.find()){
                        videotitle = findgroup.group(1)
                        break
                    }
                }

                var thumbinfo = video
                if(!searchselector.thumb!!.selector.isNullOrEmpty()){
                    thumbinfo = video.select(searchselector!!.thumb!!.selector)[0]
                }
                if (!searchselector.thumb!!.attrName.isNullOrEmpty()){
                    var thumburl = thumbinfo.attr(searchselector!!.thumb!!.attrName)

                    if(!searchselector.thumb!!.filter.isNullOrEmpty()){
                        var partn = searchselector.thumb!!.filter
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
                if(!searchselector.link!!.selector.isNullOrEmpty()){
                    linkinfo = video.select(searchselector!!.link!!.selector)[0]
                }
                var templink = ""
                if (searchselector.link!!.attrName=="text"){
                    templink = linkinfo.text()
                }else{
                    templink = linkinfo.attr(searchselector!!.link!!.attrName)
                }

                if(!searchselector.link!!.filter.isNullOrEmpty()){
                    var partn = searchselector.link!!.filter
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
            val searchCardRow = Gson().fromJson(json, CardRow::class.java)
            mRowsAdapter.addAll(mRowsAdapter.size(),searchCardRow.movies)
            this.curentPage+=1
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



        try {
            var playurl = result
            if(!mSelectedSite!!.play!!.filter.isNullOrEmpty()){
                var partn = mSelectedSite!!.play!!.filter
                val findgroup = Pattern.compile(partn).matcher(playurl)
                while (findgroup.find()){
                    playurl = findgroup.group(1)
                    break
                }
                playurl = playurl.unescapeUrl()
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
            Toast.makeText(activity,"无法解析视频地址，请稍后再试", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getResultsAdapter(): ArrayObjectAdapter {
        title = "搜索结果"
        return mRowsAdapter
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onDestroy() {
        fetchBrowser.destory()
        super.onDestroy()
    }

    companion object {

    }


    @PublishedApi
    internal fun String.unescapeUrl(): String {
        return URLDecoder.decode(this.replace("%u2026", "..."), Charset.forName("UTF-8").toString())
    }


}