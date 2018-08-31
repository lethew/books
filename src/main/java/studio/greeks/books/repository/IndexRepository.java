package studio.greeks.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.greeks.books.entity.Index;

public interface IndexRepository extends JpaRepository<Index, String> {
}
