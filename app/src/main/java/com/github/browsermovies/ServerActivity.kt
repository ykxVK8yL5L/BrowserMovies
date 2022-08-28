package com.github.browsermovies


import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.util.Log
import com.github.browsermovies.libs.ApiService
import com.github.browsermovies.libs.MainApplication
import com.github.browsermovies.models.Site
import com.github.browsermovies.utils.NetUtils
import com.google.gson.Gson
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import kotlinx.android.synthetic.main.activity_server.*
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.concurrent.TimeUnit


class ServerActivity : Activity() {

    private lateinit var server:Server
    private lateinit var srctext:TextView
    private lateinit var websrc:TextView
    private lateinit var srcBtn:Button
    private lateinit var textBtn:Button
    private lateinit var textEdit:EditText
    var sysconfig = ApiService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)

        srctext = findViewById(R.id.serverurl)
        srcBtn = findViewById(R.id.fetch)
        websrc = findViewById(R.id.websrc)

        textBtn = findViewById(R.id.fetchtext)
        textEdit = findViewById(R.id.webtext)

        srcBtn.setOnClickListener(View.OnClickListener {
            try {
                var url = websrc.text.toString()
                object : AsyncTask<Void, Void, String>() {
                    override fun onPostExecute(result: String) {
                        //super.onPostExecute(result)
                        handleResult(result)
                    }
                    override fun doInBackground(vararg p0: Void?): String? {
                        var doc = Jsoup.connect(url).followRedirects(true).sslSocketFactory(sysconfig.DefaultSSLSocketFactory()).userAgent(sysconfig.MobileUserAgent()).get()
                        return doc.text()
                    }
                }.execute()

            }catch (e:Exception){
                Toast.makeText(this,"更新网站出错，请检查", Toast.LENGTH_LONG).show()
            }

        })


        textBtn.setOnClickListener(View.OnClickListener {
            try {
                try {
                    var updatetext = webtext.text.toString()
                    var file = File(MainApplication.getRootFile(),"sites.json")
                     Log.d("server write filepathis",MainApplication.getRootFile().absolutePath)
                    if (file.exists() && file.canWrite()) {
                        val fOut = FileOutputStream(file)
                        val outputStreamWriter = OutputStreamWriter(fOut)
                        outputStreamWriter.write(updatetext)
                        outputStreamWriter.close()
                    } else {
                        val isFileCreated = file.createNewFile()
                        if (isFileCreated) {
                            val fOut = FileOutputStream(file)
                            val outputStreamWriter = OutputStreamWriter(fOut)
                            outputStreamWriter.write(updatetext)
                            outputStreamWriter.close()
                        }
                    }
                    Toast.makeText(this,"导入成功，请返回主页面重新加载", Toast.LENGTH_LONG).show()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(this,"文件写入失败，请检查权限", Toast.LENGTH_LONG).show()
                }

            }catch (e:Exception){
                Toast.makeText(this,"更新网站出错，请检查", Toast.LENGTH_LONG).show()
            }

        })





        server = AndServer.webServer(this)
            .port(9863)
            .listener(object : Server.ServerListener {
                override fun onException(e: Exception?) {
                }

                override fun onStarted() {
                    srctext.text="TV端请在浏览器打开http:/"+NetUtils.getLocalIPAddress()+":"+server.port
                }

                override fun onStopped() {

                }
            })
            .timeout(10, TimeUnit.SECONDS)
            .build()
        server.startup()

    }


    fun handleResult(result: String){
        try{
            try{
                val sites:List<Site> = Gson().fromJson<Array<Site>>(result, Array<Site>::class.java).toMutableList()
                try {
                    var file = File(MainApplication.getRootFile(),"sites.json")
                    if(file.exists()){
                        file.writeText(result)
                    }else{
                        val isFileCreated :Boolean = file.createNewFile()
                        if(isFileCreated){
                            file.writeText(result)
                        }
                    }

                }catch (e: java.lang.Exception){
                    Toast.makeText(this,"文件写入失败，请检查权限", Toast.LENGTH_LONG).show()
                }
            }catch (e: java.lang.Exception){
                Toast.makeText(this,"json格式不正确，请检查", Toast.LENGTH_LONG).show()
            }
            Toast.makeText(this,"更新完成，请重新加载", Toast.LENGTH_LONG).show()
        }catch (e:java.lang.Exception){
            Toast.makeText(this,"更新出错请检查网站源", Toast.LENGTH_LONG).show()
        }


    }

    override fun onDestroy() {
        server.shutdown()
        super.onDestroy()
    }



}


