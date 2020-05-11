package com.color.FileProcessing.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.color.FileProcessing.model.ResourceModel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DownloadFile {

    private String download_file_path = "/Users/yc/liuwei/svg/downloadSvg";

    //@PostConstruct
    public List<ResourceModel> getResource(){
        List<ResourceModel> resourceModelList = new ArrayList<>();
        File file = new File("/Users/yc/liuwei/svg/crawler.json");
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            InputStreamReader inputReader = new InputStreamReader(in);
            BufferedReader bf = new BufferedReader(inputReader);
            String str="";
            int j = 0;
            int i=3554;
            while ((str = bf.readLine()) != null) {
                if (null != str) {
                    String[] strs = str.split("},");
                    for(int a=3554;a<strs.length;a++){
                        String aa= strs[a];
                        if( a==0){
                            aa = aa.substring(1) +"}";
                        }else if( a== strs.length-1){
                            aa = aa.substring(0,aa.length()-1);
                        }else{
                            aa = aa+"}";
                        }
                        JSONObject jsonObject = JSON.parseObject(aa);
                        ResourceModel resourceModel = JSON.parseObject(jsonObject.toJSONString(), ResourceModel.class);
                        File fileDir = new File(download_file_path + "/svg_"+j+"_"+i);
                        fileDir.mkdir();
                        downloadNet(resourceModel.getConfig_link(), download_file_path + "/svg_"+j+"_"+i+"/aaa.json");
                        System.out.println("download:"+resourceModel.getConfig_link());
                        randomSleep();
                        downloadNet(resourceModel.getCover(), download_file_path + "/svg_"+j+"_"+i+"/aaa.jpg");
                        System.out.println("download:"+resourceModel.getCover());
                        randomSleep();
                        downloadNet(resourceModel.getLink(), download_file_path + "/svg_"+j+"_"+i+"/aaa.svg");
                        System.out.println("download:"+resourceModel.getLink());
                        randomSleep();
                        if(!StringUtils.isEmpty(resourceModel.getTexture())) {
                            downloadNet(resourceModel.getTexture(), download_file_path + "/svg_"+j+"_"+i+"/bbb.jpg");
                        }
                        downloadNet(resourceModel.getZip_link(), download_file_path + "/svg_"+j+"_"+i+"/aaa.zip");
                        System.out.println("download:"+resourceModel.getZip_link());
                        randomSleep();
                        ++i;
                    }
                }
                ++j;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resourceModelList;
    }

    public void downloadNet(String networkFileUrl,String outFile)  {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;
        try {
            URL url = new URL(networkFileUrl);
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(outFile);
            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void randomSleep(){
        Random random = new Random();
        int r = random.nextInt(6);
        if( r == 0){
            r =4;
        }
        try {
            Thread.sleep(r*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        String a = "{sdfsdf},{asdasdf},{ertert}";
        String[] as = a.split("},");
        for(int i=0;i<as.length;i++){
            System.out.println(as[i]);
        }
    }

}
