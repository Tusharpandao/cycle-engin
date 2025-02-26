package com.itrosys.cycle_engine.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.itrosys.cycle_engine.dto.Cycle;
import com.itrosys.cycle_engine.dto.CycleResponse;
import com.itrosys.cycle_engine.dto.ItemNameAndTypelResponse;
import com.itrosys.cycle_engine.dto.ItemResponse;
import com.itrosys.cycle_engine.entity.Item;
import com.itrosys.cycle_engine.exception.ItemNotFound;
import com.itrosys.cycle_engine.repository.ItemRepository;

@Service
public class ItemService {

	private ItemRepository itemRepository;

	public ItemService(ItemRepository repository) {
		this.itemRepository = repository;

	}
	public ItemResponse mapToItemResponse(Item item) {
	    return ItemResponse.builder()
	            .itemId(item.getItemId())
	            .itemName(item.getItemName())
	            .itemType(item.getItemType())
	            .price(item.getPrice())
	            .validTo(item.getValidTo())
	            .brandName(item.getBrand().getBrandName()) 
	            .build();
	}

	public ItemResponse getItemById(int id) {

		Item item = itemRepository.findById(id)
		.orElseThrow(() -> new ItemNotFound("Item with id " + id + " not found"));

		return mapToItemResponse(item) ;
	}
	
	 public List<ItemResponse> getItemsByBrandName(String brandName) {
	        // Fetch all items whose brand.brandName matches
	        List<Item> items = itemRepository.findByBrand_BrandName(brandName);

	        // Convert List<Item> to List<ItemResponse>
	        return items.stream()
	                .map(item -> ItemResponse.builder()
	                        .itemId(item.getItemId())
	                        .itemName(item.getItemName())
	                        .itemType(item.getItemType())
	                        .price(item.getPrice())
	                        .validTo(item.getValidTo())
	                        .brandName(item.getBrand().getBrandName()) // from Brand entity
	                        .build())
	                .collect(Collectors.toList());
	    }
	
	 public Map<String, List<String>> getGroupedItemNameAndTypeByBrandName(String brandName) {
		    List<ItemNameAndTypelResponse> items = itemRepository.findDistinctItemsByBrandName(brandName);

		    return items.stream()
		            .collect(Collectors.groupingBy(
		                    ItemNameAndTypelResponse::getItemType, 
		                    Collectors.mapping(ItemNameAndTypelResponse::getItemName, Collectors.toList())
		            ));
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


	public List<Item> getItemsByType(String type) {
		return itemRepository.findByItemType(type);
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

}
