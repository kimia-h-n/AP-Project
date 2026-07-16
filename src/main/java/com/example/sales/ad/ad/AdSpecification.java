package com.example.sales.ad.ad;

import com.example.sales.ad.model.Ad;
import org.springframework.data.jpa.domain.Specification;

public class AdSpecification {

    public static Specification<Ad> titleContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return cb.conjunction();
            return cb.like(
                    cb.lower(root.get("title")),
                    "%" + keyword.trim() + "%");
        };
    }
}
