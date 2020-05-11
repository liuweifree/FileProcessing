package com.color.FileProcessing.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChangeDownloadFile {

    private String BASE_FILE_PATH = "/Users/yc/liuwei/svg/downloadSvg";

    private String OUT_FILE_PATH = "/Users/yc/liuwei/svg/changeDownloadSvg";

    private String OUT_NO_JSON_PATH = "/Users/yc/liuwei/svg/noJson";

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
                            circles.getJSONObject(j).put("r",BigDecimal.valueOf(dd));
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

    public void changeSvg(String fileName,String outfile){
        File file = new File(fileName);
        try {
            InputStream in = new FileInputStream(file);
            InputStreamReader inputReader = new InputStreamReader(in);
            BufferedReader bf = new BufferedReader(inputReader);
            String str;
            int i=1;
            FileWriter outFile = new FileWriter(outfile);
            BufferedWriter bw=new BufferedWriter(outFile);
            while ((str = bf.readLine()) != null) {
                if(str.indexOf("id=")!=-1){
                    String a = str.replaceAll("id=\"id[0-9]+:id[0-9]+\"","id=\""+i+"\"");
                    ++i;
                    bw.write(a);
                }else{
                    bw.write(str);
                }
            }
            bf.close();
            inputReader.close();
            bw.close();
            outFile.close();
        }catch (Exception e){
            log.info("changeSvg fileName:{} , outfile:{}",fileName,outfile);
            log.info("changeSvg error:{}",e);
        }

    }

    //@PostConstruct
    public void test(){
        for( int i =0 ;i<3477;i++) {
            String svgfileName = "/Users/yc/liuwei/svg/changeDownloadSvg/svg_"+i+"/aaa.svg";
            String jsonFileName = "/Users/yc/liuwei/svg/changeDownloadSvg/svg_"+i+"/aaa.json";
            String outSvgFile = "/Users/yc/liuwei/svg/changeDownloadSvg/svg_"+i+"/test.svg";
            changeSvgAndJson(svgfileName, jsonFileName, outSvgFile);
        }

    }


    public void changeSvgAndJson(String svgFileName , String jsonFileName,String outSvgFile){
        File osf = new File(outSvgFile);
        if(osf.exists()){
            osf.delete();
        }

        File jsonFile = new File(jsonFileName);

        File file = new File(svgFileName);
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        Map<String,String> jsonColorMap = new HashMap<>();
        try{
            InputStream in = new FileInputStream(jsonFile);
            InputStreamReader inputReader = new InputStreamReader(in);
            BufferedReader bf = new BufferedReader(inputReader);
            String str  = bf.readLine();
            JSONObject jsonObject = JSON.parseObject(str);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String color = jsonObject1.getString("color");
                System.out.println("id:"+id+",color:"+color);
                jsonColorMap.put(color,id);
            }


            Document doc = f.createDocument(file.toURI().toString());
            NodeList nodelist =doc.getElementsByTagName("g");
            if( null != nodelist && nodelist.getLength() > 0) {

                for (int i = 0; i < nodelist.getLength(); i++) {
                    Node node = nodelist.item(i);
                    Node gChildNode = node.getFirstChild();
                    Node gnode = node.getAttributes().getNamedItem("id");

                    if( null != gChildNode){
                       Node fillNode =  gChildNode.getAttributes().getNamedItem("fill");
                       if( null != fillNode){
                           if( null != gnode) {
                               gnode.setNodeValue(jsonColorMap.get(fillNode.getNodeValue()));
                           }
                           System.out.println(fillNode.getNodeValue());
                       }
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
            FileWriter outFile = new FileWriter(outSvgFile);
            BufferedWriter bw=new BufferedWriter(outFile);
            bw.write(elementStr);
            bf.close();
            inputReader.close();
            bw.close();
            outFile.close();

        }catch (Exception e){
            System.out.println(e);
        }

    }

    public void mvJpg(){

    }

    //@PostConstruct
    public void changeAll(){
        File file = new File(BASE_FILE_PATH);
        File[] files = file.listFiles();
        int a = 0;
        int b = 0;
        for(int i=0;i<files.length;i++){
            File[] subFiles = files[i].listFiles();
            if( subFiles == null){
                System.out.println("subFiles is null , "+files[i].getPath());
                continue;
            }
            boolean hasJsonFile = false;
            for(int j=0;j<subFiles.length;j++){
                String fileName = subFiles[j].getPath();
                if(fileName.indexOf(".json")!=-1){
                    hasJsonFile = true;
                }
            }
            //如果包含json文件 那么转移走
            if(hasJsonFile){
                String path = OUT_FILE_PATH+"/svg_"+a;
                File file1 = new File(path);
                if(!file1.exists()){
                    file1.mkdir();
                }
                for(int j=0;j<subFiles.length;j++){
                    String fileName = subFiles[j].getPath();
                    if(fileName.indexOf(".json")!=-1){
                        changeJson(fileName,path+"/aaa.json");
                    }else if( fileName.indexOf(".svg") != -1){
                        changeSvg(fileName,path+"/aaa.svg");
                    }else if( fileName.indexOf(".jpg") != -1){
                        cpFile(fileName,path+"/"+subFiles[j].getName());
                    }
                }
                ++a;
            }else{
                String path = OUT_NO_JSON_PATH+"/svg_"+b;
                File file1 = new File(path);
                if(!file1.exists()){
                    file1.mkdir();
                }
                for(int j=0;j<subFiles.length;j++) {
                    String fileName = subFiles[j].getPath();
                    cpFile(fileName, path+"/"+subFiles[j].getName());
                }
                ++b;
            }
        }
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


    public static void main(String[] args){
        String a = "[{\"r\":\"18.994457999999998\",\"x\":33.36403,\"y\":33.376529999999995},{\"r\":0.4193033849999999,\"x\":164.80302739999942,\"y\":36.98350574999993},{\"r\":1.1945543850000002,\"x\":184.9768300000003,\"y\":40.045103075000064},{\"r\":1.08817296,\"x\":200.75238319999966,\"y\":47.504939099999966},{\"r\":2.4611539500000004,\"x\":234.6017575624996,\"y\":57.298023425000025},{\"r\":4.5103803749999996,\"x\":258.45260607499955,\"y\":70.26243228749995},{\"r\":0.1909314,\"x\":280.2300318749998,\"y\":158.69568062499948}]";
        String b = "[{\"r\":\"4\",\"x\":148.33952593485515,\"y\":261.3676323445638},{\"r\":1.40248782,\"x\":63.26571909999997,\"y\":214.01396029999998},{\"r\":1.5863295,\"x\":33.821546299999994,\"y\":221.43667919999996},{\"r\":2.34945432,\"x\":136.47873994999998,\"y\":291.89962250000013},{\"r\":1.01594775,\"x\":101.19193819999995,\"y\":226.94227584999993},{\"r\":0.6788637900000001,\"x\":73.5520287,\"y\":247.2832759999999}]";
        JSONArray jsonArray = JSON.parseArray(b);
        for(int i=0;i< jsonArray.size();i++){
            Object r = jsonArray.getJSONObject(i).get("r");
            if( r instanceof String){
                System.out.println(r);
                Integer c = Integer.valueOf((String)r);
                Float dd = Float.valueOf((String)r);

                jsonArray.getJSONObject(i).put("r",BigDecimal.valueOf(dd));
            }
        }
        for(int i=0;i< jsonArray.size();i++){
            Object r = jsonArray.getJSONObject(i).get("r");
            if( r instanceof String) {
                System.out.println(r);
            }
        }

    }
}
