package studio.greeks.books.crawler.demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.util.Request;

import java.io.FileWriter;


public class Dus88Crawler {
    public static void main(String[] args) throws Exception {
        Document document = Request.get("https://www.88dus.com/xiaoshuo/84/84251/");
        FileWriter writer = new FileWriter("都市最强装逼系统.txt");
        Elements elements = document.select(".mulu ul li a");
        for (Element element : elements) {
            Document chapter = Request.get(element.absUrl("href"));
            if(chapter != null) {
                writer.append(chapter.select("h1").text()).append('\n');
                String content = chapter.select(".yd_text2").html();
                content = content.replaceAll("<br>", "\n").replaceAll("&nbsp;", "");
                writer.append(content).append('\n');
            }else{
                writer.append("#####本章节信息缺失#####");
            }
        }
        writer.close();
    }
}
