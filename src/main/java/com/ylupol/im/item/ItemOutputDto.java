package com.ylupol.im.item;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ItemOutputDto {

    private final Integer id;

    private final String name;

    private final String location;

    public ItemOutputDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.location = item.getLocation().getName();
    }
}
