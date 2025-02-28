package com.itrosys.cycle_engine.dto;


import java.math.BigDecimal;
import java.util.Date;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequest {

    private String itemName;
    private String itemType;
    private BigDecimal price;
    private Date validTo; // Optional field
    private String brandName;
}
