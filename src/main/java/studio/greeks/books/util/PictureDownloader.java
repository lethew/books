package studio.greeks.books.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

public class PictureDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PictureDownloader.class);

    public static File download(String url, String folder) {
        try {
            LOGGER.debug("Download picture from:{}", url);
            File dir = new File(folder);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }

            URL target = new URL(url);
            String[] split = target.getFile().split("/");
            File outFile = new File(folder + "/" + split[split.length - 1]);
            LOGGER.debug("Save picture to:{}", outFile.getAbsolutePath());
            FileOutputStream outputStream = new FileOutputStream(outFile);
            URLConnection urlConnection = target.openConnection();
            urlConnection.setConnectTimeout(10000);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] bytes = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, count);
            }
            inputStream.close();
            outputStream.close();

            return outFile;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
