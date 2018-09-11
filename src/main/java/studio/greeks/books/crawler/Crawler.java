package studio.greeks.books.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.greeks.books.entity.Chapter;
import studio.greeks.books.entity.Index;
import studio.greeks.books.util.PictureDownloader;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public interface Crawler {
    List<Index> indexes();

    List<Chapter> chapters(Index index);

    String content(Chapter chapter);


    Logger logger = LoggerFactory.getLogger(Crawler.class);
    Random random = new Random();

    static void doGetContent(Index index, Chapter chapter) {
        try {
            Source source = Source.get(new URL(index.getIndexUrl()));
            if (null != source) {
                Crawler crawler = source.getCrawler();
                File localDir = new File(index.getLocalDir());
                if (!localDir.isDirectory()) {
                    localDir.mkdirs();
                }

                String fileName = String.format("chapter.%5d", chapter.getRank()).replaceAll(" ", "0");
                File saveFile = new File(String.format("%s/%s", index.getLocalDir(), fileName));
                FileWriter writer = new FileWriter(saveFile);
                writer.append(chapter.getName()).append('\n');
                writer.append(crawler.content(chapter));
                chapter.setLocalPath(saveFile.getAbsolutePath());
                chapter.setHasCache(Boolean.TRUE);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    static List<Chapter> doGetChapters(Index index) {
        try {
            logger.info(index.toString());
            Source source = Source.get(new URL(index.getIndexUrl()));
            if (null != source) {
                Crawler crawler = source.getCrawler();
                List<Chapter> chapters = crawler.chapters(index);
                return chapters;
            } else {
                logger.error("无法匹配到相应的爬虫程序，地址：{}", index.getIndexUrl());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    default String path(String id) {
        return String.format("D:/book/%d/%d/%s", random.nextInt(100), random.nextInt(100), id);
    }

    default void downloadCover(Index index) {
        index.setLocalDir(path(index.getId()));
        if (null != index.getCoverUrl()) {
            boolean hasDownload = false;
            File pic = null;
            for (int i = 0; i < 30; i++) {
                try {
                    pic = PictureDownloader.download(index.getCoverUrl(), index.getLocalDir());
                    hasDownload = true;
                    break;
                }catch (Exception e){
                }
            }
            if( hasDownload && pic != null) {
                index.setCoverPath(pic.getAbsolutePath());
            }
        }
    }

    default List<Index> submit(List<? extends AbstractNavGetter> getters) {
        ExecutorService service = Executors.newFixedThreadPool(getters.size());
        List<Index> indices = new ArrayList<>();
        try {
            List<Future<List<Index>>> futures = service.invokeAll(getters);
            for (Future<List<Index>> future : futures) {
                indices.addAll(future.get());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return indices;
    }
}
