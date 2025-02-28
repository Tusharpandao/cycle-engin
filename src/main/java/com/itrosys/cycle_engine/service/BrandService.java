package com.itrosys.cycle_engine.service;


import java.util.List;
import java.util.Optional;


import com.itrosys.cycle_engine.entity.Item;
import com.itrosys.cycle_engine.exception.DuplicateBrand;
import com.itrosys.cycle_engine.repository.ItemRepository;
import org.springframework.stereotype.Service;

import com.itrosys.cycle_engine.dto.BrandResponse;
import com.itrosys.cycle_engine.entity.Brand;
import com.itrosys.cycle_engine.exception.BrandNotFound;
import com.itrosys.cycle_engine.repository.BrandRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class BrandService {

   final private BrandRepository brandRepository;
    final private ItemRepository itemRepository;

    public BrandService(BrandRepository brandRepository,ItemRepository itemRepository) {
        this.brandRepository = brandRepository;
        this.itemRepository=itemRepository;
    }

    public BrandResponse getBrand(int id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFound("Brand with ID " + id + " not found"));

        if (brand.getIsActive() == 'N'){
            throw new BrandNotFound("Brand with ID " + id + " is not active ");
       }
        return BrandResponse.builder()
                .id(brand.getBrandId())
                .name(brand.getBrandName())
                .build();
    }


    public List<BrandResponse> getAllBrands() {
//		    List<Brand> brands = brandRepository.findAll();
        List<Brand> brands = brandRepository.findByIsActive('y');
        return brands.stream()
		            .map(brand -> BrandResponse.builder()
		                    .id(brand.getBrandId())
		                    .name(brand.getBrandName())
		                    .build())
		                    .toList();
    }

    public BrandResponse getBrandByName(String brandName) {

        Brand brand = brandRepository.findByBrandName(brandName)
                .orElseThrow(() -> new BrandNotFound("Brand with Name " + brandName + " not found"));

        if (brand.getIsActive() == 'N'){
            throw new BrandNotFound("Brand with brandName " + brandName + " is not active ");
        }
        return BrandResponse.builder()
                .id(brand.getBrandId())
                .name(brand.getBrandName())
                .build();
    }

    // Create a new Brand
    public BrandResponse addBrand(String brandName) {
        // Find brand by name
        Optional<Brand> existingBrandOptional = brandRepository.findByBrandName(brandName);

        // Get logged-in user
        String loggedInUsername = getLoggedInUsername();
//        Check the brand present in database
        if (existingBrandOptional.isPresent()) {
            Brand existingBrand = existingBrandOptional.get();
//                here check brand is active if is active state then we not save that brand again
            if (existingBrand.getIsActive() == 'Y') {
                throw new DuplicateBrand("Brand with name '" + brandName + "' already exists and is active.");
            } else {
                // Reactivate brand if brand is inActive state
                existingBrand.setIsActive('Y');
                existingBrand.setModifiedBy(loggedInUsername);
                Brand updatedBrand = brandRepository.save(existingBrand);

                return BrandResponse.builder()
                        .message("Brand reactivated successfully.")
                        .name(updatedBrand.getBrandName())
                        .id(updatedBrand.getBrandId())
                        .build();
            }
        }

        // If brand does not exist, create a new one
        Brand newBrand = new Brand();
        newBrand.setBrandName(brandName);
        newBrand.setIsActive('Y'); // Set as active
        newBrand.setModifiedBy(loggedInUsername); // Set modifiedBy

        Brand savedBrand = brandRepository.save(newBrand);

        return BrandResponse.builder()
                .message("Brand added successfully.")
                .name(savedBrand.getBrandName())
                .id(savedBrand.getBrandId())
                .build();
    }


    // Delete a Brand by id
    public BrandResponse deleteBrandById(int id) {
        // Fetch brand from DB or throw exception
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFound("Brand with ID " + id + " not found."));

        // Get logged-in user
        String loggedInUsername = getLoggedInUsername();

        // Check if brand is already inactive
        if (brand.getIsActive() == 'N') {
            throw new BrandNotFound("Brand with ID " + id + " is already inactive.");
        }

        // Set brand as inactive instead of deleting
        brand.setIsActive('N');
        brand.setModifiedBy(loggedInUsername);
        brandRepository.save(brand);  // Save updated brand

        // Fetch all items linked to this brand
        List<Item> items = itemRepository.findByBrand(brand);
//
        // Check if items exist before updating
        if (!items.isEmpty()) {
            for (Item item : items) {
                item.setIsActive('N');  // Deactivate item
                item.setModifiedBy(loggedInUsername);
            }
            // Save all updated items
            itemRepository.saveAll(items);
        }
        return BrandResponse.builder()
                .message("Brand deleted successfully.")
                .name(brand.getBrandName())
                .id(brand.getBrandId())
                .build();
    }

    public BrandResponse deleteBrandByName(String brandName) {
        // Fetch brand from DB or throw exception
        Brand brand = brandRepository.findByBrandName(brandName)
                .orElseThrow(() -> new BrandNotFound("Brand with ID " + brandName + " not found."));

        // Get logged-in user
        String loggedInUsername = getLoggedInUsername();

        // Check if brand is already inactive
        if (brand.getIsActive() == 'N') {
            throw new BrandNotFound("Brand with ID " + brandName + " is already inactive.");
        }

        // If brand active Y  Set brand as inactive instead of deleting
        brand.setIsActive('N');
        brand.setModifiedBy(loggedInUsername);
        brandRepository.save(brand);  // Save updated brand

        // Fetch all items linked to this brand
        List<Item> items = itemRepository.findByBrand(brand);

        // Check if items exist before updating
        if (!items.isEmpty()) {
            for (Item item : items) {
                item.setIsActive('N');  // Deactivate item
                item.setModifiedBy(loggedInUsername);
            }
            // Save all updated items
            itemRepository.saveAll(items);
        }
        return BrandResponse.builder()
                .message("Brand deleted successfully.")
                .name(brand.getBrandName())
                .id(brand.getBrandId())
                .build();
    }


    public BrandResponse updateBrandName(int id, String newBrandName) {
        // Find brand by ID
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFound("Brand with ID " + id + " not found."));

        // Check if another active brand with the new name already exists
        Optional<Brand> existingBrand = brandRepository.findByBrandName(newBrandName);
        if (existingBrand.isPresent() && existingBrand.get().getIsActive() == 'Y') {
            throw new DuplicateBrand("Brand name '" + newBrandName + "' already exists and is active.");
        }

        // Update brand name  and Set modified by current user
        brand.setBrandName(newBrandName);
        brand.setModifiedBy(getLoggedInUsername());

        // Save updated brand
        Brand updatedBrand = brandRepository.save(brand);

        return BrandResponse.builder()
                .message("Brand name updated successfully.")
                .id(updatedBrand.getBrandId())
                .name(updatedBrand.getBrandName())
                .build();
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
