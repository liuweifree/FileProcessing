package com.color.FileProcessing.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.color.FileProcessing.service.SvgChange;
import com.color.FileProcessing.service.UploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
public class UploadCrawlerFile {

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private SvgChange svgChange;

    @Value("${base.file.write.path}")
    private String baseFileWritePath;

    private String svgTempPath = "/Users/yc/liuwei/svg/svgtmp";

    //@PostConstruct
    public void uploadAll(){
        String basePath = "/Users/yc/liuwei/svg/changeDownloadSvg";


        for(int i=612;i<622;i++){
           uploadFile(basePath+"/svg_"+i,"svg_"+i);

      //      File file = new File(basePath+"/svg_"+i);

        }

    }
    //@PostConstruct
    private String readJsonFile(){
        String basePath = "/Users/yc/liuwei/svg/changeDownloadSvg";
        String jsonStr="";

        try {
            File jsonFile =  ResourceUtils.getFile("classpath:importData.json");
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            JSONArray jsonArray = JSON.parseArray(jsonStr);
//            for(int i=0;i<jsonArray.size();i++) {
//                String fileName = jsonArray.getJSONObject(i).getString("file");
//                String name = jsonArray.getJSONObject(i).getString("name");
//                System.out.println(fileName+"########"+name+"#####num:"+i);
//                uploadChangeFile(basePath + "/"+fileName, name);
//            }
            File randomFile =  ResourceUtils.getFile("classpath:random.txt");
            FileReader fr = new FileReader(randomFile.getPath());
            BufferedReader bf = new BufferedReader(fr);
            String str;
            Set<String> fileSet = new HashSet<>();
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                if(!StringUtils.isEmpty(str)){
                   String fileName = str.substring(str.indexOf(":")+1);
                    fileSet.add(fileName);

                }
            }
            bf.close();
            fr.close();

            fileSet.forEach(fileName->{
                try {
                    uploadChangeFile(basePath + "/" + fileName, fileName);
                }catch (Exception e){
                    fileName = fileName+"{error}";
                }
                System.out.println(fileName);
            });

            /**
            Set<String> allIdSet = new HashSet<>();
            for(int i=0;i<jsonArray.size();i++){
                allIdSet.add(jsonArray.getJSONObject(i).getString("file"));
            }
            int j = 0;
            while (j<300){
                Random random = new Random();
                Integer r = random.nextInt(3476);
                String rfileName = "svg_"+r;
                if(!allIdSet.contains(rfileName)){
                    System.out.println("name:"+rfileName);
                    uploadFile(basePath+"/"+rfileName,rfileName);
                    ++j;
                }
            }
            **/


        }catch (Exception e){
            log.error("readJsonFile error:{}",e);
        }
        return jsonStr;

    }

    /**
     * 上传正常文件
     * @param pathName
     * @param name
     */
    public void uploadFile(String pathName,String name){
        File file = new File(pathName);
        File[] files = file.listFiles();
        int type =0;
        for(int i=0;i<files.length;i++){
            if(files[i].getPath().indexOf("bbb.jpg") !=-1){
                type = 1;
            }
        }
        String id = svgChange.getResourceId(type);

        File file1 = new File(svgTempPath+"/"+id+"_svg");
        file1.mkdir();
        String pathName1 = file1.getPath();
        for(int i=0;i<files.length;i++){
            String fileNameStr = files[i].getName();
            String outFileName = "";
            if(fileNameStr.indexOf("aaa.jpg")==-1 && fileNameStr.indexOf("bbb.jpg") == -1 ){
                String stuff = fileNameStr.substring(fileNameStr.lastIndexOf("."));
                outFileName = pathName1+"/"+id+stuff;
                cpFile(files[i].getPath(),outFileName);
            }
        }
        String zipFile = this.execZip(id,pathName1);
        uploadFileService.uploadResource(id,1,pathName+"/aaa.jpg",name);
        uploadFileService.uploadResource(id,2,zipFile,name);
        uploadFileService.uploadResource(id,3,pathName+"/test.svg",name);
    }

    public void uploadChangeFile(String pathName,String name){
        File file = new File(pathName);
        File[] files = file.listFiles();
        int type =0;
        for(int i=0;i<files.length;i++){
            if(files[i].getPath().indexOf("bbb.jpg") !=-1){
                type = 1;
            }
        }
        String id = svgChange.getChangeResourceId(name);
        if(StringUtils.isEmpty(id)){
            return;
        }
        File file1 = new File(svgTempPath+"/"+id+"_svg");
        file1.mkdir();
        String pathName1 = file1.getPath();
        for(int i=0;i<files.length;i++){
            String fileNameStr = files[i].getName();
            String outFileName = "";
            if(fileNameStr.indexOf("aaa.jpg")==-1 && fileNameStr.indexOf("bbb.jpg") == -1 && fileNameStr.indexOf("aaa.svg") == -1){
                String stuff = fileNameStr.substring(fileNameStr.lastIndexOf("."));
                outFileName = pathName1+"/"+id+stuff;
                cpFile(files[i].getPath(),outFileName);
            }
        }
        String zipFile = this.execZip(id,pathName1);
        uploadFileService.updateResource(id,pathName+"/aaa.jpg",1);
        uploadFileService.updateResource(id,zipFile,2);
        uploadFileService.updateResource(id,pathName+"/test.svg",3);
    }

    public void cpFile(String inFileName,String outFileName){
        File inFile = new File(inFileName);
        File outFile = new File(outFileName);
        try {
            Files.copy(inFile.toPath(), outFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String execZip(String id, String fileName){
        String pwd = "sh*3jl23#s";
        String zipName = fileName + "/"+id;

        File file = new File( zipName+".jpg");
        String fileNames = "";
        if(file.exists()){
            fileNames = zipName+".jpg "+zipName+".svg "+zipName+".json";
        }else{
            fileNames = zipName+".svg "+zipName+".json";
        }

        String command = baseFileWritePath+"/zipFile.sh "+pwd+" "+zipName+".zip "+fileNames;
        Process p = null;
        BufferedReader stdout = null;
        try {
            p = Runtime.getRuntime().exec(command);
            int exitValue = p.waitFor();

            if (0 != exitValue) {
                log.info("call shell failed. error code is :" + exitValue);
            }else{
                log.info("shell exec success");
            }
            stdout = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = stdout.readLine()) != null) {
                log.info("execZip result:{}",line);
            }
            stdout.close();
        } catch (Exception e) {
            log.info("execNodeJs error:{}",e);
        }

        return zipName+".zip";
    }


    public static void main(String[] args){
        String a = "/User/svg/aaa.jpg";
        String stuff = a.substring(a.lastIndexOf("."));
        System.out.println(stuff);

    }

}
