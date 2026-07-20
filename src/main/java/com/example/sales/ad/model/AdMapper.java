package com.example.sales.ad.model;

import com.example.sales.ad.fav.FavoriteAd;
import com.example.sales.ad.report.AdReport;
import com.example.sales.ad.report.AdReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AdMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    Ad toEntity(AdRequest request);

    @Mapping(target = "sellerFirstname", source = "seller.firstname")
    @Mapping(target = "sellerLastname", source = "seller.lastname")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerUsername", source = "seller.username")
    @Mapping(target = "favorite", ignore = true)
    @Mapping(target = "cityName", source = "city.name")
    AdResponse toResponse(Ad ad);


    @Mapping(target = "sellerFirstname", source = ("seller.firstname"))
    @Mapping(target = "sellerLastname", source = "seller.lastname")
    @Mapping(target = "adTitle", source = "ad.title")
    @Mapping(target = "adReportId", source = "id")
    @Mapping(target = "adId", source = "ad.id")
    List<AdReportResponse> toAdReportResponse(List<AdReport> ads);

    @Mapping(target = "cityName", source = "city.name")
    List<AdResponse> toResponseList(List<Ad> ads);

    @Mapping(target = "id", source = "ad.id")
    @Mapping(target = "title", source = "ad.title")
    @Mapping(target = "price", source = "ad.price")
    @Mapping(target = "cityName", source = "ad.city.name")
    @Mapping(target = "category", source = "ad.category")
    @Mapping(target = "createdAt", source = "ad.createdAt")
    @Mapping(target = "updatedAt", source = "ad.updatedAt")
    List<AdCartSummery> toCartSummeryFromFavorites(List<FavoriteAd> ads);

    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "id", source = "id")
    List<AdCartSummery> toCartSummeryList(List<Ad> ads);

    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "sellerFirstname", source = "seller.firstname")
    @Mapping(target = "sellerLastname", source = "seller.lastname")
    @Mapping(target = "sellerId", source = "seller.id")
    List<PendingAd> toPendingAdList(List<Ad> ads);
}

