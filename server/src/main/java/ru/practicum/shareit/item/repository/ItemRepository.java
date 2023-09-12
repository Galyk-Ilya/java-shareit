package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "FROM Item it " +
            "WHERE LOWER (it.name) LIKE LOWER (concat('%',:text,'%')) " +
            "OR LOWER (it.description) LIKE LOWER (concat('%',:text,'%')) " +
            "AND it.available = TRUE")
    Page<Item> findItemsByText(@Param("text") String text, Pageable pageable);

    Page<Item> findByOwner(User owner, Pageable pageable);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> ids);
}