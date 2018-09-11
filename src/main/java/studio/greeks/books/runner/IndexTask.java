package studio.greeks.books.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.greeks.books.crawler.Source;
import studio.greeks.books.repository.IndexRepository;
import studio.greeks.books.service.QueueService;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IndexTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(IndexTask.class);

    private IndexRepository indexRepository;

    private QueueService queueService;

    private Source.Frequency frequency;

    IndexTask(IndexRepository indexRepository, QueueService queueService, Source.Frequency frequency) {
        this.indexRepository = indexRepository;
        this.queueService = queueService;
        this.frequency = frequency;
    }

    @Override
    public void run() {
        ExecutorService service = Executors.newFixedThreadPool(Source.values().length);
        List<IndexScanner> scanners = new ArrayList<>();
        for (Source source : Source.values()) {
            if(source.getFrequency() == frequency) {
                IndexScanner scanner = new IndexScanner(indexRepository, source, queueService);
                scanners.add(scanner);
            }
        }
        try {
            service.invokeAll(scanners);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        service.shutdown();
    }
}
