package studio.greeks.books;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import studio.greeks.books.crawler.Source;
import studio.greeks.books.entity.Index;
import studio.greeks.books.repository.ChapterRepository;
import studio.greeks.books.repository.IndexRepository;
import studio.greeks.books.service.QueueService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class IndexApplicationRunner implements ApplicationRunner {

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private QueueService queueService;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(Source.values().length);
        List<IndexScanner> scanners = new ArrayList<>();

        for (Source source : Source.values()) {
            IndexScanner scanner = new IndexScanner(indexRepository, source);
            scanners.add(scanner);
        }
        service.invokeAll(scanners);
        service.shutdown();
    }

    class IndexScanner implements Callable<String> {
        private final Logger logger = LoggerFactory.getLogger(IndexScanner.class);
        private IndexRepository indexRepository;
        private Source source;

        IndexScanner(IndexRepository indexRepository, Source source) {
            this.indexRepository = indexRepository;
            this.source = source;
        }

        @Override
        public String call() throws Exception {
            List<Index> indices = source.getCrawler().indexes();
            Index index = new Index();
            Integer newCount = 0;
            Integer updateCount = 0;
            for (Index index1 : indices) {
                index.setIndexUrl(index1.getIndexUrl());
                try {
                    Index existIndex = indexRepository.findOne(Example.of(index)).get();
                    if (!index1.getLastUpdate().equals(existIndex.getLastUpdate())) {
                        index1.setId(existIndex.getId());
                        indexRepository.save(index1);
                        queueService.addToLast(QueueService.Name.UPDATE_NOVEL_QUEUE, index1.getId());
                        updateCount++;
                    }
                } catch (NoSuchElementException e) {
                    Index save = indexRepository.save(index1);
                    queueService.addToLast(QueueService.Name.NEW_NOVEL_QUEUE, save.getId());
                    newCount++;
                }
            }
            logger.debug("站点{}:\n新增小说 {} 本\n更新小说 {} 本", source.getDomain(), newCount, updateCount);
            return "SUCCESS";
        }
    }
}
