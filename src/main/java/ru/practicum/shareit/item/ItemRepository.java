package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Item getItemById(Integer id);

    List<Item> findAllByOwnerId(Integer userId);

    @Query("select i " +
            "from Item as i " +
            "join i.owner as u " +
            "where " +
            "i.available = TRUE and (" +
            "lower(i.name) like lower(concat('%', :query, '%')) or " +
            "lower(i.description) like lower(concat('%', :query, '%')))")
    List<Item> findAvailableItemsByNameOrDescription(String query);
}
