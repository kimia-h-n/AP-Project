package com.example.sales.picture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StorageRepository extends JpaRepository<ImageData, UUID> {
    List<ImageData> findByAdIdOrderBySortOrderAsc(Long adId);

    Optional<ImageData> findByAdIdAndId(Long adId, UUID id);

    @Query("""
            select i.id as id, i.ad.id as adId
            from ImageData i
            where i.ad.id in :adIds
              and i.primaryImage = true
            """)
    List<ImageMetaView> findPrimaryMetaByAdIdIn(@Param("adIds") Collection<Long> adIds);

    @Modifying
    @Query("""
            update ImageData i
            set i.primaryImage = false
            where i.ad.id = :adId
              and i.primaryImage = true
            """)
    void clearPrimaryForAd(@Param("adId") Long adId);

    boolean existsByAdId(Long adId);

    @Query("""
            select i.id as id, i.name as name, i.type as type,
                   i.sortOrder as sortOrder, i.primaryImage as primaryImage
            from ImageData i
            where i.ad.id = :adId
            order by i.sortOrder asc
            """)
    List<ImageMetaView> findMetaByAdId(@Param("adId") Long adId);
}
