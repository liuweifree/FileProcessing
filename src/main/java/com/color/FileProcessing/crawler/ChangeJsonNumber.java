package com.color.FileProcessing.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigDecimal;

@Slf4j
@Service
public class ChangeJsonNumber {

    private String OUT_FILE_PATH = "/Users/yc/liuwei/svg/changeDownloadSvg";

   //@PostConstruct
    public void changeAllJson(){
        for( int i=0;i<3477;i++){
            String fileIn =OUT_FILE_PATH + "/svg_"+i+"/aaa.json";
            System.out.println(fileIn);
            changeJson(fileIn,fileIn);
        }
    }


    public void changeJson(String fileName,String outfile){
        try {
            File jsonFile = new File(fileName);
            InputStream in = new FileInputStream(jsonFile);
            InputStreamReader inputReader = new InputStreamReader(in);
            BufferedReader bf = new BufferedReader(inputReader);
            String str  = bf.readLine();
            JSONObject jsonObject = JSON.parseObject(str);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int i=0;i<jsonArray.size();i++){
                int a = i+1;
                jsonArray.getJSONObject(i).put("id",a+"");
                JSONArray circles = jsonArray.getJSONObject(i).getJSONArray("circles");
                if( !circles.isEmpty()){
                    for(int j=0;j<circles.size();j++){
                        Object r = circles.getJSONObject(j).get("r");
                        if( r instanceof  String){
                            Double dd = Double.valueOf((String)r);
                            circles.getJSONObject(j).put("r", BigDecimal.valueOf(dd));
                        }
                        Object x = circles.getJSONObject(j).get("x");
                        if( x instanceof  String){
                            Double dd = Double.valueOf((String)x);
                            circles.getJSONObject(j).put("x",BigDecimal.valueOf(dd));
                        }

                        Object y = circles.getJSONObject(j).get("y");
                        if( y instanceof  String){
                            Double dd = Double.valueOf((String)y);
                            circles.getJSONObject(j).put("y",BigDecimal.valueOf(dd));
                        }
                    }
                    jsonArray.getJSONObject(i).put("circles",circles);
                }
            }
            jsonObject.put("data",jsonArray);
            FileWriter outFile = new FileWriter(outfile);
            BufferedWriter bw=new BufferedWriter(outFile);
            bw.write(jsonObject.toJSONString());
            bw.close();
            outFile.close();
        }catch (Exception e){
            log.info("changeJson fileName:{} , outfile:{}",fileName,outfile);
            log.info("changeJson error:{}",e);
        }
    }

}
