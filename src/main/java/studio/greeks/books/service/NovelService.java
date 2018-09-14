package studio.greeks.books.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import studio.greeks.books.crawler.Crawler;
import studio.greeks.books.entity.Chapter;
import studio.greeks.books.entity.Index;
import studio.greeks.books.repository.ChapterRepository;
import studio.greeks.books.repository.IndexRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
public class NovelService {
    @Autowired private IndexRepository indexRepository;
    @Autowired private ChapterRepository chapterRepository;
    public List<Index> findByName(String name){
        return indexRepository.findAllByName(name);
    }
    public Index findById(String id){
        return indexRepository.findById(id).get();
    }
    public List<Chapter> findChappterById(String id){
        Chapter chapter = new Chapter();
        chapter.setNid(id);
        return chapterRepository.findAll(Example.of(chapter),Sort.by("rank"));
    }

    public Chapter findChapterById(String id){
        Chapter chapter = chapterRepository.findById(id).get();
        Index index = findById(chapter.getNid());
        if(chapter.getLocalPath()==null) {
            Crawler.doGetContent(index, chapter);
        }
        return chapterRepository.save(chapter);
    }

    public String content(Chapter chapter){
        StringBuilder builder = new StringBuilder();
        if(chapter.getLocalPath()!=null) {
            try {
                FileReader reader = new FileReader(chapter.getLocalPath());
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = null;
                while ((line=bufferedReader.readLine())!=null){
                    builder.append(line).append("<br>");
                }
                reader.close();
                if(builder.length()==0){
                    return content(findChapterById(chapter.getId()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
