package com.example.sales.ad.mapper;

import com.example.sales.ad.Ad;
import com.example.sales.ad.dto.AdCardSummary;
import com.example.sales.ad.dto.AdRequest;
import com.example.sales.ad.dto.AdResponse;
import com.example.sales.ad.favorite.FavoriteAd;
import com.example.sales.ad.dto.PendingAdResponse;
import com.example.sales.ad.reported.model.AdReport;
import com.example.sales.ad.reported.dto.AdReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdMapper {

    // =========================
    // AdRequest -> Ad
    // =========================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ad toEntity(AdRequest request);


    // =========================
    // Ad -> AdResponse
    // =========================

    @Mapping(target = "sellerFirstname", source = "seller.firstname")
    @Mapping(target = "sellerLastname", source = "seller.lastname")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerUsername", source = "seller.username")
    @Mapping(target = "favorite", ignore = true)
    @Mapping(target = "cityName", source = "city.name")
    AdResponse toResponse(Ad ad);

    List<AdResponse> toResponseList(List<Ad> ads);


    // =========================
    // AdReport -> AdReportResponse
    // =========================

    @Mapping(target = "adReportId", source = "id")
    @Mapping(target = "adId", source = "ad.id")
    @Mapping(target = "adTitle", source = "ad.title")
    @Mapping(target = "sellerFirstName", source = "ad.seller.firstname")
    @Mapping(target = "sellerLastName", source = "ad.seller.lastname")
    @Mapping(target = "sellerId", source = "ad.seller.id")
    @Mapping(target = "reportReason", source = "reason")
    @Mapping(target = "primaryImageId", ignore = true)
    @Mapping(target = "primaryImageUrl", ignore = true)
    AdReportResponse toAdReportResponse(AdReport adReport);

    List<AdReportResponse> toAdReportResponseList(List<AdReport> adReports);


    // =========================
    // FavoriteAd -> AdCartSummery
    // =========================

    @Mapping(target = "id", source = "ad.id")
    @Mapping(target = "title", source = "ad.title")
    @Mapping(target = "price", source = "ad.price")
    @Mapping(target = "cityName", source = "ad.city.name")
    @Mapping(target = "category", source = "ad.category")
    @Mapping(target = "createdAt", source = "ad.createdAt")
    @Mapping(target = "updatedAt", source = "ad.updatedAt")
    @Mapping(target = "primaryImageId", ignore = true)
    @Mapping(target = "primaryImageUrl", ignore = true)
    AdCardSummary toCartSummeryFromFavorite(FavoriteAd favoriteAd);

    List<AdCardSummary> toCartSummeryFromFavorites(
            List<FavoriteAd> favoriteAds
    );


    // =========================
    // Ad -> AdCartSummery
    // =========================

    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "primaryImageId", ignore = true)
    @Mapping(target = "primaryImageUrl", ignore = true)
    AdCardSummary toCartSummery(Ad ad);

    List<AdCardSummary> toCartSummeryList(List<Ad> ads);


    // =========================
    // Ad -> PendingAd
    // =========================

    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "sellerFirstName", source = "seller.firstname")
    @Mapping(target = "sellerLastName", source = "seller.lastname")
    @Mapping(target = "sellerId", source = "seller.id")
    PendingAdResponse toPendingAd(Ad ad);

    List<PendingAdResponse> toPendingAdList(List<Ad> ads);
}
