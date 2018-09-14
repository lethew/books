package studio.greeks.books.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import studio.greeks.books.crawler.Crawler;
import studio.greeks.books.entity.Chapter;
import studio.greeks.books.entity.Index;
import studio.greeks.books.service.NovelService;

import java.util.List;

@Controller
public class IndexController {
    @Autowired private NovelService novelService;
    @GetMapping("/novel/index.html")
    public ModelAndView novelIndex(){
        ModelAndView view = new ModelAndView("index");

        return view;
    }

    @GetMapping("/novel/list.html")
    public ModelAndView novelList(String word, Integer pageIndex){
        ModelAndView view = new ModelAndView("list");
        List<Index> byName = novelService.findByName(word);
        view.addObject("word", word);
        view.addObject("items", byName);
        return view;
    }

    @GetMapping("/novel/chapters.html")
    public ModelAndView novelChapters(String id){
        ModelAndView view = new ModelAndView("chapters");
        Index index = novelService.findById(id);
        List<Chapter> chapters = novelService.findChappterById(id);
        view.addObject("book", index);
        view.addObject("chapters", chapters);
        return view;
    }

    @GetMapping("/novel/read/{nid}/{cid}.html")
    public ModelAndView novelRead(@PathVariable String nid, @PathVariable String cid){
        ModelAndView view = new ModelAndView("read");
        Index index = novelService.findById(nid);
        Chapter chapter = novelService.findChapterById(cid);
        view.addObject("book", index);
        view.addObject("chapter", chapter);
        view.addObject("content", novelService.content(chapter));
        return view;
    }
}
