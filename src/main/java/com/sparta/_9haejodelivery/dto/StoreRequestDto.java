package com.sparta._9haejodelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class StoreRequestDto {

    @NotBlank
    private String storeName;
    @NotNull
    private UUID categoryId;
    @NotNull
    private UUID regionId;
    @NotBlank
    private String address;
    @Size(max = 100)
    private String description;
    private Boolean isHide;

}
