package studio.greeks.books.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class QueueService {
    private Logger logger = LoggerFactory.getLogger(QueueService.class);
    public enum Name{
        NEW_NOVEL_QUEUE("new_novel_queue"),
        UPDATE_NOVEL_QUEUE("update_novel_queue");

        String name;

        Name(String name) {
            this.name = name;
        }
    }

    @Autowired
    private RedisTemplate redisTemplate;

    private BoundListOperations<String, String> listOperations;

    public void push(Name name, String msg){
        listOperations = redisTemplate.boundListOps(name.name);
        Long aLong = listOperations.rightPush(msg);
        }
    public String pop(Name name){
        listOperations = redisTemplate.boundListOps(name.name);
        return listOperations.leftPop();
    }
}
