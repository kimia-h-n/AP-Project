package com.example.sales.ad.filter;

import com.example.sales.ad.Ad;
import com.example.sales.ad.model.AdCategory;
import com.example.sales.ad.model.AdStatus;
import com.example.sales.rating.SellerRating;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Specification helpers for building advertisement queries.
 */
public class AdSpecifications {
    /**
     * Filters ads by status.
     */
    public static Specification<Ad> hasStatus(AdStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Tehran");

    /**
     * Filters ads by creation date relative to the current day.
     */
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

    /**
     * Filters ads by category.
     */
    public static Specification<Ad> hasCategory(AdCategory category) {
        return (root, query, cb) ->
                category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
    }

    /**
     * Filters ads by city id.
     */
    public static Specification<Ad> hasCityId(Long cityId) {
        return (root, query, cb) ->
                cityId == null ? cb.conjunction() : cb.equal(root.get("city").get("id"), cityId);
    }

    /**
     * Filters ads by a price range.
     */
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

    /**
     * Applies dynamic ordering based on the selected sort option.
     */
    public static Specification<Ad> applySorting(AdSortChoice sortOption) {
        return (root, query, cb) -> {
            if (sortOption == null || query.getResultType() == Long.class || query.getResultType() == long.class) {
                return cb.conjunction();
            }

            switch (sortOption) {
                case NEWEST -> query.orderBy(cb.desc(root.get("createdAt")));
                case CHEAPEST -> query.orderBy(cb.asc(root.get("price")));
                case MOST_EXPENSIVE -> query.orderBy(cb.desc(root.get("price")));

                case SELLER_RATING -> {
                    Subquery<Double> subquery = query.subquery(Double.class);
                    Root<SellerRating> subRoot = subquery.from(SellerRating.class);

                    subquery.select(cb.avg(subRoot.get("rating")))
                            .where(cb.equal(subRoot.get("seller"), root.get("seller")));

                    Expression<Double> avgRating = cb.coalesce(subquery, 0.0);

                    query.orderBy(cb.desc(avgRating), cb.desc(root.get("createdAt")));
                }
            }
            return cb.conjunction();
        };
    }


}