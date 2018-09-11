package studio.greeks.books.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.greeks.books.crawler.Crawler;
import studio.greeks.books.entity.Chapter;
import studio.greeks.books.entity.Index;
import studio.greeks.books.repository.ChapterRepository;
import studio.greeks.books.repository.IndexRepository;
import studio.greeks.books.service.QueueService;

import java.util.List;

public class ChapterNotifyScanner implements Runnable{
    private final Logger logger = LoggerFactory.getLogger(ChapterNotifyScanner.class);

    private IndexRepository indexRepository;

    private ChapterRepository chapterRepository;

    private QueueService queueService;

    ChapterNotifyScanner(IndexRepository indexRepository, ChapterRepository chapterRepository, QueueService queueService) {
        this.indexRepository = indexRepository;
        this.chapterRepository = chapterRepository;
        this.queueService = queueService;
    }

    @Override
    public void run() {
        while (true){
            try {
                String indexId = queueService.pop(QueueService.Name.UPDATE_NOVEL_QUEUE);
                if(indexId == null){
                    Thread.sleep(1000*30);
                }else {
                    Index index = indexRepository.selectById(indexId);
                    if(index!=null) {
                        List<Chapter> chapters = Crawler.doGetChapters(index);
                        if (chapters != null) {
                            indexRepository.save(index);
                            for (Chapter chapter : chapters) {
                                Chapter temp = chapterRepository.findByUrl(chapter.getUrl());
                                if (temp != null) {
                                    chapterRepository.save(chapter);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
