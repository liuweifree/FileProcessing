package com.color.FileProcessing.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ImageService {

    public void changeSize(String fileName,Integer width,Integer height,String outFile){
        try {
            Thumbnails.of(fileName).size(width,height).toFile(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeQuality(String fileName,String outFile,float quality){
        try {
            Thumbnails.of(fileName).size(1000,1000).outputQuality(quality).toFile(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void thumb(String fileName,String outFile,float quality){
        try {
            Thumbnails.of(fileName).size(2000,2000).outputQuality(quality).toFile(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        String base = "/Users/yc/liuwei/svg/changeDownloadSvg/";

        for( int i=0;i<3477;i++ ) {
            String fileName = base+ "svg_"+i+"/bbb.jpg";
            System.out.println(fileName);
            File file = new File(fileName);

            if( file.exists()){
                String outFileName = base+"svg_"+i+"/bbb_1.jpg";
                File outFile = new File(outFileName);
                if( !outFile.exists()){
                    File inFile = new File(fileName);
                    try {
                        Files.copy(inFile.toPath(), outFile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(outFile + ",length:" +file.length());
                if( file.length() > 400*1024) {
                    try {
                       // Thumbnails.of(fileName).size(2000, 2000).outputQuality(0.8f).toFile(outFileName);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }

        }
    }
}
