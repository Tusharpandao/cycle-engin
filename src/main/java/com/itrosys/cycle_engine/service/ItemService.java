package com.itrosys.cycle_engine.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.itrosys.cycle_engine.dto.*;
import com.itrosys.cycle_engine.entity.Brand;
import com.itrosys.cycle_engine.exception.BrandNotFound;
import com.itrosys.cycle_engine.repository.BrandRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.itrosys.cycle_engine.entity.Item;
import com.itrosys.cycle_engine.exception.ItemNotFound;
import com.itrosys.cycle_engine.repository.ItemRepository;

@Service
public class ItemService {

	final private ItemRepository itemRepository;
	final private BrandRepository brandRepository;

	public ItemService(ItemRepository repository,BrandRepository brandRepository) {
		this.itemRepository = repository;
		this.brandRepository= brandRepository;

	}
	public ItemResponse mapToItemResponse(Item item) {
	    return ItemResponse.builder()
	            .itemId(item.getItemId())
	            .itemName(item.getItemName())
	            .itemType(item.getItemType())
	            .price(item.getPrice())
	            .validTo(item.getValidTo())
				.brandId(item.getBrand().getBrandId())
	            .brandName(item.getBrand().getBrandName()) 
	            .build();
	}

	public ItemResponse getItemById(int id) {

		Item item = itemRepository.findById(id)
		.orElseThrow(() -> new ItemNotFound("Item with id " + id + " not found"));

		if (item.getIsActive() == 'N'){
			throw new BrandNotFound("Item with ID " + id + " is not active ");
		}

		return mapToItemResponse(item) ;
	}

	public List<ItemResponse> getItemsByBrandName(String brandName) {
		// Fetch brand by name
		Brand brand = brandRepository.findByBrandName(brandName)
				.orElseThrow(() -> new BrandNotFound("Brand with name '" + brandName + "' not found ."));

		// Check if brand is inactive
		if (brand.getIsActive() == 'N') {
			throw new BrandNotFound("Brand '" + brandName + "' is inactive.");
		}
		// Fetch all items linked to the brand
		List<Item> items = itemRepository.findByBrand_BrandName(brandName);

		// If no items are found, throw exception
		if (items.isEmpty()) {
			throw new ItemNotFound("No items found for brand '" + brandName + "'.");
		}

		// Convert List<Item> to List<ItemResponse>
		return items.stream()
				.map(item -> ItemResponse.builder()
						.itemId(item.getItemId())
						.itemName(item.getItemName())
						.itemType(item.getItemType())
						.price(item.getPrice())
						.validTo(item.getValidTo())
						.brandId(item.getBrand().getBrandId())
						.brandName(item.getBrand().getBrandName()) // from Brand entity
						.build())
				.collect(Collectors.toList());
	}


	public Map<String, List<String>> getGroupedItemNameAndTypeByBrandName(String brandName) {
		Brand brand = brandRepository.findByBrandName(brandName)
				.orElseThrow(() -> new BrandNotFound("Brand with name '" + brandName + "' not found ."));

		// Check if brand is inactive
		if (brand.getIsActive() == 'N') {
			throw new BrandNotFound("Brand '" + brandName + "' is inactive.");
		}

		List<ItemNameAndTypelResponse> items = itemRepository.findDistinctItemsByBrandName(brandName);
		 // If no items are found, throw item exception
		 if (items.isEmpty()) {
			 throw new ItemNotFound("Items with Brand name '" + brandName + "' not found in the database.");
		 }
		    return items.stream()
		            .collect(Collectors.groupingBy(
		                    ItemNameAndTypelResponse::getItemType, 
		                    Collectors.mapping(ItemNameAndTypelResponse::getItemName, Collectors.toList())
		            ));
		}
	public ItemResponse addItem(ItemRequest itemRequest) {
		// Manual validation for required fields
		if (itemRequest.getItemName() == null || itemRequest.getItemName().trim().isEmpty()) {
			throw new IllegalArgumentException("Item name cannot be blank");
		}
		if (itemRequest.getItemType() == null || itemRequest.getItemType().trim().isEmpty()) {
			throw new IllegalArgumentException("Item type cannot be blank");
		}
		if (itemRequest.getPrice() == null) {
			throw new IllegalArgumentException("Price cannot be null");
		}
		if (itemRequest.getBrandName() == null || itemRequest.getBrandName().trim().isEmpty()) {
			throw new IllegalArgumentException("Brand name cannot be blank");
		}

		// Check if the brand exists and is active
		Brand brand = brandRepository.findByBrandNameAndIsActive(itemRequest.getBrandName(), 'Y')
				.orElseThrow(() -> new BrandNotFound("Brand '" + itemRequest.getBrandName() + "' is not active or does not exist"));

		// Create new Item entity
		Item item = new Item();
		item.setItemName(itemRequest.getItemName());
		item.setItemType(itemRequest.getItemType());
		item.setPrice(itemRequest.getPrice());
		item.setValidTo(itemRequest.getValidTo());
		item.setBrand(brand);
		item.setIsActive('Y');
		item.setModifiedBy(getLoggedInUsername());

		// Save item
		Item savedItem = itemRepository.save(item);

		// Convert to response DTO
		return ItemResponse.builder()
				.itemId(savedItem.getItemId())
				.itemName(savedItem.getItemName())
				.itemType(savedItem.getItemType())
				.price(savedItem.getPrice())
				.validTo(savedItem.getValidTo())
				.brandId(savedItem.getBrand().getBrandId())
				.brandName(savedItem.getBrand().getBrandName())
				.build();
	}


	public CycleResponse calculateTotalPrice(Cycle cycle) {
		    List<String> selectedItems = Arrays.asList(
		        cycle.getTyre(), cycle.getWheel(), cycle.getFrame(),
		        cycle.getSeating(), cycle.getBrakes(), cycle.getChainAssembly(),
		        cycle.getHandlebar()
		    );

		    // Fetch item details from the database
		    List<Item> items = itemRepository.findItemsByBrandAndNames(cycle.getBrand(), selectedItems);

		    // Map to store individual item prices
		    Map<String, BigDecimal> partsPrice = new LinkedHashMap<>();

		    // Calculate total base price
		    BigDecimal totalPrice = BigDecimal.ZERO;
		    for (Item item : items) {
		        partsPrice.put(item.getItemName(), item.getPrice()); // Store item price
		        totalPrice = totalPrice.add(item.getPrice());
		    }

		    // Calculate GST (18% of total price)
		    BigDecimal gst = totalPrice.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);

		    // Calculate final total price
		    BigDecimal finalTotalPrice = totalPrice.add(gst);

		    return CycleResponse.builder()
		            .brand(cycle.getBrand())
		            .tyre(cycle.getTyre())
		            .wheel(cycle.getWheel())
		            .frame(cycle.getFrame())
		            .seating(cycle.getSeating())
		            .brakes(cycle.getBrakes())
		            .chainAssembly(cycle.getChainAssembly())
		            .handlebar(cycle.getHandlebar())
		            .price(totalPrice)
		            .gst(gst)
		            .totalPrice(finalTotalPrice)
		            .partsPrice(partsPrice)
		            .build();
		}


	public List<ItemResponse> getItemsByType(String type) {
		List<Item> items = itemRepository.findByItemTypeAndIsActiveAndBrand_IsActive(type, 'Y', 'Y');

		return items.stream()
				.map(item -> ItemResponse.builder()
						.itemId(item.getItemId())
						.itemName(item.getItemName())
						.itemType(item.getItemType())
						.price(item.getPrice())
						.validTo(item.getValidTo())
						.brandName(item.getBrand().getBrandName())
						.brandId(item.getBrand().getBrandId())
						.build())
				.collect(Collectors.toList());
	}


	public void deleteItemById(int id) {
		Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFound("Item with Id " + id + "  not found"));

		itemRepository.deleteById(id);
	}

	public ItemResponse updateItemPrice(int id, BigDecimal newPrice) {
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new ItemNotFound("Item not found with ID: " + id));
		item.setPrice(newPrice);
		Item savedItem = itemRepository.save(item);
		return mapToItemResponse(savedItem);

	}


	// Helper method to get the logged-in username
	private String getLoggedInUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		} else {
			return principal.toString();
		}
	}

}
