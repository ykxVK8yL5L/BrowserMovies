package com.github.browsermovies.libs;


import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.github.browsermovies.utils.SitesServer;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

@RestController
public class ServerController {
    @PostMapping("/update")
    String update(@RequestParam("websrc") String websrc) {
        SitesServer sites = new SitesServer();
        return sites.fetchSitesFromUrl(websrc);
    }


    @PostMapping("/updatetext")
    String updateText(@RequestParam("updatetext") String updatetext) {

        try {
            File file = new File(MainApplication.getRootFile(),"sites.json");
            if(file.exists() && file.canWrite()){
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
                outputStreamWriter.write(updatetext);
                outputStreamWriter.close();
            }else{
                Boolean isFileCreated = file.createNewFile();
                if(isFileCreated){
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
                    outputStreamWriter.write(updatetext);
                    outputStreamWriter.close();
                }
            }
            return "导入成功，请返回主页面重新加载\n"+updatetext;
        }catch (Exception e){
            e.printStackTrace();
            return "文件写入失败，请检查权限";
        }




    }



    @GetMapping(value = "/",produces = MediaType.TEXT_HTML_VALUE)
    String index() {
        String html = "<html><head><title>网站配置页面</title><style>\n" +
                "input[type=text], select , textarea {\n" +
                "  width: 100%;\n" +
                "  padding: 12px 20px;\n" +
                "  margin: 8px 0;\n" +
                "  display: inline-block;\n" +
                "  border: 1px solid #ccc;\n" +
                "  border-radius: 4px;\n" +
                "  box-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                "input[type=submit] {\n" +
                "  width: 100%;\n" +
                "  background-color: #4CAF50;\n" +
                "  color: white;\n" +
                "  padding: 14px 20px;\n" +
                "  margin: 8px 0;\n" +
                "  border: none;\n" +
                "  border-radius: 4px;\n" +
                "  cursor: pointer;\n" +
                "}\n" +
                "\n" +
                "input[type=submit]:hover {\n" +
                "  background-color: #45a049;\n" +
                "}\n" +
                "\n" +
                "div {\n" +
                "  border-radius: 5px;\n" +
                "  background-color: #f2f2f2;\n" +
                "  padding: 20px;\n" +
                "}\n" +
                "</style></head><body><div><p>两种方法任选一种。第一种是把json代码写入远程网址更新（推荐）示例地址：https://netcut.cn/p/b24862eb235ec404<br/>第二种方法把json代码直接写入文本框（如果第一种方法不行就第二种）<br/>如果两种都不行就卸载吧，这个APP可能跟你无缘！</p><p><form action='/update' method='post'><label for=\"websrc\">方法一、网站源地址</label><input type=\"text\" name='websrc' placeholder='请输入网站源地址'></input><input type='submit' value='更新'/></form><form action='/updatetext' method='post'><label for=\"updatetext\">方法二、网站源代码</label><textarea name='updatetext' placeholder='请输入网站源代码'></textarea><input type='submit' value='手动更新'/></form></p></div></body></html>";
        return html;
    }
}