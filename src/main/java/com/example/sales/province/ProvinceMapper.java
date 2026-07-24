package com.example.sales.province;


import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for converting {@link City} entities to {@link ProvinceResponse} DTOs.
 */
@Mapper(componentModel = "spring")
public interface ProvinceMapper {

    /**
     * Converts a list of city entities into province response DTOs.
     *
     * @param cityList list of city entities
     * @return list of province responses
     */
    List<ProvinceResponse> toProvinceResponse(List<City> cityList);
}
