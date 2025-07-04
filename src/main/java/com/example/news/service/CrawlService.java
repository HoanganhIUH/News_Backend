package com.example.news.service;

import com.example.news.model.Article;
import com.example.news.model.Category;
import com.example.news.repository.ArticleRepository;
import com.example.news.repository.CategoryRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CrawlService {

    private static final String RSS_URL = "https://tuoitre.vn/rss/tin-moi-nhat.rss";

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

//    @Transactional
//    public void crawlFromRss() throws IOException {
//        Document rssDoc = Jsoup.connect(RSS_URL).get();
//        Elements items = rssDoc.select("item");
//
//        System.out.println("Tổng số bài trong RSS: " + items.size());
//
//        for (Element item : items) {
//            String title = item.selectFirst("title").text();
//            String link = item.selectFirst("link").text();
//            String pubDateStr = item.selectFirst("pubDate").text(); // Bạn có thể parse pubDate nếu cần
//
//            // Kiểm tra đã tồn tại chưa
//            Optional<Article> existing = articleRepository.findAll().stream()
//                .filter(a -> a.getUrl().equals(link))
//                .findFirst();
//            if (existing.isPresent()) continue;
//
//            // Lấy chi tiết bài viết
//            Document detailDoc = Jsoup.connect(link)
//                .userAgent("Mozilla/5.0")
//                .timeout(10_000)
//                .get();
//
//            String content = detailDoc.select(".detail-content").text();
//
//            // Lấy category từ breadcrumbs nếu có
//            String categoryName = detailDoc.select(".bread-crumbs .item a").last() != null
//                ? detailDoc.select(".bread-crumbs .item a").last().text()
//                : "Tin mới";
//
//            Category category = categoryRepository.findAll().stream()
//                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
//                .findFirst()
//                .orElseGet(() -> {
//                    Category newCat = new Category();
//                    newCat.setName(categoryName);
//                    return categoryRepository.save(newCat);
//                });
//
//            Article article = new Article();
//            article.setTitle(title);
//            article.setUrl(link);
//            article.setContent(content);
//            article.setPublishedAt(LocalDateTime.now());
//            article.setCategory(category);
//
//            articleRepository.save(article);
//
//            System.out.println("Đã lưu: " + title);
//        }
//    }

    @Transactional
    public void crawlFromTuoitre() throws IOException {
        Map<String, String> categoryLinks = new HashMap<>();
        categoryLinks.put("Thời sự", "https://tuoitre.vn/thoi-su.htm");
        categoryLinks.put("Thể thao", "https://tuoitre.vn/the-thao.htm");
        categoryLinks.put("Thế giới", "https://tuoitre.vn/the-gioi.htm");
        categoryLinks.put("Giải trí", "https://tuoitre.vn/giai-tri.htm");
        categoryLinks.put("Công nghệ", "https://tuoitre.vn/cong-nghe.htm");
        categoryLinks.put("Xe", "https://tuoitre.vn/xe.htm");
        categoryLinks.put("Giáo dục", "https://tuoitre.vn/giao-duc.htm");
        categoryLinks.put("Video", "https://tuoitre.vn/video.htm");
        

        for (Map.Entry<String, String> entry : categoryLinks.entrySet()) {
            String categoryName = entry.getKey();
            String url = entry.getValue();

            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                .header("Accept-Language", "vi-VN,vi;q=0.9")
                .get();

            // Chọn selector phù hợp cho từng chuyên mục
            Elements newsElements;
            if (categoryName.equals("Tin mới")) {
                newsElements = doc.select(".name-news a, h3.title-news a");
            } else {
                newsElements = doc.select("h3.name-news a, h3.title-news a, .box-category-item a");
            }
            System.out.println("[" + categoryName + "] Số lượng bài crawl được: " + newsElements.size());

            for (Element news : newsElements) {
                String title = news.text();
                String articleUrl = news.absUrl("href");
                System.out.println("Tiêu đề: " + title + " | Link: " + articleUrl);

                Document detailDoc = Jsoup.connect(articleUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                    .header("Accept-Language", "vi-VN,vi;q=0.9")
                    .get();

                // Nếu title rỗng, lấy lại từ trang chi tiết với nhiều selector hơn
                if (title == null || title.trim().isEmpty()) {
                    title = detailDoc.select("h1.article-title, h1.title-article, h1.title, h1, .article-title, .title-article, .title").text();
                    System.out.println("Title lấy từ trang chi tiết: " + title);
                }

                // Lấy urlVideo từ trang chi tiết
                String urlVideo = detailDoc.select("video source").attr("src");
                if (urlVideo == null || urlVideo.isEmpty()) {
                    urlVideo = detailDoc.select("iframe").attr("src");
                }

                // Lấy urlImage từ trang chi tiết
                String urlImage = detailDoc.select("meta[property=og:image]").attr("content");
                if (urlImage == null || urlImage.isEmpty()) {
                    urlImage = detailDoc.select(".detail-content img").attr("src");
                }
                if (urlImage == null || urlImage.isEmpty()) {
                    urlImage = detailDoc.select("img").attr("src");
                }
                if (urlImage == null || urlImage.isEmpty()) {
                    urlImage = detailDoc.select("meta[name=image]").attr("content");
                }

                // Đảm bảo dùng biến title đã cập nhật khi lưu Article
                String content = detailDoc.select(".detail-content").text();
                LocalDateTime publishedAt = LocalDateTime.now();

                Category category = categoryRepository.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName)).findFirst()
                    .orElseGet(() -> {
                        Category newCat = new Category();
                        newCat.setName(categoryName);
                        return categoryRepository.save(newCat);
                    });

                if (articleRepository.findAll().stream().noneMatch(a -> a.getUrl().equals(articleUrl))) {
                    Article article = new Article();
                    article.setTitle(title); // Đảm bảo title đã được cập nhật
                    article.setContent(content);
                    article.setUrl(articleUrl);
                    article.setPublishedAt(publishedAt);
                    article.setCategory(category);
                    article.setUrlImage(urlImage); // Lưu urlImage
                    article.setUrlVideo(urlVideo); // Lưu urlVideo
                    articleRepository.save(article);
                }
            }
        }
    }
}
