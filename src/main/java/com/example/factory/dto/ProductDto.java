package com.example.factory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProductDto {

    private String name;
    private long machineId;
    private int numbersInPack;
    private long expectedProductivity;

}
