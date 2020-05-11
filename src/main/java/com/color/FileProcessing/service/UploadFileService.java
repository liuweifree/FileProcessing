package com.color.FileProcessing.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class UploadFileService {

    @Value("${upload.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public void uploadResource(String id,Integer type,String filePath,String name){
        String url = apiUrl+"/api/fileprocess/resource/upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("multipart/form-data;charset=UTF-8"));
        FileSystemResource resource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("resourceId", id);
        param.add("Filedata", resource);
        param.add("type", type);
        if(!StringUtils.isEmpty(name)) {
            param.add("name", name);
        }
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(param, headers);
        String result = restTemplate.postForObject(url, formEntity, String.class);


    }




    public void updateResource(String id,String filePath,Integer type){


        String url = apiUrl+"/api/fileprocess/resource/uploadFile";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("multipart/form-data;charset=UTF-8"));
        FileSystemResource resource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("resourceId", id);
        param.add("Filedata", resource);
        param.add("type", type);
        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(param, headers);
        String result = restTemplate.postForObject(url, formEntity, String.class);
    }




}
