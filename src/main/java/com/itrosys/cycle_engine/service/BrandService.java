package com.itrosys.cycle_engine.service;



import java.util.List;


import org.springframework.stereotype.Service;

import com.itrosys.cycle_engine.dto.BrandResponse;
import com.itrosys.cycle_engine.entity.Brand;
import com.itrosys.cycle_engine.exception.BrandNotFound;
import com.itrosys.cycle_engine.repository.BrandRepository;

@Service
public class BrandService {

	private BrandRepository brandRepository;
	
	public BrandService(BrandRepository brandRepository) {
		this.brandRepository=brandRepository;
	}

	 public BrandResponse getBrand(int id) {
	        Brand brand = brandRepository.findById(id)
	                .orElseThrow(() -> new BrandNotFound("Brand with ID " + id + " not found"));

	        return BrandResponse.builder()
	                .id(brand.getBrandId())
	                .name(brand.getBrandName())
	                .build();
	    }
	 

	
	 public List<String> getAllBrands() {
//		    List<Brand> brands = brandRepository.findAll();
		    return brandRepository.findAllBrandNames();
		    

//		    return brands.stream()
//		            .map(brand -> BrandResponse.builder()
//		                    .id(brand.getBrandId())
//		                    .name(brand.getBrandName())
//		                    .build())
//		                    .toList();
		}

	public BrandResponse getBrandByName(String brandName) {
		
		 Brand brand = brandRepository.findByBrandName(brandName)
				.orElseThrow(() -> new BrandNotFound("Brand with Name " + brandName+ " not found"));
		
		 return BrandResponse.builder()
		            .id(brand.getBrandId())
		            .name(brand.getBrandName())
		            .build();
	}

	// Create a new Brand
	public void createBrand(String brandName) {
		Brand brand = new Brand();
		brand.setBrandName(brandName);
		brandRepository.save(brand);
	}

	// Delete a Brand
	public void deleteBrandById(int id) {
		brandRepository.deleteById(id);
	}
	
}
