package studio.greeks.books.util;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public final class Request {
    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);
    private static final int MAX_TRY_TIMES = 30;
    private static final int DEFAULT_TIME_OUT = 10000;
    private static final long DEFAULT_SLEEP_TIMES = 500;

    public static Document get(String url) {
        return get(url, null, null);
    }

    public static Document get(String url, Map<String, String> headers, Map<String, String> cookies){
        LOGGER.debug("GET:{}",url);
        Connection connect = Jsoup.connect(url);

        if(null != headers && !headers.isEmpty()) {
            LOGGER.debug("Headers:{}",headers);
            connect.headers(headers);
        }

        if(null != cookies && !cookies.isEmpty()) {
            LOGGER.debug("Cookies:{}",cookies);
            connect.cookies(cookies);
        }
        return doRequest(connect, "GET");
    }
    public static Document post(String url, Map<String, String> headers, Map<String, String> cookies, Map<String, String> data){
        LOGGER.debug("GET:{}",url);
        Connection connect = Jsoup.connect(url);

        if(null != headers && !headers.isEmpty()) {
            LOGGER.debug("Headers:{}",headers);
            connect.headers(headers);
        }

        if(null != cookies && !cookies.isEmpty()) {
            LOGGER.debug("Cookies:{}",cookies);
            connect.cookies(cookies);
        }

        if(null != data && !data.isEmpty()) {
            LOGGER.debug("Data:{}", data);
            connect.data(data);
        }
        return doRequest(connect, "POST");
    }

    private static Document doRequest(Connection connection, String type){
        for (int i = 0; i < MAX_TRY_TIMES; i++) {
            try {
                switch (type){
                    case "POST":
                        return connection.timeout(DEFAULT_TIME_OUT).post();
                    case "GET":
                        return connection.timeout(DEFAULT_TIME_OUT).get();
                }
            } catch (Exception e) {
                if(e instanceof HttpStatusException) {
                    LOGGER.error(String.format("Msg:%s,URL:%s Status:%s", e.getMessage(),((HttpStatusException) e).getUrl(), ((HttpStatusException) e).getStatusCode()), e);
                }
                LOGGER.debug("Try GET:{}"+connection.request().url());
                try {
                    Thread.sleep(DEFAULT_SLEEP_TIMES);
                } catch (InterruptedException e1) {
                    LOGGER.error(e1.getMessage(),e1);
                }
            }
        }
        return null;
    }
}
