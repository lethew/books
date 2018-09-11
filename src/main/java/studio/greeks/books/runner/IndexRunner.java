package studio.greeks.books.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import studio.greeks.books.crawler.Source;
import studio.greeks.books.repository.IndexRepository;
import studio.greeks.books.service.QueueService;

import java.util.Timer;

@Component
public class IndexRunner implements ApplicationRunner {

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private QueueService queueService;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        for (Source.Frequency frequency : Source.Frequency.values()) {
            new Timer().schedule(new IndexTask(indexRepository, queueService, frequency), 0, frequency.getTime());
        }
    }
}
