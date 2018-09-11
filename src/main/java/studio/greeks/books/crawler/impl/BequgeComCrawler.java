package studio.greeks.books.crawler.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.crawler.BqgCrawler;
import studio.greeks.books.crawler.Crawler;
import studio.greeks.books.entity.Chapter;
import studio.greeks.books.entity.Index;
import studio.greeks.books.util.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BequgeComCrawler implements Crawler, BqgCrawler {
    private static final String ROOT = "https://www.bequge.com/";
    @Override
    public List<Index> indexes() {
        Document document = Request.get(ROOT);
        Elements navElements = document.select(".nav ul li a");
        List<Index> indices = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            Element element = navElements.get(i);
            if(element!=null){
                Document temp = Request.get(element.absUrl("href"));
                if(temp!=null){
                    Elements select = temp.select("#newscontent div ul li");
                    for (Element item : select) {
                        Index index = new Index();
                        index.setName(item.select(".s2 a").text());
                        index.setIndexUrl(item.select(".s2 a").last().absUrl("href"));
                        index.setType(element.text());
                        index.setStatus("连载中");
                        index.setLastUpdate(element.select(".s3").text());
                        index.setAuthor(element.select(".s5").text());
                        indices.add(index);
                    }
                }
            }
        }
        return indices;
    }

    @Override
    public List<Chapter> chapters(Index index) {
        Document document = Request.get(index.getIndexUrl());

        Element coverEle = document.selectFirst("#fmimg img");
        index.setCoverUrl(coverEle.absUrl("src"));

        downloadCover(index);

        Element descEle = document.selectFirst("#intro");
        index.setDescription(descEle.html().replaceAll("(<p>)|(</p>)", "\n").replaceAll("&nbsp;", ""));

        index.setUpdateTime(new Date());

        return chapters(document, 1, index.getId());
    }

    @Override
    public String content(Chapter chapter) {
        return defaultContent(chapter);
    }
}
