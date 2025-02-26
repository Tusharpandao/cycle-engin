package com.itrosys.cycle_engine.repository;

import com.itrosys.cycle_engine.dto.ItemNameAndTypelResponse;
import com.itrosys.cycle_engine.entity.Item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item,Integer> {
	
	 List<Item> findByBrand_BrandName(String brandName);
	 
	 @Query("SELECT DISTINCT new com.itrosys.cycle_engine.dto.ItemNameAndTypelResponse(i.itemName, i.itemType) " +
		       "FROM Item i WHERE i.brand.brandName = :brandName")
		List<ItemNameAndTypelResponse> findDistinctItemsByBrandName(@Param("brandName") String brandName);


	    @Query("SELECT i FROM Item i WHERE i.brand.brandName = :brandName AND i.itemName IN :selectedItems")
	    List<Item> findItemsByBrandAndNames(@Param("brandName") String brandName, 
	                                        @Param("selectedItems") List<String> selectedItems);
}
