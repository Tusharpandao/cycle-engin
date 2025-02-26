package com.itrosys.cycle_engine.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itrosys.cycle_engine.dto.Cycle;
import com.itrosys.cycle_engine.dto.CycleResponse;
import com.itrosys.cycle_engine.dto.ItemResponse;
import com.itrosys.cycle_engine.service.ItemService;

@RestController
@RequestMapping("/item")
public class ItemController {

	private ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;

	}

	@GetMapping("by-id/{id}")
	public ResponseEntity<ItemResponse> getItemById(@PathVariable int id) {
		return new ResponseEntity<>(itemService.getItemById(id), HttpStatus.FOUND);

	}

	@GetMapping("/brand/{brandName}")
	public ResponseEntity<List<ItemResponse>> getItemsByBrandName(@PathVariable String brandName) {
		List<ItemResponse> items = itemService.getItemsByBrandName(brandName);
		return new ResponseEntity<>(items, HttpStatus.OK);
	}

	@GetMapping("/byBrand")
	public Map<String, List<String>> getGroupedItemNameAndTypeByBrand(@RequestParam String brandName) {
		return itemService.getGroupedItemNameAndTypeByBrandName(brandName);
	}

	@PostMapping("/calculate-price")
	public ResponseEntity<CycleResponse> calculatePrice(@RequestBody Cycle cycle) {
		CycleResponse response = itemService.calculateTotalPrice(cycle);
		return ResponseEntity.ok(response);
	}
}
