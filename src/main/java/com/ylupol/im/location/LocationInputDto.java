package com.ylupol.im.location;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Builder
@Getter
@ToString
class LocationInputDto {

    @NotBlank
    private String name;

    private String description;

    Location toLocation() {
        return Location.builder()
            .name(name)
            .description(description)
            .build();
    }
}
