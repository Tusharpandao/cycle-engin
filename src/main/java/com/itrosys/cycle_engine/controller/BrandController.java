package com.itrosys.cycle_engine.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.itrosys.cycle_engine.dto.BrandResponse;
import com.itrosys.cycle_engine.service.BrandService;

@RestController
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

//  Important API
    // Get all brand names http://localhost:8080/brand/brands
    @GetMapping("/brands")
    public ResponseEntity<List<BrandResponse>> getAllBrandNames() {
        return new ResponseEntity<>(brandService.getAllBrands(), HttpStatus.OK);
    }

    // Get brand by ID  http://localhost:8080/brand/1
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable int id) {
        return new ResponseEntity<>(brandService.getBrand(id), HttpStatus.OK);
    }

    //  Get brand by Name  http://localhost:8080/brand/by-name?name=atlas
    @GetMapping("/by-name")
    public ResponseEntity<BrandResponse> getBrandByName(@RequestParam String name) {
        return new ResponseEntity<>(brandService.getBrandByName(name), HttpStatus.OK);
    }
    // POST: http://localhost:8080/brand/create

    @PostMapping("/add")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> addBrand(@RequestParam String name) {

        return new ResponseEntity<>(brandService.addBrand(name),HttpStatus.ACCEPTED);
    }

    // update the brand name
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BrandResponse> updateBrandName(@PathVariable int id, @RequestParam String newBrandName) {
        BrandResponse updatedBrand = brandService.updateBrandName(id, newBrandName);
        return ResponseEntity.ok(updatedBrand);
    }
    @DeleteMapping("/delete/delete-by-name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BrandResponse> deleteBrandByName(@RequestParam String brandName) {
        return new ResponseEntity<>(brandService.deleteBrandByName(brandName), HttpStatus.ACCEPTED);
    }


    // DELETE: http://localhost:8080/brand/delete/2
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> deleteBrandById(@PathVariable int id) {
        return new ResponseEntity<>( brandService.deleteBrandById(id),HttpStatus.ACCEPTED);
    }

}
