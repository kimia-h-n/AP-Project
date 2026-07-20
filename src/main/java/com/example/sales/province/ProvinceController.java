package com.example.sales.province;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/province")
public class ProvinceController {

    private final ProvinceService service;

    @GetMapping()
    public List<ProvinceResponse> getAllProvinces(){
        return service.getAllProvinces();
    }

}
