package com.example.sales.picture;

import java.util.UUID;
import java.util.UUID;

/**
 * Projection view exposing metadata for an image without loading the full entity.
 * <p>
 * This interface is used by repository queries to fetch lightweight image details.
 * </p>
 */
public interface ImageMetaView {

    /**
     * Returns the image identifier.
     *
     * @return image UUID
     */
    UUID getId();

    /**
     * Returns the image file name.
     *
     * @return image name
     */
    String getName();

    /**
     * Returns the identifier of the related ad.
     *
     * @return ad ID
     */
    Long getAdId();

    /**
     * Returns the image MIME type.
     *
     * @return image type
     */
    String getType();

    /**
     * Returns the display order of the image.
     *
     * @return sort order
     */
    Integer getSortOrder();

    /**
     * Indicates whether this image is marked as primary.
     *
     * @return true if primary image, otherwise false
     */
    boolean isPrimaryImage();
}
