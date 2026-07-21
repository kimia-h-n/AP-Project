package com.example.sales.ad;

import com.example.sales.ad.model.AdStatus;
import com.example.sales.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {

    List<Ad> findAllByStatus(AdStatus status);

    List<Ad> findAllBySeller(User seller);

    List<Ad> findAllBySellerAndStatus(User seller, AdStatus status);

    List<Ad> findByTitleContainingIgnoreCaseAndStatus(String title, AdStatus status);

    long countByStatus(AdStatus status);


    boolean existsById(Long id);
}
