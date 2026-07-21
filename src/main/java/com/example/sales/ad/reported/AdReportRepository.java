package com.example.sales.ad.reported;

import com.example.sales.ad.reported.model.AdReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AdReportRepository extends JpaRepository<AdReport, Long> {
    @Query("""
            select ar
            from AdReport ar
            join fetch ar.ad ad
            join fetch ad.seller seller
            order by ar.id desc
            """)
    List<AdReport> findAllWithAdAndSeller();


}
