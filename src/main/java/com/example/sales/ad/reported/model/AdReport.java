package com.example.sales.ad.reported.model;

import com.example.sales.ad.Ad;
import com.example.sales.user.model.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing a report submitted for an advertisement.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ad_reports")
public class AdReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_reason", nullable = false)
    private ReportReason reason;
//    @Column(name = "description")
//    private String description;
//

}
