package com.itrosys.cycle_engine.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResponse {

	private int itemId;
	private String itemName;
	private String itemType;
	private BigDecimal price;
	private Date validTo;
	private String brandName;
	private int brandId;
}
