package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemStorageImpl implements ItemStorage {

    private long generateId = 0;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    @Override
    public Item createItem(Item item, long userId) {
        item.setId(++generateId);
        item.setOwner(userId);
        items.put(item.getId(), item);
        final List<Item> userItems = userItemIndex.computeIfAbsent(item.getOwner(), k -> new ArrayList<>());
        userItems.add(item);
        log.info("User with id: {} added an item with id: {}", userId, item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item item, long userId, long itemId) {
        checkId(item.getId());
        Item oldItem = items.get(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getOwner() != null && item.getOwner() > 0) {
            oldItem.setOwner(item.getOwner());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        log.info("User with id: {} updated an item with id: {}", userId, itemId);
        return oldItem;
    }

    @Override
    public Item getItemById(long id) {
        checkId(id);
        log.info("Item info with id: {} received ", id);
        return items.get(id);
    }

    @Override
    public List<Item> getAllItemsByUserId(long userId) {
        log.info("Info about user items with id: {} received ", userId);
        return userItemIndex.getOrDefault(userId, List.of());
    }

    @Override
    public List<Item> getItemsByText(String text) {
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long id) {
        checkId(id);
        Item item = items.remove(id);
        userItemIndex.get(item.getOwner()).remove(item);
        log.info("Предмет с  id: {} удален ", id);
    }

    private void checkId(Long id) {
        if (id != 0 && !items.containsKey(id)) {
            log.info("Item with id: {} not found ", id);
            throw new NotFoundException("Item with id " +
                    id + " not found");
        }
    }
}