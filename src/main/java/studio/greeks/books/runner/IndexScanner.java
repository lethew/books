package studio.greeks.books.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.greeks.books.crawler.Source;
import studio.greeks.books.entity.Index;
import studio.greeks.books.repository.IndexRepository;
import studio.greeks.books.service.QueueService;

import java.util.List;
import java.util.concurrent.Callable;

public class IndexScanner implements Callable<String> {
    private final Logger logger = LoggerFactory.getLogger(IndexScanner.class);
    private IndexRepository indexRepository;
    private Source source;
    private QueueService queueService;

    IndexScanner(IndexRepository indexRepository, Source source, QueueService queueService) {
        this.indexRepository = indexRepository;
        this.source = source;
        this.queueService = queueService;
    }

    @Override
    public String call(){
        List<Index> indices = source.getCrawler().indexes();
        Integer newCount = 0;
        Integer updateCount = 0;
        for (Index index1 : indices) {
            Index existIndex = indexRepository.findByIndexUrl(index1.getIndexUrl());
            if (existIndex != null) {
                if (!index1.getLastUpdate().equals(existIndex.getLastUpdate())) {
                    index1.setId(existIndex.getId());
                    indexRepository.save(index1);
                    queueService.push(QueueService.Name.UPDATE_NOVEL_QUEUE, index1.getId());
                    updateCount++;
                }
            } else {
                Index save = indexRepository.save(index1);
                queueService.push(QueueService.Name.NEW_NOVEL_QUEUE, save.getId());
                newCount++;
            }
        }
        logger.debug("站点{}:\n新增小说 {} 本\n更新小说 {} 本", source.getDomain(), newCount, updateCount);
        return "SUCCESS";
    }
}
