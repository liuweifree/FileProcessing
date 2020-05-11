package com.color.FileProcessing.controller;

import com.color.FileProcessing.model.ResultModel;
import com.color.FileProcessing.service.SvgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FileProcessingController {

    @Autowired
    private SvgService svgService;

    @RequestMapping("/svg/create")
    public String changeSvg(){
        svgService.findAndCreateSvg();
        return "success";
    }

    @RequestMapping("/svg/delJson")
    public ResultModel delJsonFile(String fileName){
        System.out.println("delJson:"+fileName);
        svgService.delJsonFile(fileName);
        ResultModel resultModel = new ResultModel();
        resultModel.setCode(200);
        resultModel.setMsg("success");
        return resultModel;
    }

    @RequestMapping("/svg/writeJson")
    public ResultModel writeSvgJson(String svgJson,String fileName){

        if(!StringUtils.isEmpty(svgJson)) {
            svgJson = svgJson.replaceAll(",null","");
            System.out.println("writeJson:"+svgJson);
            svgService.writeJsonFile(svgJson, fileName);
        }
        ResultModel resultModel = new ResultModel();
        resultModel.setCode(200);
        resultModel.setMsg("success");
        return resultModel;
    }
}
