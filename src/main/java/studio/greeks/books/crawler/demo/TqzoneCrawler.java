package studio.greeks.books.crawler.demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.util.Request;

import java.io.FileWriter;
import java.io.IOException;

public class TqzoneCrawler {

    public static void main(String[] args) throws IOException {
        Document document = Request.get("http://www.3qzone.com/27_27085/");
        FileWriter writer = new FileWriter("系统之全能小神农.txt");
        Elements elements = document.select("#list dl dd a");
        for (Element element : elements) {
            Document chapter = Request.get(element.absUrl("href"));
            String content = chapter.select("#content").html();
            content = content.replace("一秒记住【3Q中文网 www.3qzone.com】，精彩小说无弹窗免费阅读！","");
            content = content.replaceAll("<br>","\n").replaceAll("&nbsp;","");
            writer.append(content);
        }
        writer.close();
    }
}
