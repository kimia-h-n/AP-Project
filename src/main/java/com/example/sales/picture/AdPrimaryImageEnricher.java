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

@Component
@RequiredArgsConstructor
public class AdPrimaryImageEnricher {

    private static final String IMAGE_ENDPOINT = "/api/v1/images/";

    private final StorageRepository storageRepository;

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
                    imageId == null
                            ? null
                            : IMAGE_ENDPOINT + imageId
            );
        }
    }
}

