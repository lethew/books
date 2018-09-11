package studio.greeks.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import studio.greeks.books.entity.Index;

public interface IndexRepository extends JpaRepository<Index, String> {
    @Query(value = "select idx from Index idx where idx.indexUrl=?1")
    Index findByIndexUrl(String indexUrl);
    @Query(value = "select idx from Index idx where idx.id=?1")
    Index selectById(String id);
}
