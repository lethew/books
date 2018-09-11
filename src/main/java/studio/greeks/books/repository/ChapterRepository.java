package studio.greeks.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import studio.greeks.books.entity.Chapter;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, String> {
    @Query("Select c from Chapter c where c.url=?1")
    Chapter findByUrl(String url);

    @Query("Select count(c) from Chapter c where c.nid=?1")
     int countAllByNid(String nid);
}
