package studio.greeks.books.crawler;

import studio.greeks.books.crawler.impl.*;

import java.net.URL;

public enum Source {
    _31xs_net("www.31xs.net", new _31xsNetCrawler(), new String[]{"www.31xs.com"}, Frequency.day, false),
    _88dus_com("www.88dus.com", new _88dusComCrawler(),null, Frequency.day, false),
    bixiawenxue_org("www.bixiawenxue.org", new BixiawenxueOrgCrawler(),null, Frequency.day, false),
    biquge_cc("www.biquge.cc", new BiqugeCcCrawler(),null, Frequency.hour, false),
    bequge_com("www.bequge.com", new BequgeComCrawler(),null, Frequency.hour, false);

    private String domain;
    private Crawler crawler;
    private String[] alisDomain;
    private Frequency frequency;
    private boolean isGenuine;

    Source(String domain, Crawler crawler, String[] alisDomain, Frequency frequency, boolean isGenuine) {
        this.domain = domain;
        this.crawler = crawler;
        this.alisDomain = alisDomain;
        this.frequency = frequency;
        this.isGenuine = isGenuine;
    }

    public static Source get(URL url){
        String domain = url.getHost();
        for (Source source : values()) {
            if(source.domain.equals(domain)){
                return source;
            }
            if(source.alisDomain != null){
                for (String s : source.alisDomain) {
                    if(s.equals(domain)){
                        return source;
                    }
                }
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

    public String[] getAlisDomain() {
        return alisDomain;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public boolean isGenuine() {
        return isGenuine;
    }

    public enum Frequency{
        hour(1000*60*60), day(24*hour.time), week(7*day.time);

        private long time;

        Frequency(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }
    }
}
