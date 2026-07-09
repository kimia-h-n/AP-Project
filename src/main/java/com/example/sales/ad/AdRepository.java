package com.example.sales.ad;

import com.example.sales.ad.model.Ad;
import com.example.sales.ad.model.AdStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {

    List<Ad> findAllByStatus(AdStatus status);

    boolean existsById(Long id);
}
