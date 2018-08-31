package studio.greeks.books.crawler.demo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.books.util.Request;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BiqudaoCrawler {
    public static void main(String[] args) throws Exception {
        Document document = Request.get("https://www.biqudao.com/bqge167000/");
        FileWriter writer = new FileWriter("异世之万界召唤系统_3.txt");
        Elements elements = document.select("#list dl dd a");
        List<Get> gets = new ArrayList<Get>();
        int i = 0;
        for (Element element : elements) {
            if(i<12){
                i ++;
                continue;
            }
            gets.add(new Get(element.absUrl("href")));
            i ++;
        }

        ExecutorService service = Executors.newFixedThreadPool(50);
        List<Future<Document>> futures = service.invokeAll(gets);
        for (Future<Document> future : futures) {
            Document chapter = future.get();
            writer.append(chapter.select(".bookname h1").text()).append('\n');
            String content = chapter.select("#content").html();
            content = content.replaceAll("<br>","\n").replaceAll("&nbsp;","");
            writer.append(content).append('\n');
        }
        service.shutdown();
        writer.close();
    }
}

class Get implements Callable<Document>{
    private String url;

    public Get(String url) {
        this.url = url;
    }

    public Document call() throws Exception {
        return Request.get(url);
    }
}
