package com.example.sales.ad.filter;

import com.example.sales.ad.Ad;
import com.example.sales.ad.model.AdCategory;
import com.example.sales.ad.model.AdStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class AdSpecifications {
    public static Specification<Ad> hasStatus(AdStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Tehran");

    public static Specification<Ad> hasDateFilter(DateFilter dateFilter) {
        return (root, query, cb) -> {
            if (dateFilter == null) {
                return cb.conjunction();
            }

            LocalDate today = LocalDate.now(APP_ZONE);

            Instant startOfToday = today.atStartOfDay(APP_ZONE).toInstant();
            Instant startOfYesterday = today.minusDays(1).atStartOfDay(APP_ZONE).toInstant();
            Instant startOfSevenDaysAgo = today.minusDays(7).atStartOfDay(APP_ZONE).toInstant();

            return switch (dateFilter) {
                case YESTERDAY -> cb.and(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), startOfYesterday),
                        cb.lessThan(root.get("createdAt"), startOfToday)
                );

                case PAST_WEEK -> cb.and(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), startOfSevenDaysAgo),
                        cb.lessThan(root.get("createdAt"), startOfToday)
                );

                case OLDER -> cb.lessThan(root.get("createdAt"), startOfSevenDaysAgo);
            };
        };
    }

    public static Specification<Ad> hasCategory(AdCategory category) {
        return (root, query, cb) ->
                category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
    }

    public static Specification<Ad> hasCityId(Long cityId) {
        return (root, query, cb) ->
                cityId == null ? cb.conjunction() : cb.equal(root.get("city").get("id"), cityId);
    }

    public static Specification<Ad> priceGte(Long minPrice) {
        return (root, query, cb) ->
                minPrice == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Ad> priceLte(Long maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Ad> priceBetween(Long minPrice, Long maxPrice) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (minPrice != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return predicate;
        };
    }
}