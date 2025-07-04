package com.example.news.controllers;

import com.example.news.service.CrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crawl")
public class CrawlController {

    @Autowired
    private CrawlService crawlService;

    @GetMapping("/rss")
    public String crawlRss() {
        try {
            crawlService.crawlFromTuoitre();
            return "Crawl RSS thành công!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi crawl RSS: " + e.getMessage();
        }
    }
}
