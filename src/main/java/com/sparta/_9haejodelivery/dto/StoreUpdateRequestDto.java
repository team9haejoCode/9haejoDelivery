package com.sparta._9haejodelivery.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class StoreUpdateRequestDto {

    private String storeName;
    private UUID categoryId;
    private UUID regionId;
    private String address;
    @Size(max = 100)
    private String description;
    private Boolean isHide;

}
