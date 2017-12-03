package com.ylupol.im.item;

import com.ylupol.im.web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/items")
public class ItemController {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ItemOutputDto> getById(@PathVariable int id) {
        return itemRepository.findById(id)
            .map(ItemOutputDto::new)
            .map(ResponseEntity::ok)
            .orElseThrow(ResourceNotFoundException::new);
    }

}
