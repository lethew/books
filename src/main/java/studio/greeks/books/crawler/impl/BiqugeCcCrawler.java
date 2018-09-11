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

public class BiqugeCcCrawler implements Crawler, BqgCrawler {

    private final static String NAV_URL_FORMAT = "https://www.biquge.cc/class/%d/1.html";

    @Override
    public List<Index> indexes() {
        List<Index> indices = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            Document document = Request.get(String.format(NAV_URL_FORMAT, i));
            if(document!=null){
                Elements select = document.select("#newscontent div ul li");
                for (Element element : select) {
                    Index index = new Index();
                    index.setName(element.select(".s2").text());
                    index.setIndexUrl(element.select(".s2 a").last().absUrl("href"));
                    index.setType(element.select(".s1").text().replace("[","").replace("]",""));
                    index.setStatus("连载中");
                    index.setLastUpdate(element.select(".s3").text());
                    index.setAuthor(element.select(".s4").text());
                    indices.add(index);
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
        index.setDescription(descEle.html().replaceAll("<br>", "\n").replaceAll("&nbsp;", ""));

        index.setUpdateTime(new Date());

        return chapters(document, 1, index.getId());
    }

    @Override
    public String content(Chapter chapter) {
        return defaultContent(chapter);
    }
}
