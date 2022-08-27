package com.github.browsermovies.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.github.browsermovies.libs.ApiService
import java.util.regex.Matcher
import java.util.regex.Pattern


class FetchBrowser constructor(context: Context){
    var sysconfig = ApiService()
    var webview:WebView = WebView(context)
    var context:Context = context
    var inwords = "m3u8,mp4"
    var uninwords = ""

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


    fun fetchHtmlFromUrl(url:String,type:String="default",callback:(String)->Unit){
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
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
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
                if(hasInWord(url!!)&&!hasUninWord(url!!)){
                    if (!hasHandle) {
                        hasHandle = true
                        callback(url)
                    } else {
                        view!!.stopLoading()
                        return
                    }
                }else{
                    super.onLoadResource(view, url)
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
        this.webview.destroy()
    }

}

