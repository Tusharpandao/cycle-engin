package com.itrosys.cycle_engine.controller;

import com.itrosys.cycle_engine.dto.BrandResponse;
import com.itrosys.cycle_engine.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
@Tag(name = "Brand Controller", description = "APIs related to Brand Management")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @Operation(summary = "Get all brand names", description = "Fetches all available brand names")
    @GetMapping("/brands")
    public ResponseEntity<List<BrandResponse>> getAllBrandNames() {
        return new ResponseEntity<>(brandService.getAllBrands(), HttpStatus.OK);
    }

    @Operation(summary = "Get brand by ID", description = "Fetch brand details by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable int id) {
        return new ResponseEntity<>(brandService.getBrand(id), HttpStatus.OK);
    }

    @Operation(summary = "Get brand by Name", description = "Fetch brand details by its name")
    @GetMapping("/by-name")
    public ResponseEntity<BrandResponse> getBrandByName(@RequestParam String name) {
        return new ResponseEntity<>(brandService.getBrandByName(name.toUpperCase()), HttpStatus.OK);
    }

    @Operation(summary = "Add a new brand", description = "Creates a new brand",
            security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/add")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> addBrand(@RequestParam String name) {
        return new ResponseEntity<>(brandService.addBrand(name.toUpperCase()), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Update brand name", description = "Updates the brand name by its ID",
            security = @SecurityRequirement(name = "basicAuth"))
    @PatchMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BrandResponse> updateBrandName(@RequestParam int id, @RequestParam String newBrandName) {
        return ResponseEntity.ok(brandService.updateBrandName(id, newBrandName.toUpperCase()));
    }

    @Operation(summary = "Delete brand by Name", description = "Removes a brand using its name",
            security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/delete/delete-by-name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BrandResponse> deleteBrandByName(@RequestParam String brandName) {
        return new ResponseEntity<>(brandService.deleteBrandByName(brandName.toUpperCase()), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Delete brand by ID", description = "Removes a brand using its ID",
            security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> deleteBrandById(@PathVariable int id) {
        return new ResponseEntity<>(brandService.deleteBrandById(id), HttpStatus.ACCEPTED);
    }
}
