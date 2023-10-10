package com.todaysroom.map.controller;

import com.todaysroom.map.dto.NewsDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @GetMapping
    public ResponseEntity<List<NewsDto>> newsCrawling(){
        String url = "https://land.naver.com/news/breaking.naver";
        try{
            Document doc = Jsoup.connect(url).get();
            Elements articles = doc.select("div.section_headline li");

            List<NewsDto> newsList = new ArrayList<>();

            for (Element article : articles) {
                String imageUrl = article.select(".photo img[src]").attr("src");
                String newsTitle = article.select("dt > a").text();
                String newsLink = "https://land.naver.com" + article.select("dt > a").attr("href");
                String writing = article.select("dd > span.writing").text();
                String date = article.select("dd > span.date").text();
                String newsContent = article.select("dd").get(0).text();

                newsContent = newsContent.replace(writing, "").replace(date, "");

                newsList.add(new NewsDto(imageUrl, newsTitle, newsContent, newsLink, writing, date));
            }

            return new ResponseEntity<>(newsList, HttpStatus.OK);


        } catch (IOException e){
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
