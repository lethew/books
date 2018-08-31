package studio.greeks.books.crawler.demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.util.Request;

import java.io.FileWriter;

/**
 * HTTP error fetching URL 问题未解决
 */
public class Biquge5200Crawler {
    public static void main(String[] args) throws Exception {
        Document document = Request.get("https://www.biquge5200.cc/76_76141/");
        FileWriter writer = new FileWriter("极品捉鬼系统.txt");
        Elements elements = document.select("#list dl dd a");
        int i = 0;
        for (Element element : elements) {
            if(i<9){
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
        writer.close();
    }
}
