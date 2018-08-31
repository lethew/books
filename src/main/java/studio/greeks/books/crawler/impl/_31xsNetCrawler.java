package studio.greeks.books.crawler.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.crawler.Crawler;
import studio.greeks.books.crawler.NavGetter;
import studio.greeks.books.entity.Chapter;
import studio.greeks.books.entity.Index;
import studio.greeks.books.util.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class _31xsNetCrawler implements Crawler {
    private static final String ROOT = "http://www.31xs.net/";
    @Override
    public List<Index> indexes() {
        Document document = Request.get(ROOT);
        Elements navElements = document.select("#wrapper .nav ul li a");
        List<Index> indices = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(navElements.size()-2);
        List<NavGetter> getters = new ArrayList<>();
        for (int i = 1; i < navElements.size()-1; i++) {
            Element navElement = navElements.get(i);
            NavGetter getter = new NavGetter(navElement);
            getters.add(getter);
        }
        submit(indices, service, getters);
        service.shutdown();
        return indices;
    }



    @Override
    public List<Chapter> chapters(Index index) {
        Document document = Request.get(index.getIndexUrl());

        Element coverEle = document.getElementById("fmimg");
        index.setCoverUrl(coverEle.children().first().absUrl("src"));
        if(coverEle.select("span").last().attr("class").equals("a")){
            index.setStatus("已完结");
        }else{
            index.setStatus("连载中");
        }

        downloadCover(index);

        Element infoEle = document.getElementById("info");
        index.setAuthor(infoEle.select("p").first().text().replace("作    者：",""));

        Element descEle = document.getElementById("intro");
        index.setDescription(descEle.html().replaceAll("</p>","\n").replaceAll("&nbsp;","").replaceAll("<p>",""));

        index.setUpdateTime(new Date());


        Elements elements = document.select("#list dl").first().children();
        List<Chapter> chapters = new ArrayList<>();
        int i = 0;
        int dtCount = 0;
        for (Element element : elements) {
            if(element.is("dt")){
                dtCount++;
            }
            if(dtCount >= 2){
                String url = element.select("a").first().absUrl("href");
                if(url.startsWith("http")) {
                    chapters.add(new Chapter(element.text(), url, i, index.getId()));
                    i++;
                }
            }
        }
        return chapters;
    }

    @Override
    public String content(Chapter chapter) {
        Document document = Request.get(chapter.getUrl());
        String content = document.select("#content").html();
        content = content.replaceAll("</p>","\n").replaceAll("&nbsp;","").replaceAll("<p>","");
        chapter.setLength(content.length());
        chapter.setUpdateTime(new Date());
        return content;
    }

    class NavGetter implements studio.greeks.books.crawler.NavGetter {
        private Element navElement;

        NavGetter(Element navElement) {
            this.navElement = navElement;
        }

        @Override
        public List<Index> call() throws Exception {
            String type = navElement.text();
            String url = navElement.absUrl("href");
            List<Index> indices = new ArrayList<>();
            while (url != null) {
                Document indexDocument = Request.get(url);
                Elements indexElements = indexDocument.select("tr");
                for (Element indexElement : indexElements) {
                    Element nameElement = indexElement.selectFirst("td a");
                    if(nameElement != null) {
                        Index index = new Index(nameElement.text(), nameElement.absUrl("href"), type);
                        Element lastElement = indexElement.selectFirst(".even");
                        lastElement.select("a").remove();
                        index.setLastUpdate(lastElement.text());
                        index.setAuthor(indexElement.select(".odd").last().text());
                        index.setLength(Long.parseLong(indexElement.select(".center").last().text().replace("千字","000")));
                        indices.add(index);
                    }
                }
                Elements pageElements = indexDocument.select("#content .bottem1 a");
                if(pageElements.last().text().equals("下一页")){
                    url = pageElements.last().absUrl("href");
                }else{
                    url = null;
                }
            }
            return indices;
        }
    }
}
