package com.example.sales.province;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for retrieving province/city data.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/province")
public class ProvinceController {

    private final ProvinceService service;

    /**
     * Returns all available provinces.
     *
     * @return list of province responses
     */
    @GetMapping()
    public List<ProvinceResponse> getAllProvinces() {
        return service.getAllProvinces();
    }
}
