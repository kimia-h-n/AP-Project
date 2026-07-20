package com.example.sales.picture;


import com.example.sales.ad.AdRepository;
import com.example.sales.ad.model.Ad;
import com.example.sales.exception.*;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class StorageService {

    private final AdRepository adRepository;
    private final StorageRepository imageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ImageData upload(Long adId, MultipartFile file, String username) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new InvalidDataFormat();

        Ad ad = adRepository.findById(adId).orElseThrow(AdNotFoundException::new);
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (!(ad.getSeller().getId().equals(user.getId())))
            throw new OperationNotAllowedException();
        boolean isFirstImage = !imageRepository.existsByAdId(adId);

        ImageData image = ImageData.builder()
                .name(file.getOriginalFilename())
                .type(contentType)
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .sortOrder(nextOrder(adId))
                .primaryImage(isFirstImage)
                .ad(ad)
                .build();

        return imageRepository.save(image);
    }

    private int nextOrder(Long adId) {
        return imageRepository.findMaxSortOrderByAdId(adId) + 1;
    }


    @Transactional(readOnly = true)
    public ImageDownload download(UUID imageId) {
        ImageData image = imageRepository.findById(imageId).orElseThrow(ImageNotFoundException::new);
        byte[] data = ImageUtils.decompressImage(image.getImageData());
        return new ImageDownload(data, image.getType());
    }

    @Transactional
    public void removeImage(UUID imageId, String username) {
        ImageData image = imageRepository.findById(imageId)
                .orElseThrow(ImageNotFoundException::new);

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (!image.getAd().getSeller().getId().equals(user.getId()))
            throw new OperationNotAllowedException();

        Long adId = image.getAd().getId();
        int removedOrder = image.getSortOrder();
        boolean wasPrimary = image.isPrimaryImage();

        imageRepository.delete(image);

        List<ImageData> images = imageRepository.findByAdIdOrderBySortOrderAsc(adId);

        for (ImageData img : images) {
            if (img.getSortOrder() > removedOrder) {
                img.setSortOrder(img.getSortOrder() - 1);
            }
            img.setPrimaryImage(false);
        }

        if (wasPrimary && !images.isEmpty()) {
            images.getFirst().setPrimaryImage(true);
        }
    }

    @Transactional
    public void replaceImage(UUID imageId, MultipartFile file, String username) throws IOException {
        ImageData oldImage = imageRepository.findById(imageId)
                .orElseThrow(ImageNotFoundException::new);

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (!oldImage.getAd().getSeller().getId().equals(user.getId()))
            throw new OperationNotAllowedException();

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new InvalidDataFormat();

        oldImage.setName(file.getOriginalFilename());
        oldImage.setType(contentType);
        oldImage.setImageData(ImageUtils.compressImage(file.getBytes()));

        imageRepository.save(oldImage);
    }
}
