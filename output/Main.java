import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.awt.datatransfer.DataFlavor;
import java.beans.Transient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.annotation.processing.Generated;

class AdSpecifications {
    public static Specification<Ad> hasStatus(AdStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Tehran");

    public static Specification<Ad> hasDateFilter(DateFilter dateFilter) {
        return (root, query, cb) -> {
            if (dateFilter == null) {
                return cb.conjunction();
            }

            LocalDate today = LocalDate.now(APP_ZONE);

            Instant startOfToday = today.atStartOfDay(APP_ZONE).toInstant();
            Instant startOfYesterday = today.minusDays(1).atStartOfDay(APP_ZONE).toInstant();
            Instant startOfSevenDaysAgo = today.minusDays(7).atStartOfDay(APP_ZONE).toInstant();

            return switch (dateFilter) {
                case YESTERDAY -> cb.and(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), startOfYesterday),
                        cb.lessThan(root.get("createdAt"), startOfToday)
                );

                case PAST_WEEK -> cb.and(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), startOfSevenDaysAgo),
                        cb.lessThan(root.get("createdAt"), startOfToday)
                );

                case OLDER -> cb.lessThan(root.get("createdAt"), startOfSevenDaysAgo);
            };
        };
    }

    public static Specification<Ad> hasCategory(AdCategory category) {
        return (root, query, cb) ->
                category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
    }

    public static Specification<Ad> hasCityId(Long cityId) {
        return (root, query, cb) ->
                cityId == null ? cb.conjunction() : cb.equal(root.get("city").get("id"), cityId);
    }

    public static Specification<Ad> priceGte(Long minPrice) {
        return (root, query, cb) ->
                minPrice == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Ad> priceLte(Long maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Ad> priceBetween(Long minPrice, Long maxPrice) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (minPrice != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return predicate;
        };
    }
}

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
class AdController {
    private final AdService adService;
    private final StorageService storageService;

    @PostMapping("/ads")
    public AdInsertResponse addAdvertisement(@RequestBody AdRequest request, Authentication authentication) {
        String username = authentication.getName();
        return adService.addAd(request, username);
    }

    //sample use case: GET /api/v1/ads?status=APPROVED
    //for now only gets active ads
    @GetMapping("/ads")
    public List<AdCartSummery> getAllAds(Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.getAllActiveAds(username);
    }

    @GetMapping("/ads/{id}")
    public AdResponse getAd(@PathVariable Long id, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.getAd(id, username);
    }

    @GetMapping("/me/ads/")
    public List<AdResponse> getAllMyAds(Authentication authentication) {
        String username = authentication.getName();
        return adService.getAllMyAds(username);
    }

    @PatchMapping("/ads/{id}")
    public void updateAd(@PathVariable Long id, @RequestBody AdUpdateRequest adRequest, Authentication authentication) {
        String username = authentication.getName();
        adService.updateAd(id, username, adRequest);
    }


    @DeleteMapping("/ads/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAd(@PathVariable Long id, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        adService.removeAd(id, username);
    }

    /**
     * If token is sent, it means that the user has logged in and proper adjustments must be made
     *
     * @param authentication
     * @return username or null if not logged in.
     */
    private String extractUsernameIfLoggedIn(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return "anonymousUser".equals(username) ? null : username;
    }

    @GetMapping("/search")
    public List<AdCartSummery> searchByTitle(@RequestParam String title, Authentication authentication) {
        String username = extractUsernameIfLoggedIn(authentication);
        return adService.searchByTitle(username, title);
    }

    @GetMapping("/filter")
    public List<AdCartSummery> searchAds(
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) AdCategory category,
            @RequestParam(required = false) DateFilter dataFilter,
            @RequestParam(required = false) Long cityId
    ) {
        return adService.searchAds(minPrice, maxPrice, category, dataFilter, cityId);
    }

    @PostMapping(value = "/ads/{adId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @PathVariable Long adId,
            @RequestPart("files") List<MultipartFile> files, Authentication authentication) throws IOException {
        log.info("UPLOAD IMAGE -> adId={}, fileCount={}, authPresent={}",
                adId,
                files != null ? files.size() : 0,
                authentication != null);

        if (authentication != null) {
            log.info("UPLOAD IMAGE -> username={}", authentication.getName());
        }

        List<UUID> ids = new ArrayList<>();
        String username = authentication.getName();
        for (MultipartFile file : files) {
            ImageData saved = storageService.upload(adId, file, username);
            ids.add(saved.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ids);
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> download(@PathVariable UUID imageId) {
        ImageDownload result = storageService.download(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(result.data());
    }

    @DeleteMapping("/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeImage(@PathVariable UUID imageId, Authentication authentication) {
        String username = authentication.getName();
        storageService.removeImage(imageId, username);
    }

    @PutMapping(value = "/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> replaceImage(
            @PathVariable UUID imageId,
            @RequestPart("files") MultipartFile file,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        storageService.replaceImage(imageId, file, username);
        return ResponseEntity.ok(imageId);
    }


}

@Repository
interface AdRepository extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {

    List<Ad> findAllByStatus(AdStatus status);

    List<Ad> findAllBySeller(User seller);

    List<Ad> findByTitleContainingIgnoreCaseAndStatus(String title, AdStatus status);

    boolean existsById(Long id);
}

@Service
@AllArgsConstructor
class AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final StorageRepository storageRepository;
    private final ProvinceRepository provinceRepository;
    private final AdMapper adMapper;

    public AdInsertResponse addAd(AdRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Ad ad = adMapper.toEntity(request);
        ad.setCity(provinceRepository.findById(request.getCityId()).orElseThrow(CityNotFoundException::new));
        ad.setSeller(user);
        ad.setStatus(AdStatus.PENDING);
        Ad savedAd = adRepository.save(ad);
        return new AdInsertResponse(savedAd.getId());
    }


    public List<AdCartSummery> getAllActiveAds(String username) {
//        applyFavorite(username, activeAds);
//        return adMapper.toCartSummeryList(adRepository.findAllByStatus(AdStatus.APPROVED));
        List<AdCartSummery> ads = adMapper.toCartSummeryList
                (adRepository.findAllByStatus(AdStatus.APPROVED));
        addPrimaryImage(ads);
        return ads;
    }

    public void addPrimaryImage(List<AdCartSummery> ads) {
        List<Long> adIds = ads.stream()
                .map(AdCartSummery::getId)
                .toList();

        Map<Long, UUID> primaryByAdId = storageRepository
                .findPrimaryMetaByAdIdIn(adIds)
                .stream()
                .collect(Collectors.toMap(
                        ImageMetaView::getAdId,
                        ImageMetaView::getId,
                        (existing, ignored) -> existing
                ));
        for (AdCartSummery ad : ads) {
            UUID primaryImageId = primaryByAdId.get(ad.getId());
            ad.setPrimaryImageId(primaryImageId);
            ad.setPrimaryImageUrl(primaryImageId != null ? "/api/v1/images/" + primaryImageId : null);

        }
    }

    private void applyFavorite(String username, List<AdResponse> responseList) {
        if (isNotLoggedIn(username)) {
            for (AdResponse response : responseList) {
                response.setFavorite(false);
                response.setMine(false);
            }
            return;
        }
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Set<Long> favAdIds = favoriteRepository.findFavoriteAdIdsByUser(user);
        responseList.forEach(ad -> {
            ad.setFavorite(favAdIds.contains(ad.getId()));
            ad.setMine(ad.getSellerUsername().equals(username));
        });
    }

    private static boolean isNotLoggedIn(String username) {
        return username == null;
    }

    public AdResponse getAd(Long id, String username) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
        AdResponse adResponse = adMapper.toResponse(ad);
        adResponse.setImages(buildImageResponses(id));
        if (isNotLoggedIn(username))
            return adResponse;

        //todo: if in future, viewing rejected ads is desired, change here
        //todo: is it neccessary to check this here?
//        if (ad.getStatus() != AdStatus.APPROVED)
//            throw new AdViewNotAllowedException();

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        boolean isFavorite = favoriteRepository.existsFavoriteAdByUserAndAd(user, ad);
        adResponse.setFavorite(isFavorite);
        adResponse.setMine(username.equals(adResponse.getSellerUsername()));

        return adResponse;
    }

    private List<ImageResponse> buildImageResponses(Long adId) {
        return storageRepository.findMetaByAdId(adId).stream()
                .map(m -> new ImageResponse(
                        m.getId(),
                        "/api/v1/images/" + m.getId(),
                        m.getSortOrder(),
                        m.isPrimaryImage()))
                .toList();
    }

    private boolean isAlreadyRemoved(Ad ad) {
        return ad.getStatus() == AdStatus.REMOVED;
    }

    private boolean isAdmin(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new)
                .getRole() == Role.ADMIN;
    }

    @Transactional
    public void removeAd(Long id, String username) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);

        if (!(isAdOwner(username, ad) || isAdmin(username)))
            throw new OperationNotAllowedException();

        if (isAlreadyRemoved(ad))
            throw new AdNotRemovableException();

        ad.setStatus(AdStatus.REMOVED);

    }

    private static boolean isAdOwner(String username, Ad ad) {
        return ad.getSeller().getUsername().equals(username);
    }


    public List<AdResponse> getAllMyAds(String username) {
        List<Ad> myAds = adRepository.findAllBySeller(
                userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new));
        List<AdResponse> ads = adMapper.toResponseList(myAds);
        for (int i = 0; i < ads.size(); i++) {
            ads.get(i).setMine(true);
            ads.get(i).setImages(buildImageResponses(myAds.get(i).getId()));
        }
        return ads;
    }

    public void updateAd(Long id, String username, AdUpdateRequest request) {
        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
        if (!isAdOwner(username, ad))
            throw new OperationNotAllowedException();

        if (updateAdFields(request, ad))
            adRepository.save(ad);
    }

    private boolean updateAdFields(AdUpdateRequest request, Ad ad) {
        boolean contentChanged = false;

        if (request.getTitle() != null && !request.getTitle().equals(ad.getTitle())) {
            ad.setTitle(request.getTitle());
            contentChanged = true;
        }

        if (request.getDescription() != null && !request.getDescription().equals(ad.getDescription())) {
            ad.setDescription(request.getDescription());
            contentChanged = true;
        }

        if (request.getAddress() != null && !request.getAddress().equals(ad.getAddress())) {
            ad.setAddress(request.getAddress());
            contentChanged = true;
        }

        if (request.getPrice() != null && request.getPrice() != ad.getPrice()) {
            ad.setPrice(request.getPrice());
            contentChanged = true;
        }

        if (request.getCategory() != null && request.getCategory() != ad.getCategory()) {
            ad.setCategory(request.getCategory());
            contentChanged = true;
        }

        if (request.getCondition() != null && request.getCondition() != ad.getCondition()) {
            ad.setCondition(request.getCondition());
            contentChanged = true;
        }

        if (request.getCityId() != null && !Objects.equals(request.getCityId(), ad.getCity().getId())) {
            ad.setCity(provinceRepository.findById(request.getCityId())
                    .orElseThrow(CityNotFoundException::new));
            contentChanged = true;
        }

        if (contentChanged && ad.getStatus() == AdStatus.APPROVED) {
            ad.setStatus(AdStatus.PENDING);
        }

        return contentChanged;
    }

    public List<AdCartSummery> searchByTitle(String username, String title) {
//        List<AdResponse> matchedAds = adMapper.toResponseList(
//                adRepository.findAll(AdSpecification.titleContains(title)));
//        applyFavorite(username, matchedAds);
        //todo: for later search improvements such as matching with description
//         return adMapper.toCartSummeryList(adRepository.findAll(AdSpecification.titleContains(title)));
        List<AdCartSummery> ads = adMapper.toCartSummeryList(adRepository.findByTitleContainingIgnoreCaseAndStatus(title, AdStatus.APPROVED));
        addPrimaryImage(ads);
        return ads;
    }

    public List<AdCartSummery> searchAds(Long minPrice, Long maxPrice, AdCategory category,
                                         DateFilter dateFilter, Long cityId) {
        Specification<Ad> spec = Specification.where(AdSpecifications.hasStatus(AdStatus.APPROVED));

        if (minPrice != null || maxPrice != null) {
            spec = spec.and(AdSpecifications.priceBetween(minPrice, maxPrice));
        }
        if (category != null) {
            spec = spec.and(AdSpecifications.hasCategory(category));
        }
        if (cityId != null) {
            spec = spec.and(AdSpecifications.hasCityId(cityId));
        }
        if (dateFilter != null) {
            spec = spec.and(AdSpecifications.hasDateFilter(dateFilter));
        }

        List<AdCartSummery> ads = adMapper.toCartSummeryList(adRepository.findAll(spec));
        addPrimaryImage(ads);
        return ads;
    }
}

enum DateFilter {
    YESTERDAY,
    PAST_WEEK,
    OLDER
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "favorites")
class FavoriteAd {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;
}

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/favorites")
class FavoriteAdController {

    private final FavoriteAdService favAdService;

    @PostMapping("/{adId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addToFavorites(@PathVariable Long adId, Authentication authentication) {
        String username = authentication.getName();
        favAdService.addToFavorites(adId, username);
    }

    @GetMapping
    public List<AdCartSummery> getAllUserFavoriteAds(Authentication authentication) {
        String username = authentication.getName();
        return favAdService.getAllUserFavoriteAds(username);
    }

    @DeleteMapping("/{adId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFromFavorites(@PathVariable Long adId, Authentication authentication) {
        String username = authentication.getName();
        favAdService.removeFromFavorites(adId, username);
    }

}

@Service
@AllArgsConstructor
class FavoriteAdService {
    private final FavoriteRepository favRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AdService adService;

    public void addToFavorites(Long adId, String username) {
        RequestInfo requestInfo = getRequestInfo(adId, username);

        if (isAdFavorite(requestInfo))
            throw new AlreadyFavoriteAdException();

        FavoriteAd favoriteAd = FavoriteAd.builder()
                .user(requestInfo.user())
                .ad(requestInfo.ad())
                .build();
        favRepository.save(favoriteAd);
    }

    public void removeFromFavorites(Long adId, String username) {
        RequestInfo requestInfo = getRequestInfo(adId, username);

        if (!isAdFavorite(requestInfo))
            throw new AdNotFavoriteException();

        favRepository.deleteByUserAndAd(requestInfo.user(), requestInfo.ad());

    }

    private boolean isAdFavorite(RequestInfo requestInfo) {
        return favRepository.existsFavoriteAdByUserAndAd(requestInfo.user(), requestInfo.ad());
    }

    private RequestInfo getRequestInfo(Long adId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Ad ad = adRepository.findById(adId).orElseThrow(AdNotFoundException::new);
        return new RequestInfo(user, ad);
    }

    //todo: consider using Pageable if the list of favorites are too long
    public List<AdCartSummery> getAllUserFavoriteAds(String username) {
        if (!userRepository.existsByUsername(username))
            throw new UserNotFoundException();
        List<AdCartSummery> ads = adMapper.toCartSummeryFromFavorites(favRepository.getAllByUser_Username(username));
        adService.addPrimaryImage(ads); //todo: better way of avoiding duplication
        return ads;
    }

    private record RequestInfo(User user, Ad ad) {
    }


}

@Repository
interface FavoriteRepository extends JpaRepository<FavoriteAd, Long> {

    void deleteByUserAndAd(User user, Ad ad);

    boolean existsFavoriteAdByUserAndAd(User user, Ad ad);

    List<FavoriteAd> getAllByUser_Username(String userUsername);

    @Query("select f.ad.id from FavoriteAd f where f.user = :user")
    Set<Long> findFavoriteAdIdsByUser(@Param("user") User user);

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ads")
class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String address;
    private long price;
    @Enumerated(EnumType.STRING)
    private AdCategory category;
    @Enumerated(EnumType.STRING)
    private ProductCondition condition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "province_id", nullable = false)
    private City city;

    @Enumerated(EnumType.STRING)
    private AdStatus status;
    //todo: is it good practice to save user or username?


    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ImageData> images = new ArrayList<>();

    private String rejectionReason; //can be null
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer; //can be null

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;


    public boolean isAdSpammable() {
        return status != AdStatus.SPAM_REPORT;
    }

    public void spam() {
        status = AdStatus.SPAM_REPORT;
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AdCartSummery {
    private Long id;
    private String title;
    private long price;
    private Instant createdAt;
    private Instant updatedAt;
    private String cityName;
    private AdCategory category;
    private UUID primaryImageId;
    private String primaryImageUrl;

}

enum AdCategory {
    PROPERTY, VEHICLE, ELECTRONIC, HOME, SERVICE,
    PERSONAL, HOBBIT, SOCIAL, INDUSTRIAL, JOB,
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AdInsertResponse {
    Long id;
}

@Mapper(componentModel = "spring")
interface AdMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    Ad toEntity(AdRequest request);

    @Mapping(target = "sellerFirstname", source = "seller.firstname")
    @Mapping(target = "sellerLastname", source = "seller.lastname")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerUsername", source = "seller.username")
    @Mapping(target = "favorite", ignore = true)
    @Mapping(target = "cityName", source = "city.name")
    AdResponse toResponse(Ad ad);


    @Mapping(target = "sellerFirstname", source = ("seller.firstname"))
    @Mapping(target = "sellerLastname", source = "seller.lastname")
    @Mapping(target = "adTitle", source = "ad.title")
    @Mapping(target = "adReportId", source = "id")
    @Mapping(target = "adId", source = "ad.id")
    List<AdReportResponse> toAdReportResponse(List<AdReport> ads);

    @Mapping(target = "cityName", source = "city.name")
    List<AdResponse> toResponseList(List<Ad> ads);

    @Mapping(target = "id", source = "ad.id")
    @Mapping(target = "title", source = "ad.title")
    @Mapping(target = "price", source = "ad.price")
    @Mapping(target = "cityName", source = "ad.city.name")
    @Mapping(target = "category", source = "ad.category")
    @Mapping(target = "createdAt", source = "ad.createdAt")
    @Mapping(target = "updatedAt", source = "ad.updatedAt")
    List<AdCartSummery> toCartSummeryFromFavorites(List<FavoriteAd> ads);

    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "id", source = "id")
    List<AdCartSummery> toCartSummeryList(List<Ad> ads);

    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "sellerFirstname", source = "seller.firstname")
    @Mapping(target = "sellerLastname", source = "seller.lastname")
    @Mapping(target = "sellerId", source = "seller.id")
    List<PendingAd> toPendingAdList(List<Ad> ads);
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

class AdRequest {
    private String title;
    private String description;
    private String address;
    private long price;
    private AdCategory category;
    private ProductCondition condition;
    private Long cityId;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AdResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private long price;
    private AdCategory category;
    private ProductCondition condition;
    private String cityName;
    private List<ImageResponse> images;
    private AdStatus status;
    private String sellerUsername;
    private String sellerFirstname;
    private String sellerLastname;
    private Long sellerId;
    private boolean isFavorite;
    private boolean isMine;
    private Instant createdAt;
    private Instant updatedAt;
}

enum AdStatus {
    PENDING,
    REJECTED,
    REMOVED,
    SPAM_REPORT,
    APPROVED,
    SOLD;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

class AdUpdateRequest {
    private Long id;
    private String title;
    private String description;
    private String address;
    private Long price;
    private AdCategory category;
    private ProductCondition condition;
    private Long cityId;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ImageResponse {
    private UUID id;
    private String url;
    private Integer sortOrder;
    private boolean primary;
}

enum AdModerationChoice {
    APPROVE, REJECT
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class AdModerationRequest {
    private AdModerationChoice choice;
    private String rejectReason;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class PendingAd {
    private Long id;
    private String title;
    private AdCategory category;
    private String sellerFirstName;
    private String sellerLastName;
    private String cityName;
    private Instant createdAt;
    private Instant updatedAt;
    private Long sellerId;
}

enum ProductCondition {
    NEW, ALMOST_NEW, USED
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ad_reports")
class AdReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_reason", nullable = false)
    private ReportReason reason;
//    @Column(name = "description")
//    private String description;
//

}

@Repository
interface AdReportRepository extends JpaRepository<AdReport, Long> {



}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AdReportResponse {
    private Long adReportId;
    private Long adId;
    private String adTitle;
    //todo: later change to UserName
//     private UserName userName;
    private String sellerFirstName;
    private String sellerLastname;
    private ReportReason reportReason;
}

@Controller
@RequestMapping("/api/v1/")
@AllArgsConstructor
class ReportAdController {

    private final ReportAdService reportAdService;

    @PostMapping("report-ad")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reportAd(@RequestParam Long adId,
                         @RequestBody ReportReason reportReason, Authentication authentication) {
        String username = authentication.getName();
        reportAdService.reportAd(adId, reportReason, username);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ReportAdRequest {
    private Long adId;
    private ReportReason reportReason;
}

@Service
@AllArgsConstructor
class ReportAdService {

    private final AdRepository adRepository;
    private final AdReportRepository adReportRepository;
    private final UserRepository userRepository;

    public void reportAd(Long adId, ReportReason reportReason, String username) {
        Ad ad = adRepository.findById(adId).orElseThrow(AdNotFoundException::new);
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (ad.getSeller().getUsername().equals(username) || !ad.isAdSpammable())
            throw new SpamNotAllowedException();
        ad.spam();

        //todo: move to adReportService?
        AdReport adReport = AdReport.builder()
                .reporter(user)
                .reason(reportReason)
                .build();

        adReportRepository.save(adReport);

    }
}

enum ReportReason {
    FRAUD, IMMORAL, WRONG_CATEGORY,
    WRONG_PRICE, WRONG_INFORMATION,
    DUPLICATE, UNAVAILABLE, OTHERS
}

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PostMapping("/users/block/{id}")
    public void blockUser(@PathVariable Long id) {
        adminService.blockUser(id);
    }

    @PostMapping("/users/unblock/{id}")
    public void unblockUser(@PathVariable Long id) {
        adminService.unblockUser(id);
    }

    //only admin can access
    @PostMapping("/ads/{id}/moderation")
    public void moderateAd(@PathVariable Long id, @RequestBody AdModerationRequest moderationRequest) {
        adminService.moderateAd(id, moderationRequest);
    }

    @GetMapping("/reported-ads")
    public List<AdReportResponse> getReportedAds() {
        return adminService.getReportedAds();
    }


    @GetMapping("/ads/moderation")
    public List<PendingAd> getPendingAds() {
        return adminService.getAllPendingAds();
    }
}

@Configuration
@RequiredArgsConstructor
class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner createAdmin() {
        return args -> {
            if (userRepository.existsByUsername(adminUsername))
                return;

            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(null)
                    .phoneNumber(null)
                    .firstname(null)
                    .lastname(null)
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
        };
    }
}

@Service
@AllArgsConstructor
class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdRepository adRepository;
    private final AdReportRepository adReportRepository;
    private final AdMapper adMapper;

    public List<UserResponse> getAllUsers() {
        return userMapper.toUserResponse(userRepository.findAll());
    }


    @Transactional
    public void blockUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (!user.isEnabled())
            throw new AlreadyBlockedException();
        user.setEnabled(false);

    }

    @Transactional
    public void unblockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.isEnabled())
            throw new UserAlreadyEnabled();
        user.setEnabled(true);
    }


    public void moderateAd(Long id, AdModerationRequest moderationRequest) {

        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);

        switch (moderationRequest.getChoice()) {
            case APPROVE -> {
                ad.setStatus(AdStatus.APPROVED);
                ad.setRejectionReason(null);
            }
            case REJECT -> {
                ad.setStatus(AdStatus.REJECTED);
                ad.setRejectionReason(moderationRequest.getRejectReason());
            }
        }
        adRepository.save(ad);
    }

    public List<PendingAd> getAllPendingAds() {
        return adMapper.toPendingAdList(adRepository.findAllByStatus(AdStatus.PENDING));
    }

    public List<AdReportResponse> getReportedAds() {
        List<AdReport> adReportResponses = adReportRepository.findAll();
        return adMapper.toAdReportResponse(adReportResponses);

    }
}

@SpringBootApplication
class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authService.authenticate(request));

    }


}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AuthenticationRequest {
    String username;
    String password;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AuthenticationResponse {
    private String token;
    private Role role;
}

@Service
@RequiredArgsConstructor
class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        } catch (AuthenticationException e) {
            throw new InvalidUsernameOrPassword();
        }
        var user = repository.findByUsername(request.getUsername()).orElseThrow(UserNotFoundException::new);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        String username = request.getUsername(), email = request.getEmail(), phoneNumber = request.getPhoneNumber();
        if (repository.existsByUsername(username))
            throw new DuplicateUsernameException();
        if (repository.existsByPhoneNumber(phoneNumber))
            throw new DuplicatePhoneNumberException();
        if (repository.existsByEmail(email))
            throw new DuplicateEmailException();

        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))//save encoded password
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(email)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .enabled(true)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RegisterRequest {
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String email;
}

@Component
@AllArgsConstructor
class AuthChannelInterceptor implements ChannelInterceptor {
    private JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthChannelInterceptor.class);


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        logger.info("Processing STOMP {} for session {}", command, accessor.getSessionId());

        if (command == null || StompCommand.CONNECTED.equals(command)) {
            return message;
        }

        try {
            String token = accessor.getFirstNativeHeader("Authorization");
            logger.info("Authorization header present: {}", token != null);

            if (StompCommand.CONNECT.equals(command)) {
                if (token == null || !token.startsWith("Bearer ")) {
                    throw new AuthenticationCredentialsNotFoundException("Authorization header missing");
                }

                token = token.substring(7);
                if (!jwtService.validateJwtToken(token)) {
                    throw new BadCredentialsException("Invalid JWT token");
                }

                String username = jwtService.extractUsername(token);

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.emptyList());
                accessor.setUser(auth);

                logger.info("Authenticated user: {}", username);
                return message;
            }

            if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
                Principal user = accessor.getUser();
                if (user == null) {
                    throw new AuthenticationCredentialsNotFoundException("User not authenticated");
                }
                return message;
            }

            return message;
        } catch (Exception e) {
            logger.error("STOMP {} command failed: {}", command, e.getMessage());
            throw new MessageDeliveryException("Authentication failed: " + e.getMessage());
        }
    }
}

@Mapper(componentModel = "spring")
interface ChatMapper {
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "message", source = "message")
    MessageResponse toResponse(ChatMessage chatMessage);

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "message", source = "message")
    List<MessageResponse> toResponseList(List<ChatMessage> messageList);
}

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ChatMessage {
    @Id
    @GeneratedValue
    private Long id;
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private Instant sentAt;
}

@Repository


interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
            SELECT m
            FROM ChatMessage m
            WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id)
               OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id)
            ORDER BY m.sentAt ASC
            """)
    List<ChatMessage> findChatMessagesBetweenUsers(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );

}

@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") //any request from front end, must start here
                .setAllowedOriginPatterns("*")
                .withSockJS(); //in case of web socket not working, try SockJS
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
        //any request from client, first must go through authChannelInterceptor
    }
}

@Controller
@NoArgsConstructor
@AllArgsConstructor

@RequestMapping("api/v1")
class ChatController {
    private ChatService chatService;

    @GetMapping("/conversations")
    public ResponseEntity<?> fetchChat(@RequestParam Long senderId, @RequestParam Long receiverId){
        return ResponseEntity.ok(
                chatService.fetchChat(senderId, receiverId));
    }
}

@RequestMapping("/api/v1/")
@RestController
class SearchController {

    private SearchService searchService;

    @GetMapping("search-user")
    public ResponseEntity<?> searchForUser(@RequestParam String name){
        return ResponseEntity.ok(searchService.searchUser(name));
    }

}

@Controller
@RequestMapping("/api/v1/")
@AllArgsConstructor
class WebSocketController {

    private final WebSocketService webSocketService;


    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload MessageRequest message, Principal principal) {
        webSocketService.sendMessage(message, principal);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class MessageRequest {
    private Long receiverId;
    private String message;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class MessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
}

@Service
@AllArgsConstructor
class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    public List<MessageResponse> fetchChat(Long senderId, Long receiverId) {
        return chatMapper.toResponseList(chatRepository.findChatMessagesBetweenUsers(senderId, receiverId));
    }


    public void saveChat(ChatMessage chatMessage) {
        chatRepository.save(chatMessage);
    }
}

@Service
class SearchService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public List<UserResponse> searchUser(String keyword) {
        List<User> userList = userRepository.searchUsers(keyword);
        return userMapper.toUserResponse(userList);
    }
}

@Service
@AllArgsConstructor
class WebSocketService {

    private final UserRepository userRepository;
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMapper chatMapper;

    public void sendMessage(MessageRequest message, Principal principal) {
        User sender = userRepository.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findById(message.getReceiverId()).orElseThrow(UserNotFoundException::new);

        ChatMessage chatMessage = ChatMessage.builder()
                .sentAt(Instant.now())
                .message(message.getMessage())
                .sender(sender)
                .receiver(receiver)
                .build();

        chatService.saveChat(chatMessage);
        String receiverDestination = "/queue/messages-" + chatMessage.getReceiver().getId();
        simpMessagingTemplate.convertAndSend(receiverDestination, chatMapper.toResponse(chatMessage));
    }
}

@Configuration
@RequiredArgsConstructor
class ApplicationConfig {

    private final UserRepository repository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found"
                ));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}

@Slf4j
@Component
@RequiredArgsConstructor
class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT FILTER -> {} {}", request.getMethod(), request.getRequestURI());
        log.info("Origin: {}", request.getHeader("Origin"));
        log.info("Authorization: {}", request.getHeader("Authorization"));

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //bearer : len is 7
        jwtToken = authHeader.substring(7);
        username = jwtService.extractUsername(jwtToken);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(
                                request
                        )
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}

@Service
class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    public String generateToken(Map<String, Object> extractClaims,
                                UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().
                setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateJwtToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // ───────────────────── Public Endpoints ─────────────────────
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/province").permitAll()
                        .requestMatchers("/chat/**").permitAll()

                        // ───────────────────── Public GET Endpoints ─────────────────
                        .requestMatchers(HttpMethod.GET, "/api/v1/ads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/filter").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/rating/avg/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()

                        // ───────────────────── Swagger / Docs ───────────────────────
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        // ───────────────────── Authenticated Endpoints ──────────────
                        .requestMatchers("/api/v1/favorites/**").authenticated()

                        // ───────────────────── Image Mutations (need token) ─────────
                        .requestMatchers(HttpMethod.POST, "/api/v1/ads/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/images/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/images/**").authenticated()

                        // ───────────────────── Admin ───────────────────────────────
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // ───────────────────── Everything else ─────────────────────
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

@RestController
@RequestMapping("/api/v1/demo-controller")
class DemoController {
    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from secured end point");
    }

}

class AdNotFavoriteException extends BaseException {
    public AdNotFavoriteException() {
        super("Ad not favorite", ErrorCode.AD_NOT_FAVORITE);
    }
}

class AdNotFoundException extends BaseException {
    public AdNotFoundException() {
        super("Ad is not found!", ErrorCode.AD_NOT_FOUND);
    }
}

class AdNotRemovableException extends BaseException {
    public AdNotRemovableException() {
        super("Ad not removable", ErrorCode.AD_NOT_REMOVABLE);
    }
}

class AdViewNotAllowedException extends BaseException {
    public AdViewNotAllowedException() {
        super("Ad view isn't allowed", ErrorCode.AD_VIEW_NOT_ALLOWED);

    }
}

class AlreadyBlockedException extends BaseException {

    public AlreadyBlockedException() {
        super("User is already blocked!", ErrorCode.USER_ALREADY_BLOCKED);
    }
}

class AlreadyFavoriteAdException extends BaseException {
    public AlreadyFavoriteAdException() {
        super("Ad is already favorite", ErrorCode.AD_ALREADY_FAVORITE);
    }
}

class AlreadyVotedException extends BaseException {
    public AlreadyVotedException() {
        super("User already voted!", ErrorCode.ALREADY_VOTED_ERROR);
    }
}

abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

class CityNotFoundException extends BaseException {
    public CityNotFoundException() {
        super("City wasn't found by id", ErrorCode.CITY_NOT_FOUND);
    }
}

class DuplicateEmailException extends BaseException {
    public DuplicateEmailException() {
        super("Email already exists", ErrorCode.DUPLICATE_EMAIL);
    }
}

class DuplicatePhoneNumberException extends BaseException {
    public DuplicatePhoneNumberException() {
        super("Phone number already exists.", ErrorCode.DUPLICATE_PHONE_NUMBER);
    }
}

class DuplicateUsernameException extends BaseException {
    public DuplicateUsernameException() {
        super("Username already exists.", ErrorCode.DUPLICATE_USERNAME);
    }
}

enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD_OR_USERNAME("INVALID_PASSWORD_OR_USERNAME", HttpStatus.UNAUTHORIZED),
    DUPLICATE_EMAIL("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT),
    DUPLICATE_PHONE_NUMBER("PHONE_NUMBER_ALREADY_EXISTS", HttpStatus.CONFLICT),
    DUPLICATE_USERNAME("USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT),

    UNAUTHORIZED_SENDER("UNAUTHORIZED_SENDER", HttpStatus.UNAUTHORIZED),

    USER_ALREADY_BLOCKED("USER_ALREADY_BLOCKED", HttpStatus.CONFLICT),

    USER_ALREADY_ENABLED("USER_ALREADY_ENABLED", HttpStatus.CONFLICT),

    AD_NOT_FOUND("AD_NOT_FOUND", HttpStatus.NOT_FOUND),

    AD_ALREADY_FAVORITE("AD_ALREADY_FAVORITE", HttpStatus.CONFLICT),

    AD_NOT_FAVORITE("AD_NOT_FAVORITE", HttpStatus.BAD_REQUEST),

    AD_NOT_REMOVABLE("AD_NOT_REMOVABLE", HttpStatus.CONFLICT),

    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", HttpStatus.FORBIDDEN),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    ALREADY_VOTED_ERROR("ALREADY_VOTED_ERROR", HttpStatus.FORBIDDEN),


    SPAM_NOT_ALLOWED("SPAM_NOT_ALLOWED", HttpStatus.FORBIDDEN),
    CITY_NOT_FOUND("CITY_NOT_FOUND", HttpStatus.NOT_FOUND),
    INVALID_DATA_FORMAT("INVALID_DATA_FORMAT", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAILED("IMAGE_UPLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_NOT_FOUND("IMAGE_NOT_FOUND", HttpStatus.NOT_FOUND),
    AD_VIEW_NOT_ALLOWED("NOT_ALLOWED_AD_VIEW", HttpStatus.FORBIDDEN);


    private final String label;
    private final HttpStatus status;

    ErrorCode(String label, HttpStatus status) {
        this.label = label;
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime timestamp,
        int status,
        String message,
        String error) {
}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleError(BaseException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getStatus().value(),
                ex.getMessage(),
                errorCode.getLabel()
        );

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}

class ImageNotFoundException extends BaseException{
    public ImageNotFoundException(){
        super("Image wasn't found", ErrorCode.IMAGE_NOT_FOUND);
    }
}

class InvalidDataFormat extends BaseException {
    public InvalidDataFormat() {
        super("Data format is invalid", ErrorCode.INVALID_DATA_FORMAT);
    }

}

class InvalidUsernameOrPassword extends BaseException {

    public InvalidUsernameOrPassword() {
        super("Password or username is invalid", ErrorCode.INVALID_PASSWORD_OR_USERNAME);
    }
}

class OperationNotAllowedException extends BaseException {
    public OperationNotAllowedException() {
        super("Operation not allowed", ErrorCode.OPERATION_NOT_ALLOWED);
    }
}

class SpamNotAllowedException extends BaseException {
    public SpamNotAllowedException() {
        super("Spam is not allowed", ErrorCode.SPAM_NOT_ALLOWED);
    }
}

class UnauthorizedSender extends BaseException {

    public UnauthorizedSender() {
        super("Unauthorized message sender", ErrorCode.UNAUTHORIZED_SENDER);
    }

}

class UploadException extends BaseException{
    public UploadException(){
        super("An error occurred during image upload", ErrorCode.IMAGE_UPLOAD_FAILED);
    }
}

class UserAlreadyEnabled extends BaseException {
    public UserAlreadyEnabled() {
        super("User is already enabled!", ErrorCode.USER_ALREADY_ENABLED);
    }
}

class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super("User is not found!", ErrorCode.USER_NOT_FOUND);
    }
}

@Data
@Entity
@Table(name = "image_data")
@AllArgsConstructor
@NoArgsConstructor
@Builder
class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String type;
    private Integer sortOrder = 0;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "image", columnDefinition = "bytea", nullable = false)
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryImage = false;
}

record ImageDownload(byte[] data, String contentType) {
}

interface ImageMetaView {
    UUID getId();

    String getName();

    Long getAdId();

    String getType();

    Integer getSortOrder();

    boolean isPrimaryImage();
}

class ImageUtils {


    public static byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }



    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

}

//package com.example.sales.picture;
//
//
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("/api/v1/image")
//class StorageController {
//    private final StorageService service;
//
//
//    @Transactional
//    @PostMapping
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void uploadImages(@RequestParam("image") MultipartFile file) throws IOException {
//        service.uploadImage(file);
//    }
//
//    @Transactional(readOnly = true)
//    @GetMapping("/{filename}")
//    public ResponseEntity<?> downloadImage(@PathVariable String filename) {
//        System.out.println("Here");
//        byte[] image = service.downloadImage(filename);
//        return ResponseEntity.status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/png"))
//                .body(image);
//    }
//}

interface StorageRepository extends JpaRepository<ImageData, UUID> {
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

    @Query("select coalesce(max(i.sortOrder), -1) from ImageData i where i.ad.id = :adId")
    int findMaxSortOrderByAdId(Long adId);
}

@Service
@AllArgsConstructor
class StorageService {

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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
class City {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String label;

    private String name; //persion
    /** example:{
     * id = 1
     * label = Tehran
     * name = تهران
     **/
}

@Configuration
@RequiredArgsConstructor
class CityDataInitializer {

    private final ProvinceRepository cityRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public CommandLineRunner loadCities() {
        return args -> {
            if (cityRepository.count() > 0) {
                return;
            }

            InputStream inputStream = new ClassPathResource("data/cities.json").getInputStream();

            List<CitySeedDto> cityDtos = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<CitySeedDto>>() {}
            );

            List<City> cities = cityDtos.stream()
                    .map(dto -> City.builder()
                            .label(dto.getLabel())
                            .name(dto.getName())
                            .build())
                    .toList();

            cityRepository.saveAll(cities);
        };
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CitySeedDto {
    private String label;
    private String name;
}

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/province")
class ProvinceController {

    private final ProvinceService service;

    @GetMapping()
    public List<ProvinceResponse> getAllProvinces(){
        return service.getAllProvinces();
    }

}

@Mapper(componentModel = "spring")
interface ProvinceMapper {

    List<ProvinceResponse> toProvinceResponse(List<City> cityList);
}

@Repository
interface ProvinceRepository extends JpaRepository<City, Long> {

}

@Data
class ProvinceResponse {
    private Long id;
    private String name;
}

@Service
@AllArgsConstructor
class ProvinceService {

    private final ProvinceRepository repository;
    private final ProvinceMapper mapper;

    public List<ProvinceResponse> getAllProvinces() {
        return mapper.toProvinceResponse(repository.findAll());
    }
}

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
class SellerRating {
    @Id
    @GeneratedValue
    private Long id;

    private Integer rating; //1->5
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
}

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
class SellerRatingController {

    private final SellerRatingService service;

    @PostMapping("/rating")
    @ResponseStatus(HttpStatus.OK)
    public void submitVote(@RequestBody SellerRatingRequest request,
                           Authentication authentication) {
        String username = authentication.getName();
        service.submitRating(request, username);
    }

    @GetMapping("/rating/avg/{sellerId}")
    public Double getAverageRating(@PathVariable Long sellerId) {
        return service.calculateSellerRatingAvg(sellerId);
    }


}

@Repository
interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {

    boolean existsBySellerAndUser(User seller, User user);

    List<SellerRating> findAllBySeller(User seller);

}

@Data
class SellerRatingRequest {
    private Long sellerId;
    private Integer rating;
}

@Service
@AllArgsConstructor
class SellerRatingService {

    private final ReportAdService reportAdService;
    SellerRatingRepository ratingRepository;
    UserRepository userRepository;

    //todo: later for editing rating
    public void submitRating(SellerRatingRequest request, String username) {
        //if user has already voted to this person!
        //if user == seller

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User seller = userRepository.findById(request.getSellerId()).orElseThrow(UserNotFoundException::new);

        if (user.getId().equals(seller.getId()))
            throw new OperationNotAllowedException();

        if (ratingRepository.existsBySellerAndUser(seller, user))
            throw new AlreadyVotedException();


        SellerRating sellerRating = SellerRating.builder()
                .seller(seller)
                .user(user)
                .rating(request.getRating())
                .build();

        ratingRepository.save(sellerRating);
    }

    /**
     *
     * @param sellerId
     * @return avgRatingScore for seller, 0.0 if no rating
     */
    public Double calculateSellerRatingAvg(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElseThrow(UserNotFoundException::new);

        List<SellerRating> ratings = ratingRepository.findAllBySeller(seller);

        return ratings.stream()
                .map(SellerRating::getRating)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

}

interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    @Query("""
       SELECT u FROM User u
       WHERE LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    List<User> searchUsers(@Param("keyword") String keyword);
}

enum Role {
    USER,
    ADMIN
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "phone_number"),
        @UniqueConstraint(columnNames = "email")
})
class User implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enabled;

    /*
      Spring:
      Role: USER
      Authority: ROLE_USER
      then for checking, .hasRole("ADMIN")
      or hasAuthority("ROLE_ADMIN")
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String getPassword() {
        return password;
    }
}

@Mapper(componentModel = "spring")
interface UserMapper {
    List<UserResponse> toUserResponse(List<User> users);
}

record UserName (String firstname, String lastname){
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserResponse {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private boolean enable;
}

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T14:59:49+0330",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
class AdMapperImpl implements AdMapper {

    @Override
    public Ad toEntity(AdRequest request) {
        if ( request == null ) {
            return null;
        }

        Ad.AdBuilder ad = Ad.builder();

        ad.title( request.getTitle() );
        ad.description( request.getDescription() );
        ad.address( request.getAddress() );
        ad.price( request.getPrice() );
        ad.category( request.getCategory() );
        ad.condition( request.getCondition() );

        return ad.build();
    }

    @Override
    public AdResponse toResponse(Ad ad) {
        if ( ad == null ) {
            return null;
        }

        AdResponse adResponse = new AdResponse();

        adResponse.setSellerFirstname( adSellerFirstname( ad ) );
        adResponse.setSellerLastname( adSellerLastname( ad ) );
        adResponse.setSellerId( adSellerId( ad ) );
        adResponse.setSellerUsername( adSellerUsername( ad ) );
        adResponse.setCityName( adCityName( ad ) );
        adResponse.setId( ad.getId() );
        adResponse.setTitle( ad.getTitle() );
        adResponse.setDescription( ad.getDescription() );
        adResponse.setAddress( ad.getAddress() );
        adResponse.setPrice( ad.getPrice() );
        adResponse.setCategory( ad.getCategory() );
        adResponse.setCondition( ad.getCondition() );
        adResponse.setImages( imageDataListToImageResponseList( ad.getImages() ) );
        adResponse.setStatus( ad.getStatus() );
        adResponse.setCreatedAt( ad.getCreatedAt() );
        adResponse.setUpdatedAt( ad.getUpdatedAt() );

        return adResponse;
    }

    @Override
    public List<AdReportResponse> toAdReportResponse(List<AdReport> ads) {
        if ( ads == null ) {
            return null;
        }

        List<AdReportResponse> list = new ArrayList<AdReportResponse>( ads.size() );
        for ( AdReport adReport : ads ) {
            list.add( adReportToAdReportResponse( adReport ) );
        }

        return list;
    }

    @Override
    public List<AdResponse> toResponseList(List<Ad> ads) {
        if ( ads == null ) {
            return null;
        }

        List<AdResponse> list = new ArrayList<AdResponse>( ads.size() );
        for ( Ad ad : ads ) {
            list.add( toResponse( ad ) );
        }

        return list;
    }

    @Override
    public List<AdCartSummery> toCartSummeryFromFavorites(List<FavoriteAd> ads) {
        if ( ads == null ) {
            return null;
        }

        List<AdCartSummery> list = new ArrayList<AdCartSummery>( ads.size() );
        for ( FavoriteAd favoriteAd : ads ) {
            list.add( favoriteAdToAdCartSummery( favoriteAd ) );
        }

        return list;
    }

    @Override
    public List<AdCartSummery> toCartSummeryList(List<Ad> ads) {
        if ( ads == null ) {
            return null;
        }

        List<AdCartSummery> list = new ArrayList<AdCartSummery>( ads.size() );
        for ( Ad ad : ads ) {
            list.add( adToAdCartSummery( ad ) );
        }

        return list;
    }

    @Override
    public List<PendingAd> toPendingAdList(List<Ad> ads) {
        if ( ads == null ) {
            return null;
        }

        List<PendingAd> list = new ArrayList<PendingAd>( ads.size() );
        for ( Ad ad : ads ) {
            list.add( adToPendingAd( ad ) );
        }

        return list;
    }

    private String adSellerFirstname(Ad ad) {
        User seller = ad.getSeller();
        if ( seller == null ) {
            return null;
        }
        return seller.getFirstname();
    }

    private String adSellerLastname(Ad ad) {
        User seller = ad.getSeller();
        if ( seller == null ) {
            return null;
        }
        return seller.getLastname();
    }

    private Long adSellerId(Ad ad) {
        User seller = ad.getSeller();
        if ( seller == null ) {
            return null;
        }
        return seller.getId();
    }

    private String adSellerUsername(Ad ad) {
        User seller = ad.getSeller();
        if ( seller == null ) {
            return null;
        }
        return seller.getUsername();
    }

    private String adCityName(Ad ad) {
        City city = ad.getCity();
        if ( city == null ) {
            return null;
        }
        return city.getName();
    }

    protected ImageResponse imageDataToImageResponse(ImageData imageData) {
        if ( imageData == null ) {
            return null;
        }

        ImageResponse imageResponse = new ImageResponse();

        imageResponse.setId( imageData.getId() );
        imageResponse.setSortOrder( imageData.getSortOrder() );

        return imageResponse;
    }

    protected List<ImageResponse> imageDataListToImageResponseList(List<ImageData> list) {
        if ( list == null ) {
            return null;
        }

        List<ImageResponse> list1 = new ArrayList<ImageResponse>( list.size() );
        for ( ImageData imageData : list ) {
            list1.add( imageDataToImageResponse( imageData ) );
        }

        return list1;
    }

    protected AdReportResponse adReportToAdReportResponse(AdReport adReport) {
        if ( adReport == null ) {
            return null;
        }

        AdReportResponse adReportResponse = new AdReportResponse();

        return adReportResponse;
    }

    protected AdCartSummery favoriteAdToAdCartSummery(FavoriteAd favoriteAd) {
        if ( favoriteAd == null ) {
            return null;
        }

        AdCartSummery adCartSummery = new AdCartSummery();

        adCartSummery.setId( favoriteAd.getId() );

        return adCartSummery;
    }

    protected AdCartSummery adToAdCartSummery(Ad ad) {
        if ( ad == null ) {
            return null;
        }

        AdCartSummery adCartSummery = new AdCartSummery();

        adCartSummery.setId( ad.getId() );
        adCartSummery.setTitle( ad.getTitle() );
        adCartSummery.setPrice( ad.getPrice() );
        adCartSummery.setCreatedAt( ad.getCreatedAt() );
        adCartSummery.setUpdatedAt( ad.getUpdatedAt() );
        adCartSummery.setCategory( ad.getCategory() );

        return adCartSummery;
    }

    protected PendingAd adToPendingAd(Ad ad) {
        if ( ad == null ) {
            return null;
        }

        PendingAd pendingAd = new PendingAd();

        pendingAd.setId( ad.getId() );
        pendingAd.setTitle( ad.getTitle() );
        pendingAd.setCategory( ad.getCategory() );
        pendingAd.setCreatedAt( ad.getCreatedAt() );
        pendingAd.setUpdatedAt( ad.getUpdatedAt() );

        return pendingAd;
    }
}

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-17T23:16:13+0330",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
class ChatMapperImpl implements ChatMapper {

    @Override
    public MessageResponse toResponse(ChatMessage chatMessage) {
        if ( chatMessage == null ) {
            return null;
        }

        MessageResponse messageResponse = new MessageResponse();

        messageResponse.setSenderId( chatMessageSenderId( chatMessage ) );
        messageResponse.setReceiverId( chatMessageReceiverId( chatMessage ) );
        messageResponse.setMessage( chatMessage.getMessage() );
        messageResponse.setId( chatMessage.getId() );

        return messageResponse;
    }

    @Override
    public List<MessageResponse> toResponseList(List<ChatMessage> messageList) {
        if ( messageList == null ) {
            return null;
        }

        List<MessageResponse> list = new ArrayList<MessageResponse>( messageList.size() );
        for ( ChatMessage chatMessage : messageList ) {
            list.add( toResponse( chatMessage ) );
        }

        return list;
    }

    private Long chatMessageSenderId(ChatMessage chatMessage) {
        User sender = chatMessage.getSender();
        if ( sender == null ) {
            return null;
        }
        return sender.getId();
    }

    private Long chatMessageReceiverId(ChatMessage chatMessage) {
        User receiver = chatMessage.getReceiver();
        if ( receiver == null ) {
            return null;
        }
        return receiver.getId();
    }
}

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-19T20:22:50+0330",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
class ProvinceMapperImpl implements ProvinceMapper {

    @Override
    public List<ProvinceResponse> toProvinceResponse(List<City> cityList) {
        if ( cityList == null ) {
            return null;
        }

        List<ProvinceResponse> list = new ArrayList<ProvinceResponse>( cityList.size() );
        for ( City city : cityList ) {
            list.add( cityToProvinceResponse( city ) );
        }

        return list;
    }

    protected ProvinceResponse cityToProvinceResponse(City city) {
        if ( city == null ) {
            return null;
        }

        ProvinceResponse provinceResponse = new ProvinceResponse();

        provinceResponse.setId( city.getId() );
        provinceResponse.setName( city.getName() );

        return provinceResponse;
    }
}

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T14:59:50+0330",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
class UserMapperImpl implements UserMapper {

    @Override
    public List<UserResponse> toUserResponse(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( users.size() );
        for ( User user : users ) {
            list.add( userToUserResponse( user ) );
        }

        return list;
    }

    protected UserResponse userToUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId( user.getId() );
        userResponse.setUsername( user.getUsername() );
        userResponse.setFirstname( user.getFirstname() );
        userResponse.setLastname( user.getLastname() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setPhoneNumber( user.getPhoneNumber() );

        return userResponse;
    }
}