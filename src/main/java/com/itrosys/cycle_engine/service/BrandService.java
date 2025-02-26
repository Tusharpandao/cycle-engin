package com.itrosys.cycle_engine.service;


import java.util.List;


import com.itrosys.cycle_engine.exception.DuplicateBrand;
import com.itrosys.cycle_engine.repository.ItemRepository;
import org.springframework.stereotype.Service;

import com.itrosys.cycle_engine.dto.BrandResponse;
import com.itrosys.cycle_engine.entity.Brand;
import com.itrosys.cycle_engine.exception.BrandNotFound;
import com.itrosys.cycle_engine.repository.BrandRepository;

@Service
public class BrandService {

    private BrandRepository brandRepository;
    private ItemRepository itemRepository;

    public BrandService(BrandRepository brandRepository,ItemRepository itemRepository) {
        this.brandRepository = brandRepository;
        this.itemRepository=itemRepository;
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
                .orElseThrow(() -> new BrandNotFound("Brand with Name " + brandName + " not found"));

        return BrandResponse.builder()
                .id(brand.getBrandId())
                .name(brand.getBrandName())
                .build();
    }

    // Create a new Brand
    public BrandResponse createBrand(String brandName) {
//        check before to save the brand database if all ready brand present then throw exception
        brandRepository.findByBrandName(brandName).ifPresent(existingBrand -> {
            throw new DuplicateBrand("Brand with name '" + brandName + "' already exists.");
        });
        Brand brand = new Brand();
        brand.setBrandName(brandName);
        Brand savedBrand = brandRepository.save(brand);

        return BrandResponse.builder()
                .message("Brand added successfully.")
                .name(savedBrand.getBrandName())
                .id(savedBrand.getBrandId())
                .build();
    }

    // Delete a Brand
    public void deleteBrandById(int id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFound("Brand with ID " + id + " not found."));

        if (!itemRepository.findByBrand(brand).isEmpty()) {
            throw new IllegalStateException("Cannot delete Brand ID " + id + " as it has associated Items.");
        }

        brandRepository.deleteById(id);
    }


}
