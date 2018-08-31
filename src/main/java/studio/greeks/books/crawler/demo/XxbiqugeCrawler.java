package studio.greeks.books.crawler.demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.util.Request;

import java.io.FileWriter;
import java.io.IOException;

public class XxbiqugeCrawler {
    public static void main(String[] args) throws IOException {
        Document document = Request.get("https://www.xxbiquge.com/9_9933/");
        FileWriter writer = new FileWriter("逆天邪神.txt");
        Elements elements = document.select("#list dl dd a");
        for (Element element : elements) {
            Document chapter = Request.get(element.absUrl("href"));
            writer.append(chapter.select(".bookname h1").text()).append('\n');
            String content = chapter.select("#content").html();
            content = content.replaceAll("<br>","\n").replaceAll("&nbsp;","");
            writer.append(content).append('\n');
        }
        writer.close();
    }
}
