package studio.greeks.books.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/novel")
public class NovelController {
    @GetMapping("/test")
    public String test(){
        return "hello, i am a test!111";
    }
}
