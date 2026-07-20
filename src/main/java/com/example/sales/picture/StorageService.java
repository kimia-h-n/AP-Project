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
        return imageRepository.findByAdIdOrderBySortOrderAsc(adId).size();
    }


    @Transactional(readOnly = true)
    public ImageDownload download(UUID imageId) {
        ImageData image = imageRepository.findById(imageId).orElseThrow(ImageNotFoundException::new);
        byte[] data = ImageUtils.decompressImage(image.getImageData());
        return new ImageDownload(data, image.getType());
    }

//    public void uploadImage(MultipartFile file) throws IOException {
//        ImageData imageData = imageRepository.save(
//                ImageData.builder()
//                        .name(file.getOriginalFilename())
//                        .type(file.getContentType())
//                        .imageData(ImageUtils.compressImage(file.getBytes())).build() // we don't want to save the hard coded file here, we first need to decompress it.
//        );
//        if (imageData == null)
//            throw new UploadException();
//    }
//    public byte[] downloadImage(String name) {
//        ImageData imageData = imageRepository.findByName(name).orElseThrow(ImageNotFoundException::new);
//        return ImageUtils.decompressImage(imageData.getImageData());
//    }
}
