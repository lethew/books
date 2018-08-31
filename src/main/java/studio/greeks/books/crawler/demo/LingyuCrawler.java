package studio.greeks.books.crawler.demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.util.Request;

import java.io.FileWriter;
import java.io.IOException;

public class LingyuCrawler {
    public static void main(String[] args) throws IOException, InterruptedException {
        Document document = Request.get("https://www.lingyu.org/wjsw/70/70303/");
        FileWriter writer = new FileWriter("玄幻之无敌熊孩子.txt");
        Elements elements = document.select(".ml_list ul li a");
        for (Element element : elements) {
            Document chapter = Request.get(element.absUrl("href"));
            writer.append(chapter.select("#nr_content .nr_title h3").text()).append('\n');
            String content = chapter.select("#nr_content .novelcontent p").html();
            content = content.replace("【  】，，，！","");
            content = content.replaceAll("<br>","\n").replaceAll("&nbsp;","");
            content = content.replaceAll("  ，。\n" +
                    "    ♂领♂域♂文♂学♂*♂www.li♂ng♂yu.or♂g","");
            writer.append(content);
            Thread.sleep(1000);
        }
        writer.close();
    }
}
