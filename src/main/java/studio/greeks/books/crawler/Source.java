package studio.greeks.books.crawler;

import studio.greeks.books.crawler.impl._31xsNetCrawler;
import studio.greeks.books.crawler.impl._88dusComCrawler;

import java.net.URL;

public enum Source {
    _31xs_net("www.31.xs.net", new _31xsNetCrawler()),
    _88dus_com("www.88dus.com", new _88dusComCrawler());
    private String domain;
    private Crawler crawler;

    Source(String domain, Crawler crawler) {
        this.domain = domain;
        this.crawler = crawler;
    }

    public static Source get(URL url){
        String domain = url.getHost();
        for (Source source : values()) {
            if(source.domain.equals(domain)){
                return source;
            }
        }
        return null;
    }

    public String getDomain() {
        return domain;
    }

    public Crawler getCrawler() {
        return crawler;
    }
}
