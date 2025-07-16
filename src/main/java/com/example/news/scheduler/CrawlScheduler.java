package com.example.news.scheduler;

import com.example.news.service.CrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CrawlScheduler {

    @Autowired
    private CrawlService crawlService;

    // Chạy mỗi 30 phút (1800000 milliseconds)
    @Scheduled(fixedRate = 180000)
    public void scheduleCrawl() {
        try {
            crawlService.crawlFromTuoitre();
            System.out.println("Cronjob crawl RSS chạy lúc: " + new java.util.Date());
        } catch (Exception e) {
            System.err.println("Lỗi cronjob:");
            e.printStackTrace();
        }
    }
}
