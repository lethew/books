package studio.greeks.books.service;

import org.springframework.stereotype.Service;

@Service
public class QueueService {
    public enum Name{
        NEW_NOVEL_QUEUE("new_novel_queue"),
        UPDATE_NOVEL_QUEUE("update_novel_queue");

        String name;

        Name(String name) {
            this.name = name;
        }
    }

    public void addToLast(Name name, String msg){

    }

    public String pollFromHead(Name name){
        return null;
    }
}
