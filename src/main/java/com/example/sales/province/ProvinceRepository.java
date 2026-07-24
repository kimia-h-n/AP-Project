package com.example.sales.province;


import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing {@link City} entities.
 */
public interface ProvinceRepository extends JpaRepository<City, Long> {
}
