package com.nmit.canteen.repository;

import com.nmit.canteen.model.MenuItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// Spring Data REST: GET /data/items, /data/items/search/findByCategory?category=Snacks
@RepositoryRestResource(path = "items", collectionResourceRel = "items")
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategory(@Param("category") String category);
    List<MenuItem> findByVegTrue();
    List<MenuItem> findByPriceLessThanEqual(@Param("max") double max);
    boolean existsByName(String name);
}
