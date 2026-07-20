package com.example.sales.ad.model;

import com.example.sales.picture.ImageData;
import com.example.sales.province.City;
import com.example.sales.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String address;
    private long price;
    @Enumerated(EnumType.STRING)
    private AdCategory category;
    @Enumerated(EnumType.STRING)
    private ProductCondition condition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "province_id", nullable = false)
    private City city;

    @Enumerated(EnumType.STRING)
    private AdStatus status;
    //todo: is it good practice to save user or username?


    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ImageData> images = new ArrayList<>();

    private String rejectionReason; //can be null
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer; //can be null

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;


    public boolean isAdSpammable() {
        return status != AdStatus.SPAM_REPORT;
    }

    public void spam() {
        status = AdStatus.SPAM_REPORT;
    }
}
