package com.example.sales.ad.fav;

import com.example.sales.ad.model.Ad;
import com.example.sales.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteAd, Long> {

    void deleteByUserAndAd(User user, Ad ad);

    boolean existsFavoriteAdByUserAndAd(User user, Ad ad);

    List<FavoriteAd> getAllByUser_Username(String userUsername);

    @Query("select f.ad.id from FavoriteAd f where f.user = :user")
    Set<Long> findFavoriteAdIdsByUser(@Param("user") User user);

}
