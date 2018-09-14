package studio.greeks.books.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Request {
    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);
    private static final int MAX_TRY_TIMES = 30;
    private static final int DEFAULT_TIME_OUT = 10000;
    private static final long DEFAULT_SLEEP_TIMES = 500;
    private static SSLSocketFactory socketFactory = null;
    static {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            socketFactory = context.getSocketFactory();
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static Document get(String url) {
        return url.startsWith("http") ? get(url, null, null, null) : null;
    }

    public static Proxy createProxy(Proxy.Type type, String host, int port) {
        return new Proxy(type, InetSocketAddress.createUnresolved(host, port));
    }

    public static Document get(String url, Map<String, String> headers, Map<String, String> cookies, Proxy proxy) {
        LOGGER.debug("GET:{}", url);
        Connection connect = Jsoup.connect(url);

        if (null != headers && !headers.isEmpty()) {
            LOGGER.debug("Headers:{}", headers);
            connect.headers(headers);
        }

        if (null != cookies && !cookies.isEmpty()) {
            LOGGER.debug("Cookies:{}", cookies);
            connect.cookies(cookies);
        }
        return doRequest(connect, "GET", proxy);
    }

    public static Document post(String url, Map<String, String> headers, Map<String, String> cookies, Map<String, String> data, Proxy proxy) {
        LOGGER.debug("GET:{}", url);
        Connection connect = Jsoup.connect(url);

        if (null != headers && !headers.isEmpty()) {
            LOGGER.debug("Headers:{}", headers);
            connect.headers(headers);
        }

        if (null != cookies && !cookies.isEmpty()) {
            LOGGER.debug("Cookies:{}", cookies);
            connect.cookies(cookies);
        }

        if (null != data && !data.isEmpty()) {
            LOGGER.debug("Data:{}", data);
            connect.data(data);
        }
        return doRequest(connect, "POST", proxy);
    }

    private static Document doRequest(Connection connection, String type, Proxy proxy) {
        for (int i = 0; i < MAX_TRY_TIMES; i++) {
            try {
                switch (type) {
                    case "POST":
                        connection.request().method(Connection.Method.POST);
                        break;
                    case "GET":
                        connection.request().method(Connection.Method.GET);
                        break;
                    case "PUT":
                        connection.request().method(Connection.Method.PUT);
                        break;
                    case "DELETE":
                        connection.request().method(Connection.Method.DELETE);
                        break;
                    case "PATCH":
                        connection.request().method(Connection.Method.PATCH);
                        break;
                    case "HEAD":
                        connection.request().method(Connection.Method.HEAD);
                        break;
                    case "OPTIONS":
                        connection.request().method(Connection.Method.OPTIONS);
                        break;
                    case "TRACE":
                        connection.request().method(Connection.Method.TRACE);
                        break;
                }
                connection.timeout(DEFAULT_TIME_OUT);

                if (proxy != null) {
                    connection.proxy(proxy);
                }

                if(socketFactory != null) {
                    connection.sslSocketFactory(socketFactory);
                }

                connection.execute();

                byte[] bytes = connection.response().bodyAsBytes();
                Document parse = connection.response().parse();
                String charset = getCharset(parse);
                String content = new String(bytes, charset);
                String baseUri = parse.baseUri();
                parse = Jsoup.parse(content);
                parse.setBaseUri(baseUri);
                return parse;
            } catch (Exception e) {
                if (e instanceof HttpStatusException) {
                    LOGGER.error(String.format("Msg:%s,URL:%s Status:%s", e.getMessage(), ((HttpStatusException) e).getUrl(), ((HttpStatusException) e).getStatusCode()), e);
                }
                LOGGER.error(e.getMessage(), e);
                LOGGER.debug("Try GET:{}" + connection.request().url());
                try {
                    Thread.sleep(DEFAULT_SLEEP_TIMES);
                } catch (InterruptedException e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
        }
        return null;
    }

    private static String getCharset(Document document) {
        Elements eles = document.select("meta[http-equiv=Content-Type]");
        Iterator<Element> itor = eles.iterator();
        while (itor.hasNext())
            return matchCharset(itor.next().toString());
        return "gb2312";
    }

    private static String matchCharset(String content) {
        String chs = "gb2312";
        Pattern p = Pattern.compile("(?<=charset=)(.+)(?=\")");
        Matcher m = p.matcher(content);
        if (m.find())
            return m.group();
        return chs;
    }
}
