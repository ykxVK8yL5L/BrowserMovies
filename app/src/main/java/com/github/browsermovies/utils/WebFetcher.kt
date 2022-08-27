package com.github.browsermovies.utils

import android.os.AsyncTask
import com.github.browsermovies.libs.ApiService
import org.jsoup.Jsoup

class WebFetcher {
    public var sysconfig = ApiService()

    public fun WebFetcher(){

    }

    /**
     * 两个方法调用fetchHtmlFromUrl
     * 命名参数:fetchHtmlFromUrl("http://www.baidu.com","default",callback = ::handleResult)
     *位置参数:fetchHtmlFromUrl("http://www.baidu.com","default",{result->handleResult(result)})
     * **/

    public fun fetchHtmlFromUrl(url:String,type:String="default",callback:(String)->Unit){
        var useragent = sysconfig.DefaultUserAgent()
        if(type=="mobile"){
            useragent = sysconfig.MobileUserAgent()
        }
        object : AsyncTask<Void, Void, String>() {
            override fun onPostExecute(result: String) {
                //super.onPostExecute(result)
                callback(result)
            }
            override fun doInBackground(vararg p0: Void?): String? {
                var doc = Jsoup.connect(url).followRedirects(true).sslSocketFactory(sysconfig.DefaultSSLSocketFactory()).userAgent(useragent).get()
                return doc.body().html()
            }
        }.execute()

    }


}