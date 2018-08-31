package studio.greeks.books.crawler.demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import studio.greeks.books.util.Request;

import java.io.FileWriter;
import java.io.IOException;

public class BixiawenxueCrawler {
    public static void main(String[] args) throws IOException {
        FileWriter writer = new FileWriter("玄幻之无敌熊孩子.txt");
        String start = "http://www.bixiawenxue.org/book/1334/589835.html";
        do {
            Document document = Request.get(start);
            Element titleEle = document.select("#content div h1").first();
            if(titleEle.select("small").remove().text().startsWith("(1")) {
                writer.append(titleEle.text()).append('\n');
            }
            Element contentEle = document.select("#htmlContent").first();
            contentEle.select("p").remove();
            String content = contentEle.html();
            content = content.replaceAll("<br>","\n").replaceAll("&nbsp;","");
            content = content.replace("一秒记住【笔下文学网 www.bixiawenxue.org】，更新快，无弹窗，免费读！","");
            content = content.replace("-->>","");
            writer.append(content).append('\n');
            start = document.getElementById("linkNext").absUrl("href");
        }while (!start.equals("http://www.bixiawenxue.org/book/1334/"));
        writer.close();
    }
}
