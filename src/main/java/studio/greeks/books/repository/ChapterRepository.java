package studio.greeks.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.greeks.books.entity.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, String> {
}
