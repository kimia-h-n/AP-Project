package com.example.sales.rating;


import com.example.sales.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SellerRating {
    @Id
    @GeneratedValue
    private Long id;

    private Integer rating; //1->5
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
}
