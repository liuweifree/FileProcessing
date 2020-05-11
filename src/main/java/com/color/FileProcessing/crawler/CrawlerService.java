package com.color.FileProcessing.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.color.FileProcessing.model.ResourceListModel;
import com.color.FileProcessing.model.ResourceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class CrawlerService {

    @Autowired
    private RestTemplate restTemplate;

    private String homeUrl= "https://color.ksmobile.net/v2/home";

    private String resourceUrl = "https://color.ksmobile.net/v2/resource";

    private String uid = "E5D5125D-A2BH-78SF-3434-9C2EF75919D1";


    private String saveJsonpath = "/Users/yc/liuwei/svg";


    public JSONObject postRequest(String url,JSONObject json ){

        //map.add("id",id);


        HttpHeaders header = new HttpHeaders();
        // 需求需要传参为form-data格式
        header.setContentType(MediaType.APPLICATION_JSON);
        header.set("Accept-Language","zh-cn");
        header.set("User-Agent","app/205026 CFNetwork/1121.2.2 Darwin/19.2.0");
        header.set("client_ver","2.5.1");
        header.set("timezone","8");
        header.set("userisvip","1");
        header.set("pkg_name","coloring.art.color.by.number.app");
        header.set("lan","zh_CN");
        header.set("os","ios");
        header.set("userloginnum","26");
        header.set("os","ios");
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(json, header);

        ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, httpEntity, JSONObject.class);
        return result.getBody();
    }


    public List<Integer> getTagList(){
        List<Integer> tagList = new ArrayList<>();
        JSONObject request = new JSONObject();
        request.put("userid",uid);
        request.put("type",2);
        JSONObject jsonObject = postRequest(homeUrl,request);
        if(null != jsonObject && !jsonObject.isEmpty()){
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("tag");
            if( null != jsonArray && !jsonArray.isEmpty()){
                for( int i=0;i<jsonArray.size();i++){
                    Integer tagId = jsonArray.getJSONObject(i).getInteger("id");
                    tagList.add(tagId);
                }
            }
        }
        return tagList;
    }


    public ResourceListModel getResourceList(Integer tagId, Integer pageNum){
        ResourceListModel resourceListModel = new ResourceListModel();
        List<ResourceModel> resourceModelList = new ArrayList<>();
        JSONObject request = new JSONObject();
        request.put("userid",uid);
        request.put("tag_id",tagId);
        request.put("type",2);
        request.put("num",12);
        request.put("pn",pageNum);
        JSONObject jsonObject = postRequest(resourceUrl,request);
        if( null != jsonObject && !jsonObject.isEmpty()){
            JSONObject dataObject =  jsonObject.getJSONObject("data");
            Integer total = dataObject.getJSONObject("pageInfo").getInteger("total");
            resourceListModel.setTotal(total);
            JSONArray list = dataObject.getJSONArray("list");
            for(int i=0;i<list.size();i++){
                JSONObject resourceJSON = list.getJSONObject(i);
                ResourceModel resourceModel = JSON.toJavaObject(resourceJSON,ResourceModel.class);
                resourceModelList.add(resourceModel);
            }
            resourceListModel.setResourceModelList(resourceModelList);
        }
        return resourceListModel;
    }

    public List<ResourceModel> getAllResourceList(Integer tagId){
        List<ResourceModel> resourceModelList = new ArrayList<>();
        ResourceListModel resourceListModel = getResourceList(tagId,1);
        Integer total = resourceListModel.getTotal();
        Integer totalPn = 1;
        if( total % 12 == 0){
            totalPn = total / 12;
        }else{
            totalPn = (total / 12) +1;
        }
        resourceModelList.addAll(resourceListModel.getResourceModelList());
        randomSleep();
       for(int i=2;i<=totalPn;i++){
           System.out.println("resource page:"+i);
           ResourceListModel resourceListModel1 =getResourceList(tagId,i);
           resourceModelList.addAll(resourceListModel1.getResourceModelList());
           randomSleep();
       }
       return resourceModelList;
    }


    public void saveAllResource(){
        List<Integer> tagList = this.getTagList();
        tagList.forEach(tagId->{
            System.out.println("get resource tagId:"+tagId);
            List<ResourceModel> resourceModelList = getAllResourceList(tagId);
           // System.out.println("result:"+JSON.toJSONString(resourceModelList));
            try {
                FileWriter outFile = new FileWriter(saveJsonpath + "/crawler.json",true);
                BufferedWriter bw = new BufferedWriter(outFile);
                bw.write(JSON.toJSONString(resourceModelList));
                bw.close();
                outFile.close();
            }catch (Exception e){

            }
        });

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
        Integer a = 122;
        Integer totalP = 1;
        if( a% 12 == 0){
            totalP = a /12;
        }else{
            totalP = a /12 +1;
        }
        System.out.println(totalP);

    }
}
