package com.example.sales.picture;

import com.example.sales.ad.Ad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * JPA entity that stores binary image data associated with an advertisement.
 * <p>
 * Each image can be ordered within an ad and marked as the primary image.
 * </p>
 */
@Data
@Entity
@Table(name = "image_data")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageData {

    /**
     * Unique identifier of the image.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Original file name of the image.
     */
    private String name;

    /**
     * MIME type of the image.
     */
    private String type;

    /**
     * Sort order used when displaying multiple images for the same ad.
     */
    private Integer sortOrder = 0;

    /**
     * Compressed binary image content.
     */
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "image", columnDefinition = "bytea", nullable = false)
    private byte[] imageData;

    /**
     * Advertisement to which this image belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    /**
     * Indicates whether this image is the primary image for the ad.
     */
    @Column(name = "is_primary", nullable = false)
    private boolean primaryImage = false;
}
