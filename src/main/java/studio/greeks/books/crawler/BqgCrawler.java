package studio.greeks.books.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.entity.Chapter;
import studio.greeks.books.entity.Index;
import studio.greeks.books.util.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface BqgCrawler {
    default List<Chapter> chapters(Document document, int maxDtCount, String nid){
        List<Chapter> chapters = new ArrayList<>();

        Elements elements = document.selectFirst("#list dl").children();
        int dtCount = 0;
        int ddCount = 0;
        for (Element element : elements) {
            if(element.is("dt")){
                dtCount++;
            }
            if(element.is("dd") && dtCount>=maxDtCount){
                chapters.add(new Chapter(element.text(), element.selectFirst("a").absUrl("href"), ddCount, nid));
                ddCount++;
            }
        }

        return chapters;
    }

     default String defaultContent(Chapter chapter){
         Document document = Request.get(chapter.getUrl());
         String content = document.select("#content").html();
         content = content.replaceAll("<br>", "\n").replaceAll("&nbsp;", "");
         chapter.setLength(content.length());
         chapter.setUpdateTime(new Date());
         return content;
     }
}
