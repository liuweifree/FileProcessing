package com.color.FileProcessing.controller;

import com.color.FileProcessing.crawler.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestCrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @RequestMapping("/crawler/save")
    public String changeSvg(){
        crawlerService.saveAllResource();
        return "success";
    }

}
