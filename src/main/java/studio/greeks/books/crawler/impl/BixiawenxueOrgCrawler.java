package studio.greeks.books.crawler.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.crawler.AbstractNavGetter;
import studio.greeks.books.crawler.Crawler;
import studio.greeks.books.entity.Chapter;
import studio.greeks.books.entity.Index;
import studio.greeks.books.util.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BixiawenxueOrgCrawler implements Crawler {
    private static final String ROOT = "http://www.bixiawenxue.org/";

    public static void main(String[] args) {
        List<Index> indexes = new BixiawenxueOrgCrawler().indexes();
        System.out.println(indexes);
    }
    @Override
    public List<Index> indexes() {
        Document document = Request.get(ROOT);
        Elements navElements = document.select("#nav-header ul li a");
        List<NavGetter> getters = new ArrayList<>();
        for (int i = 0; i < navElements.size()-1; i++) {
            Element navElement = navElements.get(i);
            if(navElement.absUrl("href").startsWith("http://www.bixiawenxue.org/fenlei")) {
                NavGetter getter = new NavGetter(navElement);
                getters.add(getter);
            }
        }

        return submit(getters);
    }

    @Override
    public List<Chapter> chapters(Index index) {
        Document document = Request.get(index.getIndexUrl());

        Element coverEle = document.selectFirst(".img-thumbnail");
        index.setCoverUrl(coverEle.absUrl("src"));

        downloadCover(index);

        Element descEle = document.getElementById("bookIntro");
        descEle.select("img").remove();
        index.setDescription(descEle.html().replaceAll("<br>", "\n").replaceAll("&nbsp;", ""));

        Element wordLengthEle = document.selectFirst(".booktag .blue");
        index.setLength(Long.parseLong(wordLengthEle.text().replace("字数：","").replace("万","0000").replace("千","000")));

        index.setUpdateTime(new Date());

        Elements elements = document.select("#list-chapterAll dl dd a");
        List<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            chapters.add(new Chapter(elements.get(i).text(), elements.get(i).absUrl("href"), i, index.getId()));
        }
        return chapters;
    }

    @Override
    public String content(Chapter chapter) {
        StringBuilder content = new StringBuilder();
        String start = chapter.getUrl();
        do {
            Document document = Request.get(start);
            Element contentEle = document.select("#htmlContent").first();
            contentEle.select("p").remove();
            String slice = contentEle.html();
            slice = slice.replaceAll("<br>","\n").replaceAll("&nbsp;","");
            slice = slice.replace("一秒记住【笔下文学网 www.bixiawenxue.org】，更新快，无弹窗，免费读！","");
            slice = slice.replace("-->>","");
            content.append(slice).append('\n');
            start = document.getElementById("linkNext").absUrl("href");
        }while (!start.equals(chapter.getUrl().replace(".html","")));
        return content.toString();
    }

    class NavGetter extends AbstractNavGetter {
        protected NavGetter(Element navElement) {
            super(navElement);
        }

        @Override
        protected List<Index> doGet() {
            String type = navElement.text();
            String url = navElement.absUrl("href");
            List<Index> indices = new ArrayList<>();
            while (url != null) {
                Document indexDocument = Request.get(url);
                Elements indexElements = indexDocument.selectFirst(".col-md-8").select("tr");
                for (Element indexElement : indexElements) {
                    Elements tdElements = indexElement.select("td");
                    if(tdElements != null && !tdElements.isEmpty()){
                        Index index = new Index();
                        index.setName(tdElements.get(0).text());
                        index.setIndexUrl(tdElements.get(0).selectFirst("a").absUrl("href"));
                        index.setType(type);
                        index.setAuthor(tdElements.get(2).text());
                        index.setLastUpdate(tdElements.get(3).text());
                        index.setStatus(tdElements.get(4).text());
                        indices.add(index);
                    }
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
