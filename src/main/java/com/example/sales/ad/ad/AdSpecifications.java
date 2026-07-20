package com.example.sales.ad.ad;

import com.example.sales.ad.model.Ad;
import com.example.sales.ad.model.AdCategory;
import com.example.sales.ad.model.AdStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class AdSpecifications {
    public static Specification<Ad> hasStatus(AdStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
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