package com.example.sales.ad.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdReportRepository extends JpaRepository<AdReport, Long> {



}
