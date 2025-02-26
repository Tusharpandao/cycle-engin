package com.itrosys.cycle_engine.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Get all brand names http://localhost:8080/brand/brands
    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrandNames() {
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
    @PostMapping("/create")
    public ResponseEntity<BrandResponse> createBrand(@RequestParam String name) {

        return new ResponseEntity<>(brandService.createBrand(name),HttpStatus.CREATED);
    }

    // DELETE: http://localhost:8080/brand/delete/2
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBrandById(@PathVariable int id) {
        brandService.deleteBrandById(id);
        return ResponseEntity.ok("Brand deleted successfully.");
    }

}
