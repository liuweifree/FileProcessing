package com.color.FileProcessing.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.color.FileProcessing.model.FileModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 处理svg文件
 */

@Slf4j
@Service
public class SvgChange {

    @Value("${base.file.read.path}")
    private String baseFileReadPath;

    @Value("${base.file.write.path}")
    private String baseFileWritePath;

    @Value("${nodejs.path}")
    private String nodeJsPath;

    @Value("${json.file.path}")
    private String jsonFilePath;

    @Value("${upload.url}")
    private String apiUrl;

    @Autowired
    private ImageService imageService;

    @Autowired
    private RestTemplate restTemplate;

    private static String DEFS_STR = "<defs><radialGradient id=\"grad1\" cx=\"50%\" cy=\"50%\" r=\"50%\" fx=\"50%\" fy=\"20%\"><stop offset=\"0%\" style=\"stop-color:rgb(148,92,217);stop-opacity:1\" /><stop offset=\"50%\" style=\"stop-color:rgb(230,115,196);stop-opacity:1\" /><stop offset=\"100%\" style=\"stop-color:rgb(254,234,147);stop-opacity:1\" /></radialGradient></defs>";



    public String getBaseFileWritePath(){
        return baseFileWritePath;
    }



    public String getResourceId(Integer type){
        String url = apiUrl+"/api/fileprocess/resource/save?pwd=dj4j5fh@jd*3HG&type="+type;
        ResponseEntity<JSONObject> responseEntity =  restTemplate.getForEntity(url,JSONObject.class);
        JSONObject jsonObject = responseEntity.getBody();
        return jsonObject.getJSONObject("data").getString("id");
    }

    public String getChangeResourceId(String name){
        String idurl = apiUrl+"/api/fileprocess/resource/findByName?name="+name;
        ResponseEntity<JSONObject> responseEntity =  restTemplate.getForEntity(idurl,JSONObject.class);
        JSONObject jsonObject = responseEntity.getBody();
        return  jsonObject.getString("data");
    }




    /**
     * 创建目录
     */
    public String createDirectory(String path){
        File file = new File(baseFileWritePath+"/"+path);
        file.mkdir();
        return file.getName();
    }


    /**
     * 遍历目录 找到所有的svg文件
     * @return
     */
    public List<FileModel> findSvgFile(){

        File file = new File(baseFileReadPath);
        File[] files = file.listFiles();
        List<FileModel> svfList = new ArrayList<>();
        for (File ff:files) {
            if(ff.isDirectory()){
                File[] files1 = ff.listFiles();
                FileModel fileModel = new FileModel();
                for(File ff1 : files1){
                    String[] names = ff1.getName().split("\\.");
                    String suffix = names[names.length-1];
                    if(suffix.equals("svg")){
                        fileModel.setSvgFile(ff1.getAbsolutePath());
                    }
                    if(suffix.equals("jpg")){
                        fileModel.setImageFile(ff1.getAbsolutePath());
                    }
                }
                svfList.add(fileModel);
            }
        }
        
        return svfList;
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

    /**
     * 执行nodejs脚本
     */
    public void execNodeJs(String inFile,String outFile){
        Process p = null;
        BufferedReader stdout = null;
        //String command = nodeJsPath+"/bin/svgo "+inFile+" --enable=removeRect,inlineStyles,removeFillNoneLw --disable=convertPathData,mergePaths --config '{ \"plugins\": [ { \"inlineStyles\": { \"onlyMatchedOnce\": false } }] }' -o " + outFile;

        String command = baseFileWritePath+"/exceNode.sh "+inFile+" "+outFile;
        try {
            p = Runtime.getRuntime().exec(command);
            int exitValue = p.waitFor();

            if (0 != exitValue) {
                log.info("call shell failed. error code is :" + exitValue);
                log.info("call shell failed. inFile :" + inFile);
            }else{
                log.info("shell exec success");
            }
            stdout = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = stdout.readLine()) != null) {
                log.info("execNodeJs result:{}",line);
            }
            stdout.close();
        } catch (Exception e) {
            log.info("execNodeJs error:{}",e);
        }

    }

    public String execZip(String id,String fileName){
        String pwd = "sh*3jl23#s";
        String zipName = fileName.split("\\.")[0];

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
                log.info("execNodeJs result:{}",line);
            }
            stdout.close();
        } catch (Exception e) {
            log.info("execNodeJs error:{}",e);
        }

        return zipName+".zip";
    }

    /**
     * svg 按照颜色分组
     * @param fileName
     */
    public void group(String fileName,String outFileName){
        File file = new File(fileName);
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);

        Map<String, List<String>> jsonMap = new HashMap<>();
        try {
            Document doc = f.createDocument(file.toURI().toString());
            NodeList nodelist =doc.getElementsByTagName("path");


            if( null != nodelist && nodelist.getLength() > 0) {
                for(int i=0;i<nodelist.getLength();i++){
                    Node node = nodelist.item(i);

                    NamedNodeMap namedNodeMap = node.getAttributes();
                    String color = namedNodeMap.getNamedItem("fill").getNodeValue();
                    String pathD = namedNodeMap.getNamedItem("d").getNodeValue();


                    List<String> jsonList = jsonMap.get(color);
                    if( null == jsonList || jsonList.size() == 0){
                        jsonList = new ArrayList<>();
                    }
                    String pathStr = "<path d=\""+pathD+"\"/>";
                    jsonList.add(pathStr);
                    jsonMap.put(color,jsonList);
                }
            }
            List<String> result = new ArrayList<>();
            List<String> whiteResult = new ArrayList<>();
            if( null != jsonMap && jsonMap.size() >0){
                final Integer[] i = {1};
                jsonMap.forEach((key,value)->{

                    String gStr = "<g id=\""+ i[0] +"\" fill=\""+key+"\" >";
                    String whiteGstr = "<g id=\""+ i[0] +"\" fill=\"white\" >";
                    result.add(gStr);
                    whiteResult.add(whiteGstr);
                    value.forEach(pp->{
                        result.add(pp);
                        whiteResult.add(pp);
                    });
                    result.add("</g>");
                    whiteResult.add("</g>");
                    ++i[0];
                });
            }


            FileWriter outFile = new FileWriter(outFileName);

            StringBuffer sb = new StringBuffer();
            BufferedWriter bw=new BufferedWriter(outFile);
            String header = "<?xml version=\"1.0\" ?><svg height=\"1000.0\" version=\"1.1\" width=\"1000.0\" xmlns=\"http://www.w3.org/2000/svg\"><rect fill=\"black\" height=\"1000.0\" width=\"1000.0\" />";
            String header1 = "<?xml version=\"1.0\" ?><svg height=\"1000.0\" version=\"1.1\" width=\"1000.0\" xmlns=\"http://www.w3.org/2000/svg\"><rect fill=\"url(#orange_red)\" height=\"1000.0\" width=\"1000.0\" />";
            //String defs = "<defs><linearGradient id=\"orange_red\" x1=\"0%\" y1=\"0%\" x2=\"100%\" y2=\"0%\"><stop offset=\"0%\" style=\"stop-color:rgb(255,255,0);stop-opacity:1\"/><stop offset=\"100%\" style=\"stop-color:rgb(255,0,0);stop-opacity:1\"/></linearGradient></defs>";
            sb.append(header);
            //sb.append(defs);
            bw.write(header);
            result.forEach(o->{
                try {
                    bw.write( o+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bw.write(" </svg>");
            bw.close();
            outFile.close();

            whiteResult.forEach(o->{
                sb.append(o+"\r\n");
            });
            sb.append(" </svg>");
            String[] ofn = outFileName.split("\\.");
            createImage(sb.toString(),ofn[0]+"_1.png");


        }catch (Exception e){
            log.info("group error:{}",e);
        }
    }


    public void delFile(String name){
        File file = new File(name);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 生成渐变色图片
     */
    public void createImage(String svgCode , String outFile){
        FileOutputStream outputStream = null;
        try {
            File file = new File(outFile);
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            byte[] bytes = svgCode.getBytes("utf-8");
            PNGTranscoder t = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(
                    new ByteArrayInputStream(bytes));
            TranscoderOutput output = new TranscoderOutput(outputStream);
            t.transcode(input, output);
            outputStream.flush();

            String resultFile = outFile.replaceAll("_1","");
            imageService.changeSize(outFile,400,400,resultFile);
        } catch (Exception e) {
           log.info("createImage error:{}",e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.info("createImage error:{}",e);
                }
            }
        }

    }

    /**
     * 生成json文件数组
     * @param fileName
     */
    public void createSvgJson(String fileName){

        try {
            File file = new File(jsonFilePath+"/svglist.json");
            InputStream in = new FileInputStream(file);
            InputStreamReader inputReader = new InputStreamReader(in);
            BufferedReader bf = new BufferedReader(inputReader);
            String str  = bf.readLine();
            JSONObject jsonObject=null;
            if( null != str) {
                jsonObject = JSON.parseObject(str);
                JSONArray jsonArray =jsonObject.getJSONArray("svgList");
                if( null == jsonArray){
                    jsonArray = new JSONArray();
                    jsonArray.add(fileName);
                    jsonObject.put("svgList",jsonArray);
                }else{
                    jsonArray.add(fileName);
                    jsonObject.put("svgList",jsonArray);
                }
            }else{
                jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(fileName);
                jsonObject.put("svgList",jsonArray);
            }

            if( null != jsonObject) {
                FileWriter outFile = new FileWriter(jsonFilePath + "/svglist.json");
                BufferedWriter bw = new BufferedWriter(outFile);
                bw.write(jsonObject.toJSONString());
                bw.close();
                outFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void delSvgJson(String fileName){

        try {
            File file = new File(jsonFilePath+"/svglist.json");
            InputStream in = new FileInputStream(file);
            InputStreamReader inputReader = new InputStreamReader(in);
            BufferedReader bf = new BufferedReader(inputReader);
            String str  = bf.readLine();
            JSONObject jsonObject=null;
            if( null != str) {
                jsonObject = JSON.parseObject(str);
                JSONArray jsonArray =jsonObject.getJSONArray("svgList");
                if( null != jsonArray){
                    int j = 0;
                    for(int i=0;i<jsonArray.size();i++){
                        String jsonStr = jsonArray.getString(i);
                        if(fileName.equals(jsonStr)){
                            j=i;
                        }
                    }
                    jsonArray.remove(j);
                    jsonObject.put("svgList",jsonArray);
                }
            }

            if( null != jsonObject) {
                FileOutputStream fileOutputStream = new FileOutputStream(jsonFilePath + "/svglist.json");
                fileOutputStream.write(jsonObject.toJSONString().getBytes("utf-8"));
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void changeDownloadSvg(String fileName,String outFileName){
        File file = new File(fileName);
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        Map<String, List<String>> jsonMap = new HashMap<>();
        try {
            Document doc = f.createDocument(file.toURI().toString());


            NodeList nodelist =doc.getElementsByTagName("g");

            if( null != nodelist && nodelist.getLength() > 0) {
                for(int i=0;i<nodelist.getLength();i++){
                    Node node = nodelist.item(i);
                    NamedNodeMap namedNodeMap = node.getAttributes();
                    if( null != namedNodeMap.getNamedItem("fill")){
                        namedNodeMap.getNamedItem("fill").setNodeValue("white");
                    }
                }
            }
            NodeList defslist = doc.getElementsByTagName("rect");
            if( null != defslist && defslist.getLength() >0){
                for(int i=0;i<defslist.getLength();i++){
                    Node node = defslist.item(i);

                    NamedNodeMap namedNodeMap = node.getAttributes();
                    if( null != namedNodeMap.getNamedItem("fill")){
                        namedNodeMap.getNamedItem("fill").setNodeValue("url(#grad1)");
                    }
                }
            }
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            transformer = transFactory.newTransformer();

            StringWriter buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            transformer.transform(new DOMSource(doc.getDocumentElement()), new StreamResult(buffer));

            String elementStr = buffer.toString();

            elementStr = elementStr.replaceAll("<defs/>",DEFS_STR);



            System.out.println(elementStr);

           // createImage(elementStr,outFileName+"_2.png");


        }catch (Exception e){
            log.info("group error:{}",e);
        }
    }




    public static void main(String[] args){



        changeDownloadSvg("/Users/yc/liuwei/svg/changeDownloadSvg/svg_101/aaa.svg","");

    }

}
