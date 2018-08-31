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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class _88dusComCrawler implements Crawler {
    private static final String ROOT = "https://www.88dus.com/";
    @Override
    public List<Index> indexes() {
        Document document = Request.get(ROOT);
        Elements navElements = document.select(".nav_l li a");
        List<Index> indices = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(navElements.size()-1);
        List<NavGetter> getters = new ArrayList<>();
        for (int i = 1; i < navElements.size(); i++) {
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

        Element coverEle = document.selectFirst(".jieshao .lf img");
        index.setCoverUrl(coverEle.absUrl("src"));

        downloadCover(index);

        Element descEle = document.selectFirst(".intro");
        index.setDescription(descEle.html().replaceAll("<br>", "\n").replaceAll("&nbsp;", ""));

        index.setUpdateTime(new Date());

        Elements elements = document.select(".mulu ul li a");
        List<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            chapters.add(new Chapter(elements.get(i).text(), elements.get(i).absUrl("href"), i, index.getId()));
        }
        return chapters;

    }

    @Override
    public String content(Chapter chapter) {
        Document document = Request.get(chapter.getUrl());
        String content = document.select(".yd_text2").html();
        content = content.replaceAll("<br>", "\n").replaceAll("&nbsp;", "");
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
                Elements indexElements = indexDocument.select(".booklist ul li");
                for (int j = 1; j < indexElements.size(); j++) {
                    Element currentElement = indexElements.get(j);
                    Index index = new Index(currentElement.select(".sm").text(), currentElement.select(".sm a").last().absUrl("href"), type);
                    index.setStatus(currentElement.select(".zt").text());
                    index.setLength(Long.parseLong(currentElement.select(".zs").text().replace("字","")));
                    index.setAuthor(currentElement.select(".zz").text());
                    index.setLastUpdate(currentElement.select(".sj").text());
                    indices.add(index);
                }

                Elements pagelink = indexDocument.select("#pagelink .next");
                if(pagelink.size()==1){
                    url = pagelink.last().absUrl("href");
                }else{
                    url = null;
                }
            }
            return indices;
        }
    }
}
