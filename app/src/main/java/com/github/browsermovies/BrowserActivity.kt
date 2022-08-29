package com.github.browsermovies

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import com.github.browsermovies.libs.ApiService
import com.github.browsermovies.libs.HistoryManager
import com.github.browsermovies.models.History
import com.github.browsermovies.models.Movie
import com.github.browsermovies.models.Site
import org.jsoup.nodes.Entities
import org.xwalk.core.XWalkActivity
import org.xwalk.core.XWalkResourceClient
import org.xwalk.core.XWalkView
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

class BrowserActivity : XWalkActivity() {
    private lateinit var cordovaWebView: XWalkView
    private lateinit var history: History
    private var mSelectedSite: Site? = null
    private var mSelectedMovie: Movie? = null
    private lateinit var sysConfig: ApiService
    //private var useragent:String = ""
    private var loadUrl:String = ""
    var inwords = "m3u8,mp4"
    var uninwords = ""
    var hasHandle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        //webview = findViewById(R.id.xwalkWebView)
        sysConfig = ApiService()
        cordovaWebView = findViewById<View>(R.id.xwalkWebView) as XWalkView
        mSelectedMovie = intent.getSerializableExtra("movie") as Movie
        mSelectedSite = intent.getSerializableExtra("site") as Site
        mSelectedSite = intent.getSerializableExtra("site") as Site
        loadUrl = intent.getStringExtra("loadUrl")
        history = intent.getSerializableExtra("history") as History
        inwords = mSelectedSite!!.inword!!
        uninwords = mSelectedSite!!.uninword!!
        //webview.load("https://www.zxzj.me/video/2767-1-1.html",null)

    }
    override fun onXWalkReady() {
        cordovaWebView.load(loadUrl,null)
        cordovaWebView.settings.javaScriptEnabled = true

        var useragent = sysConfig.DefaultUserAgent()
        if(mSelectedSite!!.play!!.useragent!! == "mobile"){
            useragent = sysConfig.MobileUserAgent()
        }

        cordovaWebView.settings.userAgentString = useragent
        cordovaWebView.setResourceClient(object : XWalkResourceClient(cordovaWebView){
            override fun onLoadFinished(view: XWalkView?, url: String?) {
                super.onLoadFinished(view, url)
//                view!!.evaluateJavascript("document.getElementsByTagName('video')[0].requestFullscreen()",{ value ->
//                    // Execute onReceiveValue's code
//                    Log.d("javascriptis",value)
//                })
                //val jscmd = "function openFullscreen() {var elem = document.getElementsByTagName('video')[0]; if (elem.requestFullscreen) { elem.requestFullscreen(); } else if (elem.webkitRequestFullscreen) { elem.webkitRequestFullscreen(); } else if (elem.msRequestFullscreen) {  elem.msRequestFullscreen();} elem.play();} openFullscreen();
                val jscmd = 'let button = document.createElement("button");button.innerText="全屏";button.onclick = function(){var elem = document.getElementsByTagName("video")[0]; if (elem.requestFullscreen) { elem.requestFullscreen(); } else if (elem.webkitRequestFullscreen) {  elem.webkitRequestFullscreen(); } else if (elem.msRequestFullscreen) {  elem.msRequestFullscreen(); } elem.play();};button.setAttribute("id", "myvideo");button.style.cssText = \'position:absolute;top:0px;left:0px;width:100%;height:50px;z-index:9999\';document.body.append(button);button.focus();'
                view!!.evaluateJavascript(jscmd,{ value ->
                    Log.d("javascriptis",value)
                })
            }

            override fun onLoadStarted(view: XWalkView?, url: String?) {
                if(hasInWord(url!!)&&!hasUninWord(url!!)){
                    if (!hasHandle) {
                        hasHandle = true
                        jumpto(url)
                    } else {
                        view!!.stopLoading()
                        return
                    }
                }else{
                    super.onLoadStarted(view, url)
                }
            }


        })
    }

    private fun jumpto(url:String){
        try {
            var playurl = url
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
            val historyManager = HistoryManager(this)
            historyManager.setHistory(history)
            historyManager.saveWithTime(0)
            var intent = Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(playurl), "video/*");
            startActivity(intent)
        }catch (e:Exception){
            Toast.makeText(this,"无法解析视频地址，请稍后再试", Toast.LENGTH_SHORT).show()
        }

    }

    fun hasInWord(url:String):Boolean{
        var filter_words = inwords.split(",")
        for (filter_string in filter_words){
            if(!filter_string.isNullOrEmpty()){
                if(url.contains(filter_string,ignoreCase = true)){
                    return true
                }
            }
        }
        return false
    }


    fun hasUninWord(url:String):Boolean{
        var filter_words = uninwords.split(",")
        for (filter_string in filter_words){
            if(!filter_string.isNullOrEmpty()){
                if(url.contains(filter_string,ignoreCase = true)){
                    return true
                }
            }
        }
        return false
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
        cordovaWebView.onDestroy()
        super.onDestroy()
    }



}
