package studio.greeks.books.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
    @GetMapping("/novel/index.html")
    public ModelAndView novelIndex(){
        ModelAndView view = new ModelAndView("index");

        return view;
    }
}
