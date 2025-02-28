package com.itrosys.cycle_engine.repository;

import com.itrosys.cycle_engine.entity.Brand;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Integer> {

	Optional<Brand> findByBrandName(String brandName);

	@Query("SELECT b.brandName FROM Brand b")
    List<String> findAllBrand();

	List<Brand> findByIsActive(char isActive);


	Optional<Brand> findByBrandNameAndIsActive(String brandName, char y);
}
