package studio.greeks.books.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import studio.greeks.books.repository.ChapterRepository;
import studio.greeks.books.repository.IndexRepository;
import studio.greeks.books.service.QueueService;

@Component
public class ChapterRunner implements ApplicationRunner {

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private QueueService queueService;

    private static final int NEW_THREAD_SIZE = 20;

    private static final int NOTIFY_THREAD_SIZE = 5;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        for (int i = 0; i < NEW_THREAD_SIZE; i++) {
            new Thread(new ChapterNewScanner(indexRepository, chapterRepository, queueService)).start();
        }

        for (int i = 0; i < NOTIFY_THREAD_SIZE; i++) {
            new Thread(new ChapterNotifyScanner(indexRepository, chapterRepository, queueService)).start();
        }
    }




}
