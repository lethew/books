package studio.greeks.books.crawler.demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.util.Request;

import java.io.FileWriter;

public class Xs31Crawler {
    public static void main(String[] args) throws Exception {
        Document document = Request.get("http://www.31xs.net/0/720/");
        FileWriter writer = new FileWriter("最强反套路系统.txt");
        if(document != null){
            Elements elements = document.select("#list dl dd a");
            int i = 0;
            for (Element element : elements) {
                if(i<11){
                    i ++;
                    continue;
                }
                Document chapter = Request.get(element.absUrl("href"));
                writer.append(chapter.select(".bookname h1").text()).append('\n');
                String content = chapter.select("#content").html();
                content = content.replaceAll("</p>","\n").replaceAll("&nbsp;","").replaceAll("<p>","");
                writer.append(content).append('\n');
                i ++;
            }
        }
        writer.close();
    }
}
