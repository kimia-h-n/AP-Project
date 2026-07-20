package com.example.sales.province;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProvinceService {

    private final ProvinceRepository repository;
    private final ProvinceMapper mapper;

    public List<ProvinceResponse> getAllProvinces() {
        return mapper.toProvinceResponse(repository.findAll());
    }
}
