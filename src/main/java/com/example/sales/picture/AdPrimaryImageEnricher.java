package com.example.sales.picture;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enriches ad-related DTOs or projections with primary image metadata.
 * <p>
 * This component resolves the primary image for each ad from the storage repository
 * and writes both the image ID and the image URL into the provided items.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class AdPrimaryImageEnricher {

    private static final String IMAGE_ENDPOINT = "/api/v1/images/";

    private final StorageRepository storageRepository;

    /**
     * Enriches the given items with primary image information.
     *
     * @param items the items to enrich
     * @param adIdExtractor function used to extract the ad ID from each item
     * @param imageIdSetter callback used to write the primary image ID into an item
     * @param imageUrlSetter callback used to write the primary image URL into an item
     * @param <T> the item type
     */
    public <T> void enrich(
            List<T> items,
            Function<T, Long> adIdExtractor,
            BiConsumer<T, UUID> imageIdSetter,
            BiConsumer<T, String> imageUrlSetter
    ) {
        if (items == null || items.isEmpty()) {
            return;
        }

        List<Long> adIds = items.stream()
                .map(adIdExtractor)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (adIds.isEmpty()) {
            return;
        }

        Map<Long, UUID> primaryByAdId =
                storageRepository.findPrimaryMetaByAdIdIn(adIds)
                        .stream()
                        .collect(Collectors.toMap(
                                ImageMetaView::getAdId,
                                ImageMetaView::getId,
                                (existing, ignored) -> existing
                        ));

        for (T item : items) {
            Long adId = adIdExtractor.apply(item);
            UUID imageId = primaryByAdId.get(adId);

            imageIdSetter.accept(item, imageId);

            imageUrlSetter.accept(
                    item,
                    imageId == null ? null : IMAGE_ENDPOINT + imageId
            );
        }
    }
}
