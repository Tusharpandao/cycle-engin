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

    // Get all brand names
    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrandNames() {
        return new ResponseEntity<>(brandService.getAllBrands(), HttpStatus.OK);
    }

    // Get brand by ID
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable int id) {
        return new ResponseEntity<>(brandService.getBrand(id), HttpStatus.OK);
    }

    //  Get brand by Name
    @GetMapping("/by-name")
    public ResponseEntity<BrandResponse> getBrandByName(@RequestParam String name) {
        return new ResponseEntity<>(brandService.getBrandByName(name), HttpStatus.OK);
    }
}
