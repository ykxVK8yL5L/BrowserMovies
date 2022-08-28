package com.github.browsermovies.utils

import android.content.Context
import android.net.http.SslError
import android.util.Log
import android.view.View
import android.webkit.ValueCallback
import android.widget.Toast
import com.github.browsermovies.libs.ApiService
import org.xwalk.core.XWalkResourceClient
import org.xwalk.core.XWalkView
import org.xwalk.core.XWalkWebResourceRequest
import org.xwalk.core.XWalkWebResourceResponse
import java.io.InputStream
import java.util.regex.Matcher
import java.util.regex.Pattern


class XwalkBrowserFetcher constructor(context: Context){
    var sysconfig = ApiService()
    var webview:XWalkView = XWalkView(context)
    var context:Context = context
    var inwords = "m3u8,mp4"
    var uninwords = ""
    var hasHandle = false
    init {
        webview!!.visibility = View.GONE
        webview!!.getSettings().setJavaScriptEnabled(true)
        webview!!.getSettings().setJavaScriptCanOpenWindowsAutomatically(true)
    }

    /**
     * 两个方法调用fetchHtmlFromUrl
     * 命名参数:fetchHtmlFromUrl("http://www.baidu.com","default",cmd,callback = ::handleResult)
     *位置参数:fetchHtmlFromUrl("http://www.baidu.com","default",cmd,{result->handleResult(result)})
     * **/


    fun fetchHtmlFromUrl(url:String,type:String="default",callback:(String)->Unit){
        var useragent = sysconfig.DefaultUserAgent()
        if(type=="mobile"){
            useragent = sysconfig.MobileUserAgent()
        }
        webview!!.getSettings().setUserAgentString(useragent)
        webview.setResourceClient(object : XWalkResourceClient(webview){
             override fun onLoadFinished(view: XWalkView?, url: String?) {
                super.onLoadFinished(view, url)
                val cmd = "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"
                view!!.evaluateJavascript(cmd,
                    object: ValueCallback<String> {
                        override fun onReceiveValue(value: String?) {
                            if (value.isNullOrEmpty()){
                                Toast.makeText(context,"无法获取结果请稍后重试", Toast.LENGTH_SHORT).show()
                            }else{
                                if (value.isNullOrEmpty()){
                                    Toast.makeText(context,"无法获取结果请稍后重试", Toast.LENGTH_SHORT).show()
                                }else{
                                    var result =  removeUTFCharacters(value!!)
                                    callback(result)
                                }

                            }
                        }
                    })
            }

        })
        webview.loadUrl(url)
    }


    fun fetchHtmlAndClickFromUrl(url:String,type:String="default",jscmd:String,clickCallback:(String)->Unit){
        var useragent = sysconfig.DefaultUserAgent()
        if(type=="mobile"){
            useragent = sysconfig.MobileUserAgent()
        }
        hasHandle = false
        webview!!.getSettings().setUserAgentString(useragent)
        webview.setResourceClient(object : XWalkResourceClient(webview){
            override fun onLoadFinished(view: XWalkView?, url: String?) {
                super.onLoadFinished(view, url)
                view!!.evaluateJavascript(jscmd,object: ValueCallback<String> {
                        override fun onReceiveValue(value: String?) {

                        }
                    })
                view!!.setResourceClient(object :XWalkResourceClient(view){
                    override fun onReceivedSslError(view: XWalkView?, callback: ValueCallback<Boolean>?,error: SslError?) {
                        //super.onReceivedSslError(view, callback, error)
                        callback!!.onReceiveValue(true)
                    }
                    override fun onLoadStarted(view: XWalkView?, url: String?) {
                        //Log.d("loadclickresourcesis",url)
                        if(hasInWord(url!!)&&!hasUninWord(url!!)){
                            if (!hasHandle) {
                                hasHandle = true
                                clickCallback(url)
                                view!!.stopLoading()
                                view!!.loadUrl("blank")
                                return
                            } else {
                                view!!.stopLoading()
                                view!!.loadUrl("blank")
                                return
                            }
                            view!!.stopLoading()
                            view!!.loadUrl("blank")
                            return
                        }else{
                            super.onLoadStarted(view, url)
                        }
                    }
                })
            }

        })
        webview.loadUrl(url)
    }



    fun fetchResourceFromUrl(url:String,type:String="default",callback:(String)->Unit){
        webview.clearCache(true);
        var useragent = sysconfig.DefaultUserAgent()
        if(type=="mobile"){
            useragent = sysconfig.MobileUserAgent()
        }
        hasHandle = false


        webview!!.getSettings().setUserAgentString(useragent)
        webview.setResourceClient(object : XWalkResourceClient(webview){

            override fun onReceivedSslError(
                view: XWalkView?,
                callback: ValueCallback<Boolean>?,
                error: SslError?
            ) {
                //super.onReceivedSslError(view, callback, error)
                callback!!.onReceiveValue(true)
            }

            override fun onReceivedResponseHeaders(view: XWalkView?,request: XWalkWebResourceRequest?,response: XWalkWebResourceResponse?) {
                var url = request!!.url.toString()
                //Log.d("loadresourcesis",url)
                if(hasInWord(url!!)&&!hasUninWord(url!!)){
                    if (!hasHandle) {
                        hasHandle = true
                        callback(url)
                        view!!.stopLoading()
                        view!!.loadUrl("blank")
                        return
                    } else {
                        view!!.stopLoading()
                        view!!.loadUrl("blank")
                        return
                    }
                    view!!.stopLoading()
                    view!!.loadUrl("blank")
                    return
                }else{
                    super.onReceivedResponseHeaders(view, request, response)
                }
            }


            override fun onLoadStarted(view: XWalkView?, url: String?) {
                //Log.d("loadresourcesis",url)
                if(hasInWord(url!!)&&!hasUninWord(url!!)){
                    if (!hasHandle) {
                        hasHandle = true
                        callback(url)
                        view!!.stopLoading()
                        view!!.loadUrl("blank")
                        return
                    } else {
                        view!!.stopLoading()
                        view!!.loadUrl("blank")
                        return
                    }
                    view!!.stopLoading()
                    view!!.loadUrl("blank")
                    return
                }else{
                    super.onLoadStarted(view, url)
                }
            }

        })
        webview.loadUrl(url)
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

    public fun destory(){
        this.webview.onDestroy()
    }

}