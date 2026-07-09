package com.example.sales.ad.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    Ad toEntity(AdRequest request);

    @Mapping(target = "sellerUsername", source = "seller.username")
    @Mapping(target = "favorite", ignore = true)
    AdResponse toResponse(Ad ad);

    List<AdResponse> toResponseList(List<Ad> ads);
}

