package com.github.browsermovies.utils

import android.util.Log
import com.github.browsermovies.libs.ApiService
import com.github.browsermovies.libs.MainApplication
import com.github.browsermovies.models.Site
import com.google.gson.Gson
import org.jsoup.Jsoup
import java.io.File

class SitesServer{
    var sysconfig = ApiService()
    init {

    }
    fun fetchSitesFromUrl(url:String):String{
        try {
            var doc = Jsoup.connect(url).followRedirects(true).sslSocketFactory(sysconfig.DefaultSSLSocketFactory()).userAgent(sysconfig.MobileUserAgent()).get()
            //doc.text()
            try{
                val sites:List<Site> = Gson().fromJson<Array<Site>>(doc.text(), Array<Site>::class.java).toMutableList()
                try {
                    var file = File(MainApplication.getRootFile(),"sites.json")
                    if(file.exists()){
                        file.writeText(doc.text())
                    }else{
                        val isFileCreated :Boolean = file.createNewFile()
                        if(isFileCreated){
                            file.writeText(doc.text())
                        }
                    }
                }catch (e:Exception){

                    return "文件写入失败，请检查权限"
                }
            }catch (e:Exception){
                return "json格式不正确，请检查"
            }
            return "导入成功，请返回主页面更新数据\n"+doc.text()
        }catch (e:Exception){
            return "更新网站源出错请检查网址是否可用"
        }
    }
}

