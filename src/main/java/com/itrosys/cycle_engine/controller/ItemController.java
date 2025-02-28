package com.itrosys.cycle_engine.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.itrosys.cycle_engine.dto.ItemRequest;
import com.itrosys.cycle_engine.entity.Item;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
	// get item By id
//	http://localhost:8080/item/by-id/2
	@GetMapping("by-id/{id}")
	public ResponseEntity<ItemResponse> getItemById(@PathVariable int id) {
		return new ResponseEntity<>(itemService.getItemById(id), HttpStatus.FOUND);

	}
//	get items By brand name
//	http://localhost:8080/item/brand/hero
	@GetMapping("/brand/{brandName}")
	public ResponseEntity<List<ItemResponse>> getItemsByBrandName(@PathVariable String brandName) {
//		List<ItemResponse> items = itemService.getItemsByBrandName(brandName);
		return new ResponseEntity<>(itemService.getItemsByBrandName(brandName), HttpStatus.OK);
	}
//	get items Type with associate item Names  "item_type": [ "itemName1","itemName2"],for all type
//	http://localhost:8080/item/byBrand?brandName=hero
	@GetMapping("/byBrand")
	public ResponseEntity<Map<String, List<String>>> getGroupedItemNameAndTypeByBrand(@RequestParam String brandName) {
		return new ResponseEntity<>(itemService.getGroupedItemNameAndTypeByBrandName(brandName),HttpStatus.FOUND);
	}

	// Get the items by item Type
	// GET: http://localhost:8080/item/by-type?type=Frame
	@GetMapping("/by-type")
	public ResponseEntity<List<ItemResponse>> getItemsByType(@RequestParam String type) {
		return ResponseEntity.ok(itemService.getItemsByType(type));
	}

//	http://localhost:8080/item/calculate-price
	@PostMapping("/calculate-price")
	public ResponseEntity<CycleResponse> calculatePrice(@RequestBody Cycle cycle) {
		CycleResponse response = itemService.calculateTotalPrice(cycle);
		return ResponseEntity.ok(response);
	}


	// update the  item price
	// http://localhost:8080/item/update-price/3?price=180.00
	@PatchMapping("/update-price/")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<ItemResponse> updateItemPrice(@RequestParam int itemId, @RequestParam BigDecimal price) {
		return new ResponseEntity<>(itemService.updateItemPrice(itemId, price),HttpStatus.ACCEPTED);
	}

	// Add item  POST http://localhost:8080/item/add
	@PostMapping("/add")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
	public ResponseEntity<ItemResponse> addItem(@RequestBody ItemRequest itemRequest) {
		return new ResponseEntity<>(itemService.addItem(itemRequest), HttpStatus.CREATED);
	}

	// DELETE: http://localhost:8080/item/delete/5
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<String> deleteItemById(@PathVariable int id) {
		itemService.deleteItemById(id);
		return ResponseEntity.ok("Item deleted successfully.");
	}
}
