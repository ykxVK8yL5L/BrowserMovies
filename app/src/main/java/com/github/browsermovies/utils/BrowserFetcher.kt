package com.github.browsermovies.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.github.browsermovies.libs.ApiService
import java.util.regex.Matcher
import java.util.regex.Pattern


class BrowserFetcher constructor(context: Context){
    var sysconfig = ApiService()
    var webview:WebView = WebView(context)
    var context:Context = context
    var filter_words = arrayOf("anna.run")
    var videolist = arrayListOf<String>()

    init {
        webview!!.visibility = View.GONE
        webview!!.getSettings().setJavaScriptEnabled(true)
        webview!!.getSettings().setJavaScriptCanOpenWindowsAutomatically(true)
        webview!!.setWebChromeClient(WebChromeClient())
        webview!!.getSettings().setDomStorageEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview!!.getSettings().mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webview.clearCache(true)
    }

    /**
     * 两个方法调用fetchHtmlFromUrl
     * 命名参数:fetchHtmlFromUrl("http://www.baidu.com","default",cmd,callback = ::handleResult)
     *位置参数:fetchHtmlFromUrl("http://www.baidu.com","default",cmd,{result->handleResult(result)})
     * **/


    fun fetchHtmlFromUrlWithRedirect(url:String,type:String="default",callback:(String)->Unit){
        var useragent = sysconfig.DefaultUserAgent()
        if(type=="mobile"){
            useragent = sysconfig.MobileUserAgent()
        }
        webview!!.getSettings().setUserAgentString(useragent)
        CookieManager.getInstance().removeAllCookie();
        webview!!.setWebViewClient(object: WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                val cmd = "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"
                webview!!.evaluateJavascript(cmd,
                    object: ValueCallback<String> {
                        override fun onReceiveValue(value: String?) {
                            if (value.isNullOrEmpty()){
                                Toast.makeText(context,"无法获取结果请稍后重试", Toast.LENGTH_SHORT).show()
                            }else{
                                var result =  removeUTFCharacters(value!!)
                                var redirurl=""
                                val partn = "window.location.href =\\\"([^\\\"]*)\\\""
                                val findgroup = Pattern.compile(partn).matcher(result)
                                while (findgroup.find()){
                                    redirurl=findgroup.group(1)
                                    break
                                }
                                //LogUtil().loge("pageresult",result)
                                if(redirurl.isNullOrEmpty()){
                                    callback(result)
                                    return
                                }

                                if (redirurl.substring(0,4)!="http"){
                                    if (redirurl.substring(0,2)!="//"){
                                        redirurl = redirurl.substring(1)
                                    }else{
                                        redirurl = redirurl.substring(2)
                                    }
                                    val uri: Uri = Uri.parse(url)
                                    var host = uri.getHost()
                                    redirurl = "http://"+host+"/"+redirurl
                                    callback(redirurl)
                                    return
                                }else{
                                    callback(redirurl)
                                    return
                                }

                            }
                        }
                    })
            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view!!.loadUrl(url)
                return true
            }

        })
        webview.loadUrl(url)
    }

    fun fetchHtmlFromUrl(url:String,type:String="default",callback:(String)->Unit){
        var useragent = sysconfig.DefaultUserAgent()
        if(type=="mobile"){
            useragent = sysconfig.MobileUserAgent()
        }
        webview!!.getSettings().setUserAgentString(useragent)
        webview!!.setWebViewClient(object: WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                val cmd = "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"
                webview!!.evaluateJavascript(cmd,
                    object: ValueCallback<String> {
                        override fun onReceiveValue(value: String?) {
                            if (value.isNullOrEmpty()){
                                Toast.makeText(context,"无法获取结果请稍后重试", Toast.LENGTH_SHORT).show()
                            }else{
                                callback(removeUTFCharacters(value))
                            }
                        }
                    })
            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view!!.loadUrl(url)
                return true
            }
        })
        webview.loadUrl(url)
    }
    fun fetchHtmlFromUrl(url:String,type:String="default",cmd:String,callback:(String)->Unit){
        var useragent = sysconfig.DefaultUserAgent()
        if(type=="mobile"){
            useragent = sysconfig.MobileUserAgent()
        }
        webview!!.getSettings().setUserAgentString(useragent)
        webview!!.setWebViewClient(object: WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                webview!!.evaluateJavascript(cmd,
                    object: ValueCallback<String> {
                        override fun onReceiveValue(value: String?) {
                            if (value.isNullOrEmpty()){
                                Toast.makeText(context,"无法获取结果请稍后重试", Toast.LENGTH_SHORT).show()
                            }else{
                                callback(removeUTFCharacters(value))
                            }
                        }
                    })
            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view!!.loadUrl(url)
                return true
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                //Log.d("loarderesuourceis",url)
                super.onLoadResource(view, url)
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
        var hasHandle = false
        webview!!.getSettings().setUserAgentString(useragent)
        webview!!.setWebViewClient(object: WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view!!.loadUrl(url)
                return true
            }
            override fun onLoadResource(view: WebView?, url: String?) {
                //Log.d("loadresourceis",url)
                if(url!!.contains(".mp4", ignoreCase = true) || url!!.contains(".m3u8", ignoreCase = true) || url!!.contains(".m3u", ignoreCase = true)){
                    if(hasFilterWord(url)){
                        super.onLoadResource(view, url)
                    }else{
                        if(!hasHandle){
                            hasHandle = true
                            callback(url)
                        }else {
                            view!!.stopLoading()
                            return
                        }
                    }
                }else{
                    super.onLoadResource(view, url)
                }
                super.onLoadResource(view, url)
            }

        })
        webview.loadUrl(url)
    }
    fun fetchBdeResourceFromUrl(url:String,type:String="mobile",callback:(String)->Unit){
        var useragent = sysconfig.DefaultUserAgent()
        if(type=="mobile"){
            useragent = sysconfig.MobileUserAgent()
        }
        var hasHandle = false
        webview!!.getSettings().setUserAgentString(useragent)
        webview!!.setWebViewClient(object: WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view!!.loadUrl(url)
                return true
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                view!!.evaluateJavascript("'http://bde4.com/god/'+ptoken+'?sg='+sg",
                    object: ValueCallback<String> {
                        override fun onReceiveValue(value: String?) {
                            if (value.isNullOrEmpty()){
                                Toast.makeText(context,"无法获取结果请稍后重试", Toast.LENGTH_SHORT).show()
                            }else{
                                callback(value)
                            }
                        }
                    })
                super.onPageFinished(view, url)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
            }

        })
        webview.loadUrl(url)
    }
    fun hasFilterWord(url:String):Boolean{
        for (filter_string in filter_words){
            if(url.contains(filter_string)){
                return true
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
        this.webview.destroy()
    }

}

//if(url.contains("ftn.qq.com")){
//    val regex = "http\\:\\/\\/([^\\/]*)\\/".toRegex()
//    var vurl = regex.replace(url, "http://114.221.144.61/")
//    hasHandle = true
//    callback(vurl)
//}else{
//    Log.d("loadresource",url)
//    callback(url)
//}