package com.example.sales.picture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link ImageData} persistence and image metadata queries.
 */
public interface StorageRepository extends JpaRepository<ImageData, UUID> {

    /**
     * Finds all images for the given ad ordered by sort order ascending.
     *
     * @param adId advertisement identifier
     * @return ordered list of image entities
     */
    List<ImageData> findByAdIdOrderBySortOrderAsc(Long adId);

    /**
     * Finds a specific image for an ad by its identifier.
     *
     * @param adId advertisement identifier
     * @param id   image identifier
     * @return matching image if present
     */
    Optional<ImageData> findByAdIdAndId(Long adId, UUID id);

    /**
     * Finds primary image metadata for a collection of ads.
     *
     * @param adIds collection of advertisement identifiers
     * @return list of primary image projections
     */
    @Query("""
            select i.id as id, i.ad.id as adId
            from ImageData i
            where i.ad.id in :adIds
              and i.primaryImage = true
            """)
    List<ImageMetaView> findPrimaryMetaByAdIdIn(@Param("adIds") Collection<Long> adIds);

    /**
     * Clears the primary image flag for all images of the specified ad.
     *
     * @param adId advertisement identifier
     */
    @Modifying
    @Query("""
            update ImageData i
            set i.primaryImage = false
            where i.ad.id = :adId
              and i.primaryImage = true
            """)
    void clearPrimaryForAd(@Param("adId") Long adId);

    /**
     * Checks whether any image exists for the given ad.
     *
     * @param adId advertisement identifier
     * @return true if at least one image exists, otherwise false
     */
    boolean existsByAdId(Long adId);

    /**
     * Returns lightweight metadata for all images of the given ad.
     *
     * @param adId advertisement identifier
     * @return list of image metadata projections ordered by sort order
     */
    @Query("""
            select i.id as id, i.name as name, i.type as type,
                   i.sortOrder as sortOrder, i.primaryImage as primaryImage
            from ImageData i
            where i.ad.id = :adId
            order by i.sortOrder asc
            """)
    List<ImageMetaView> findMetaByAdId(@Param("adId") Long adId);

    /**
     * Returns the maximum sort order for images of the specified ad.
     *
     * @param adId advertisement identifier
     * @return maximum sort order, or -1 if no images exist
     */
    @Query("select coalesce(max(i.sortOrder), -1) from ImageData i where i.ad.id = :adId")
    int findMaxSortOrderByAdId(Long adId);
}
