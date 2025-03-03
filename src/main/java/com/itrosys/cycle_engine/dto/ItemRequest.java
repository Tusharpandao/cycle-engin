package com.itrosys.cycle_engine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date validTo;
    private String validTo;
    private String brandName;
}
