import java.net.MalformedURLException;
import java.net.URL;

public class FormatTest {
    public static void main(String[] args) throws MalformedURLException {
        System.out.println(String.format("chapter.%4d",0+1).replaceAll(" ", "0"));
        URL url = new URL("http://www.runoob.com/redis/redis-lists.html");
        System.out.println(url.getHost());
    }
}
