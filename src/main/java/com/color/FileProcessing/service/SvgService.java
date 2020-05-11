package com.color.FileProcessing.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.color.FileProcessing.model.FileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class SvgService {

    @Autowired
    private SvgChange svgChange;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UploadFileService uploadFileService;

    public void findAndCreateSvg(){
        List<FileModel> svgList =  svgChange.findSvgFile();
        svgList.forEach(svgFile->{
            int type = 1;
            if(StringUtils.isEmpty(svgFile.getImageFile())){
                type = 0;
            }
            String num = svgChange.getResourceId(type);
            String pathName = num+"_svg";
            String fileName = svgChange.createDirectory(pathName);
            String outFileName = svgChange.getBaseFileWritePath()+"/"+fileName+"/"+num+"_1.svg";
            String outFileName1 = svgChange.getBaseFileWritePath()+"/"+fileName+"/"+num+".svg";
            String[] names = svgFile.getImageFile().split("\\.");
            String outImage = svgChange.getBaseFileWritePath()+"/"+fileName+"/"+num+"."+names[names.length-1];
            svgChange.execNodeJs(svgFile.getSvgFile(),outFileName);
            svgChange.group(outFileName,outFileName1);
            //svgChange.cpFile(svgFile.getImageFile(),outImage);
            if( !StringUtils.isEmpty(svgFile.getImageFile())) {
                imageService.changeQuality(svgFile.getImageFile(), outImage, 0.8f);
            }
            svgChange.delFile(outFileName);
            svgChange.createSvgJson(outFileName1);

        });

    }

    public void writeJsonFile(String jsonStr,String fileName){
        File file = new File(fileName);
        int i=0;
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            i=1;
        }
        try {
            FileWriter outFile = new FileWriter(fileName,true);
            outFile.write(jsonStr);
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Async("taskExecutor")
    public void delJsonFile(String fileName){
        String id = fileName.substring(fileName.lastIndexOf("/")+1,fileName.lastIndexOf("."));
        String filePath = fileName.split("\\.")[0];

        String zipFile = svgChange.execZip(id,fileName);
        uploadFileService.uploadResource(id,1,filePath+".png",null);
        uploadFileService.uploadResource(id,2,zipFile,null);
        uploadFileService.uploadResource(id,3,filePath+".svg",null);
        svgChange.delSvgJson(fileName);
    }

}
