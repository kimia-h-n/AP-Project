package com.example.sales.province;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service containing business logic for province/city retrieval.
 */
@Service
@AllArgsConstructor
public class ProvinceService {

    private final ProvinceRepository repository;
    private final ProvinceMapper mapper;

    /**
     * Retrieves all provinces from the database and maps them to response DTOs.
     *
     * @return list of province responses
     */
    public List<ProvinceResponse> getAllProvinces() {
        return mapper.toProvinceResponse(repository.findAll());
    }
}
