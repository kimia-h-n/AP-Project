package com.example.sales.picture;

import java.util.UUID;

public interface ImageMetaView {
    UUID getId();

    String getName();

    Long getAdId();

    String getType();

    Integer getSortOrder();

    boolean isPrimaryImage();
}