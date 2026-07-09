package com.example.sales.ad.model;

import com.example.sales.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @Enumerated(EnumType.STRING)
    private City city;

    @ElementCollection
    private List<String> imagePaths;

    @Enumerated(EnumType.STRING)
    private AdStatus status;
    //todo: is it good practice to save user or username?

    private String rejectionReason; //can be null
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer; //can be null


}
