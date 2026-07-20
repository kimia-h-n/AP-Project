package com.example.sales.province;


import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {

    List<ProvinceResponse> toProvinceResponse(List<City> cityList);
}
