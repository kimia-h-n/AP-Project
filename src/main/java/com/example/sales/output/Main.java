//import jakarta.persistence.*;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.transaction.Transactional;
//import java.io.IOException;
//import java.security.Key;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.function.Function;
//
//class AdSpecification {
//
//    public static Specification<Ad> titleContains(String keyword) {
//        return (root, query, cb) -> {
//            if (keyword == null || keyword.isBlank())
//                return cb.conjunction();
//            return cb.like(
//                    cb.lower(root.get("title")),
//                    "%" + keyword.trim() + "%");
//        };
//    }
//}
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1")
//class AdController {
//    private final AdService adService;
//
//    @PostMapping("/ads")
//    public void addAdvertisement(@RequestBody AdRequest request, Authentication authentication) {
//        String username = authentication.getName();
//        adService.addAd(request, username);
//    }
//
//
//    //sample use case: GET /api/v1/ads?status=APPROVED
//    //for now only gets active ads
//    @GetMapping("/ads")
//    public List<AdCartSummery> getAllAds(Authentication authentication) {
//        String username = extractUsernameIfLoggedIn(authentication);
//        return adService.getAllActiveAds(username);
//    }
//
//    @GetMapping("/ads/{id}")
//    public AdResponse getAd(@PathVariable Long id, Authentication authentication) {
//        String username = extractUsernameIfLoggedIn(authentication);
//        return adService.getAd(id, username);
//    }
//
//    @GetMapping("/me/ads/")
//    public List<AdResponse> getAllMyAds(Authentication authentication) {
//        String username = authentication.getName();
//        return adService.getAllMyAds(username);
//    }
//
//    @PatchMapping("/ads/{id}")
//    public void updateAd(@PathVariable Long id, @RequestBody AdUpdateRequest adRequest, Authentication authentication) {
//        String username = authentication.getName();
//        adService.updateAd(id, username, adRequest);
//    }
//
//
//    @DeleteMapping("/ads/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void removeAd(@PathVariable Long id, Authentication authentication) {
//        String username = extractUsernameIfLoggedIn(authentication);
//        adService.removeAd(id, username);
//    }
//
//    /**
//     * If token is sent, it means that the user has logged in and proper adjustments must be made
//     *
//     * @param authentication
//     * @return username or null if not logged in.
//     */
//    private String extractUsernameIfLoggedIn(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return null;
//        }
//        String username = authentication.getName();
//        return "anonymousUser".equals(username) ? null : username;
//    }
//
//    @GetMapping("/search")
//    public List<AdCartSummery> searchByTitle(@RequestParam String title, Authentication authentication) {
//        String username = extractUsernameIfLoggedIn(authentication);
//        return adService.searchByTitle(username, title);
//    }
//}
//
//@Repository
//interface AdRepository extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {
//
//    List<Ad> findAllByStatus(AdStatus status);
//
//    List<Ad> findAllBySeller(User seller);
//
//    List<Ad> findByTitleContainingIgnoreCaseAndStatus(String title, AdStatus status);
//
//    boolean existsById(Long id);
//}
//
//@Service
//@AllArgsConstructor
//class AdService {
//    private final AdRepository adRepository;
//    private final UserRepository userRepository;
//    private final FavoriteRepository favoriteRepository;
//    private final AdMapper adMapper;
//
//    public void addAd(AdRequest request, String username) {
//        User user = userRepository.findUsersByUsername(username)
//                .orElseThrow(UserNotFoundException::new);
//        Ad ad = adMapper.toEntity(request);
//        ad.setSeller(user);
//        ad.setStatus(AdStatus.PENDING);
//        adRepository.save(ad);
//    }
//
//
//    public List<AdCartSummery> getAllActiveAds(String username) {
////        List<AdCartSummery> activeAds = adMapper.toResponseList(adRepository.findAllByStatus(AdStatus.APPROVED));
////        applyFavorite(username, activeAds);
//        return adMapper.toCartSummeryList(adRepository.findAllByStatus(AdStatus.APPROVED));
//    }
//
//    private void applyFavorite(String username, List<AdResponse> responseList) {
//        if (isNotLoggedIn(username)) {
//            for (AdResponse response : responseList) {
//                response.setFavorite(false);
//                response.setMine(false);
//            }
//            return;
//        }
//        User user = userRepository.findUsersByUsername(username).orElseThrow(UserNotFoundException::new);
//        Set<Long> favAdIds = favoriteRepository.findFavoriteAdIdsByUser(user);
//        responseList.forEach(ad -> {
//            ad.setFavorite(favAdIds.contains(ad.getId()));
//            ad.setMine(ad.getSellerUsername().equals(username));
//        });
//    }
//
//    private static boolean isNotLoggedIn(String username) {
//        return username == null;
//    }
//
//    public AdResponse getAd(Long id, String username) {
//        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
//        if (isNotLoggedIn(username))
//            return adMapper.toResponse(ad);
//
//        //todo: if in future, viewing rejected ads is desired, change here
//        //todo: is it neccessary to check this here?
////        if (ad.getStatus() != AdStatus.APPROVED)
////            throw new AdViewNotAllowedException();
//
//        User user = userRepository.findUsersByUsername(username).orElseThrow(UserNotFoundException::new);
//
//        boolean isFavorite = favoriteRepository.existsFavoriteAdByUserAndAd(user, ad);
//
//        AdResponse adResponse = adMapper.toResponse(ad);
//        adResponse.setFavorite(isFavorite);
//        adResponse.setMine(username.equals(adResponse.getSellerUsername()));
//
//        return adResponse;
//    }
//
//
//    private boolean isAlreadyRemoved(Ad ad) {
//        return ad.getStatus() == AdStatus.REMOVED;
//    }
//
//    @Transactional
//    public void removeAd(Long id, String username) {
//        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
//
//        if (!isAdOwner(username, ad))
//            throw new OperationNotAllowedException();
//
//        if (isAlreadyRemoved(ad))
//            throw new AdNotRemovableException();
//
//        ad.setStatus(AdStatus.REMOVED);
//
//    }
//
//    private static boolean isAdOwner(String username, Ad ad) {
//        return ad.getSeller().getUsername().equals(username);
//    }
//
//
//    public List<AdResponse> getAllMyAds(String username) {
//        List<AdResponse> ads = adMapper.toResponseList(adRepository.findAllBySeller
//                (userRepository.findUsersByUsername(username).
//                        orElseThrow(UserNotFoundException::new)));
//        ads.forEach(ad -> ad.setMine(true));
//        return ads;
//    }
//
//    public void updateAd(Long id, String username, AdUpdateRequest request) {
//        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
//        if (!isAdOwner(username, ad))
//            throw new OperationNotAllowedException();
//
//        if (updateAdFields(request, ad))
//            adRepository.save(ad);
//    }
//
//    private boolean updateAdFields(AdUpdateRequest request, Ad ad) {
//        boolean contentChanged = false;
//
//        if (request.getTitle() != null && !request.getTitle().equals(ad.getTitle())) {
//            ad.setTitle(request.getTitle());
//            contentChanged = true;
//        }
//
//        if (request.getDescription() != null && !request.getDescription().equals(ad.getDescription())) {
//            ad.setDescription(request.getDescription());
//            contentChanged = true;
//        }
//
//        if (request.getAddress() != null && !request.getAddress().equals(ad.getAddress())) {
//            ad.setAddress(request.getAddress());
//            contentChanged = true;
//        }
//
//        if (request.getPrice() != null && request.getPrice() != ad.getPrice()) {
//            ad.setPrice(request.getPrice());
//            contentChanged = true;
//        }
//
//        if (request.getCategory() != null && request.getCategory() != ad.getCategory()) {
//            ad.setCategory(request.getCategory());
//            contentChanged = true;
//        }
//
//        if (request.getCondition() != null && request.getCondition() != ad.getCondition()) {
//            ad.setCondition(request.getCondition());
//            contentChanged = true;
//        }
//
//        if (request.getCity() != null && request.getCity() != ad.getCity()) {
//            ad.setCity(request.getCity());
//            contentChanged = true;
//        }
//
//        if (request.getImagePaths() != null && !request.getImagePaths().equals(ad.getImagePaths())) {
//            ad.setImagePaths(request.getImagePaths());
//            contentChanged = true;
//        }
//
//        if (contentChanged && ad.getStatus() == AdStatus.APPROVED) {
//            ad.setStatus(AdStatus.PENDING);
//        }
//
//        return contentChanged;
//    }
//
//    public List<AdCartSummery> searchByTitle(String username, String title) {
////        List<AdResponse> matchedAds = adMapper.toResponseList(
////                adRepository.findAll(AdSpecification.titleContains(title)));
////        applyFavorite(username, matchedAds);
//        //todo: for later search improvements such as matching with description
////         return adMapper.toCartSummeryList(adRepository.findAll(AdSpecification.titleContains(title)));
//        return adMapper.toCartSummeryList(adRepository.findByTitleContainingIgnoreCaseAndStatus(title, AdStatus.APPROVED));
//    }
//}
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "favorites")
//class FavoriteAd {
//    @Id
//    @GeneratedValue
//    private Long id;
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//    @ManyToOne
//    @JoinColumn(name = "ad_id", nullable = false)
//    private Ad ad;
//}
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("/api/v1/favorites")
//class FavoriteAdController {
//
//    private final FavoriteAdService favAdService;
//
//    @PostMapping("/{adId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void addToFavorites(@PathVariable Long adId, Authentication authentication) {
//        String username = authentication.getName();
//        favAdService.addToFavorites(adId, username);
//    }
//
//    @GetMapping
//    public List<AdCartSummery> getAllUserFavoriteAds(Authentication authentication) {
//        String username = authentication.getName();
//        return favAdService.getAllUserFavoriteAds(username);
//    }
//
//    @DeleteMapping("/{adId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteFromFavorites(@PathVariable Long adId, Authentication authentication) {
//        String username = authentication.getName();
//        favAdService.removeFromFavorites(adId, username);
//    }
//
//}
//
//@Service
//@AllArgsConstructor
//class FavoriteAdService {
//    private final FavoriteRepository favRepository;
//    private final UserRepository userRepository;
//    private final AdRepository adRepository;
//    private final AdMapper adMapper;
//
//    public void addToFavorites(Long adId, String username) {
//        RequestInfo requestInfo = getRequestInfo(adId, username);
//
//        if (isAdFavorite(requestInfo))
//            throw new AlreadyFavoriteAdException();
//
//        FavoriteAd favoriteAd = FavoriteAd.builder()
//                .user(requestInfo.user())
//                .ad(requestInfo.ad())
//                .build();
//        favRepository.save(favoriteAd);
//    }
//
//    public void removeFromFavorites(Long adId, String username) {
//        RequestInfo requestInfo = getRequestInfo(adId, username);
//
//        if (!isAdFavorite(requestInfo))
//            throw new AdNotFavoriteException();
//
//        favRepository.deleteByUserAndAd(requestInfo.user(), requestInfo.ad());
//
//    }
//
//    private boolean isAdFavorite(RequestInfo requestInfo) {
//        return favRepository.existsFavoriteAdByUserAndAd(requestInfo.user(), requestInfo.ad());
//    }
//
//    private RequestInfo getRequestInfo(Long adId, String username) {
//        User user = userRepository.findUsersByUsername(username).orElseThrow(UserNotFoundException::new);
//        Ad ad = adRepository.findById(adId).orElseThrow(AdNotFoundException::new);
//        return new RequestInfo(user, ad);
//    }
//
//    //todo: consider using Pageable if the list of favorites are too long
//    public List<AdCartSummery> getAllUserFavoriteAds(String username) {
//        if (!userRepository.existsByUsername(username))
//            throw new UserNotFoundException();
//        return adMapper.toCartSummeryFromFavorites(favRepository.getAllByUser_Username(username));
//    }
//
//    private record RequestInfo(User user, Ad ad) {
//    }
//
//
//}
//
//@Repository
//interface FavoriteRepository extends JpaRepository<FavoriteAd, Long> {
//
//    void deleteByUserAndAd(User user, Ad ad);
//
//    boolean existsFavoriteAdByUserAndAd(User user, Ad ad);
//
//    List<FavoriteAd> getAllByUser_Username(String userUsername);
//
//    @Query("select f.ad.id from FavoriteAd f where f.user = :user")
//    Set<Long> findFavoriteAdIdsByUser(@Param("user") User user);
//
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "ads")
//class Ad {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//    private String title;
//    private String description;
//    private String address;
//    private long price;
//    @Enumerated(EnumType.STRING)
//    private AdCategory category;
//    @Enumerated(EnumType.STRING)
//    private ProductCondition condition;
//    @Enumerated(EnumType.STRING)
//    private City city;
//
//    @ElementCollection
//    private List<String> imagePaths;
//
//    @Enumerated(EnumType.STRING)
//    private AdStatus status;
//    //todo: is it good practice to save user or username?
//
//    private String rejectionReason; //can be null
//    @ManyToOne
//    @JoinColumn(name = "seller_id")
//    private User seller;
//    @ManyToOne
//    @JoinColumn(name = "buyer_id")
//    private User buyer; //can be null
//
//    @CreationTimestamp
//    @Column(nullable = false, updatable = false)
//    private Instant createdAt;
//
//    @UpdateTimestamp
//    @Column(nullable = false)
//    private Instant updatedAt;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class AdCartSummery {
//    private Long id;
//
//    private String title;
//    private long price;
//    private Instant createdAt;
//    private Instant updatedAt;
//    private City city;
//}
//
//enum AdCategory {
//    PROPERTY, VEHICLE, ELECTRONIC, HOME, SERVICE,
//    PERSONAL, HOBBIT, SOCIAL, INDUSTRIAL, JOB,
//}
//
//@Mapper(componentModel = "spring")
//interface AdMapper {
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "status", ignore = true)
//    @Mapping(target = "seller", ignore = true)
//    @Mapping(target = "buyer", ignore = true)
//    @Mapping(target = "rejectionReason", ignore = true)
//    Ad toEntity(AdRequest request);
//
//    @Mapping(target = "sellerUsername", source = "seller.username")
//    @Mapping(target = "favorite", ignore = true)
//    AdResponse toResponse(Ad ad);
//
//    List<AdResponse> toResponseList(List<Ad> ads);
//
//    List<AdResponse> toResponseListFromFavorites(List<FavoriteAd> ads);
//
//    List<AdCartSummery> toCartSummeryFromFavorites(List<FavoriteAd> ads);
//
//    List<AdCartSummery> toCartSummeryList(List<Ad> ads);
//
//    List<PendingAd> toPendingAdList(List<Ad> ads);
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//
//class AdRequest {
//    private String title;
//    private String description;
//    private String address;
//    private long price;
//    private AdCategory category;
//    private ProductCondition condition;
//    private City city;
//    private List<String> imagePaths;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class AdResponse {
//    private Long id;
//    private String title;
//    private String description;
//    private String address;
//    private long price;
//    private AdCategory category;
//    private ProductCondition condition;
//    private City city;
//    private List<String> imagePaths;
//    private AdStatus status;
//    private String sellerUsername;
//    private boolean isFavorite;
//    private boolean isMine;
//    private Instant createdAt;
//    private Instant updatedAt;
//}
//
//enum AdStatus {
//    PENDING,
//    REJECTED,
//    REMOVED,
//    APPROVED,
//    SOLD;
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//
//class AdUpdateRequest {
//    private Long id;
//    private String title;
//    private String description;
//    private String address;
//    private Long price;
//    private AdCategory category;
//    private ProductCondition condition;
//    private City city;
//    private List<String> imagePaths;
//}
//
//enum City {
//    TEHRAN, KARAJ, MASHHAD ,ISFAHAN,TABRIZ
//}
//
//enum AdModerationChoice {
//    APPROVE, REJECT
//}
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//class AdModerationRequest {
//    private AdModerationChoice choice;
//    private String rejectReason;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class PendingAd {
//    private Long id;
//    private String title;
//    private AdCategory category;
//    private String sellerFirstName;
//    private String sellerLastName;
//    private City city;
//    private Instant createdAt;
//    private Instant updatedAt;
//}
//
//enum ProductCondition {
//    NEW, ALMOST_NEW, USED
//}
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/admin")
//class AdminController {
//    private final AdminService adminService;
//
//    @GetMapping("/users")
//    public List<UserResponse> getAllUsers() {
//        return adminService.getAllUsers();
//    }
//
//    @PostMapping("/users/block/{id}")
//    public void blockUser(@PathVariable Long id) {
//        adminService.blockUser(id);
//    }
//
//    @PostMapping("/users/unblock/{id}")
//    public void unblockUser(@PathVariable Long id) {
//        adminService.unblockUser(id);
//    }
//
//    //only admin can access
//    @PostMapping("/ads/{id}/moderation")
//    public void moderateAd(@PathVariable Long id, @RequestBody AdModerationRequest moderationRequest) {
//        adminService.moderateAd(id, moderationRequest);
//    }
//
//
//    @GetMapping("/ads/moderation")
//    public List<PendingAd> getPendingAds() {
//        return adminService.getAllPendingAds();
//    }
//}
//
//@Configuration
//@RequiredArgsConstructor
//class AdminInitializer {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Value("${app.admin.username}")
//    private String adminUsername;
//
//    @Value("${app.admin.password}")
//    private String adminPassword;
//
//    @Bean
//    public CommandLineRunner createAdmin() {
//        return args -> {
//            if (userRepository.existsByUsername(adminUsername))
//                return;
//
//            User admin = User.builder()
//                    .username(adminUsername)
//                    .password(passwordEncoder.encode(adminPassword))
//                    .email(null)
//                    .phoneNumber(null)
//                    .firstname(null)
//                    .lastname(null)
//                    .role(Role.ADMIN)
//                    .enable(true)
//                    .build();
//            userRepository.save(admin);
//        };
//    }
//}
//
//@Service
//@AllArgsConstructor
//class AdminService {
//
//    private final UserRepository userRepository;
//    private final UserMapper userMapper;
//    private final AdRepository adRepository;
//    private final AdMapper adMapper;
//
//    public List<UserResponse> getAllUsers() {
//        return userMapper.toUserResponse(userRepository.findAll());
//    }
//
//    public void blockUser(Long id) {
//
//        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
//        if (!user.isEnable())
//            throw new AlreadyBlockedException();
//        user.setEnable(false);
//
//    }
//
//    public void unblockUser(Long id) {
//        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
//
//        if (user.isEnable())
//            throw new UserAlreadyEnabled();
//        user.setEnable(true);
//    }
//
//
//    public void moderateAd(Long id, AdModerationRequest moderationRequest) {
//
//        Ad ad = adRepository.findById(id).orElseThrow(AdNotFoundException::new);
//
//        switch (moderationRequest.getChoice()) {
//            case APPROVE -> {
//                ad.setStatus(AdStatus.APPROVED);
//                ad.setRejectionReason(null);
//            }
//            case REJECT -> {
//                ad.setStatus(AdStatus.REJECTED);
//                ad.setRejectionReason(moderationRequest.getRejectReason());
//            }
//        }
//        adRepository.save(ad);
//    }
//
//    public List<PendingAd> getAllPendingAds() {
//        return adMapper.toPendingAdList(adRepository.findAllByStatus(AdStatus.PENDING));
//    }
//
//}
//
//@SpringBootApplication
//class Application {
//
//	public static void main(String[] args) {
//		SpringApplication.run(Application.class, args);
//	}
//
//}
//
//@RestController
//@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
//class AuthenticationController {
//
//    private final AuthenticationService authService;
//
//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse> register(
//            @RequestBody RegisterRequest request){
//        return ResponseEntity.ok(authService.register(request));
//    }
//
//    @PostMapping("/authenticate")
//    public ResponseEntity<AuthenticationResponse> authenticate(
//            @RequestBody AuthenticationRequest request){
//        return ResponseEntity.ok(authService.authenticate(request));
//
//    }
//
//
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//class AuthenticationRequest {
//    String username;
//    String password;
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//class AuthenticationResponse {
//    private String token;
//}
//
//@Service
//@RequiredArgsConstructor
//class AuthenticationService {
//
//    private final UserRepository repository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authManager;
//
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        try {
//            authManager.authenticate(new UsernamePasswordAuthenticationToken(
//                    request.getUsername(),
//                    request.getPassword()
//            ));
//        } catch (AuthenticationException e) {
//            throw new InvalidUsernameOrPassword();
//        }
//        var user = repository.findUsersByUsername(request.getUsername()).orElseThrow(UserNotFoundException::new);
//        var jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
//    }
//
//    public AuthenticationResponse register(RegisterRequest request) {
//        String username = request.getUsername(), email = request.getEmail(), phoneNumber = request.getPhoneNumber();
//        if (repository.existsByUsername(username))
//            throw new DuplicateUsernameException();
//        if (repository.existsByPhoneNumber(phoneNumber))
//            throw new DuplicatePhoneNumberException();
//        if (repository.existsByEmail(email))
//            throw new DuplicateEmailException();
//
//        var user = User.builder()
//                .username(username)
//                .password(passwordEncoder.encode(request.getPassword()))//save encoded password
//                .firstname(request.getFirstname())
//                .lastname(request.getLastname())
//                .email(email)
//                .phoneNumber(phoneNumber)
//                .role(Role.USER)
//                .enable(true)
//                .build();
//        repository.save(user);
//        var jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
//    }
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//class RegisterRequest {
//    private String username;
//    private String password;
//    private String firstname;
//    private String lastname;
//    private String phoneNumber;
//    private String email;
//}
//
//@Mapper(componentModel = "spring")
//interface ChatMapper {
//
//    public List<ChatResponse> toResponse(List<ChatMessage> messageList);
//}
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//class ChatMessage {
//    @Id
//    @GeneratedValue
//    private Long id;
//    private String msg;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "sender_id")
//    private User sender;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "receiver_id")
//    private User receiver;
//
//    private Instant sentAt;
//}
//
//@Repository
//
//
//interface ChatRepository extends JpaRepository<ChatMessage, Long> {
//
//    @Query("""
//            SELECT m
//            FROM ChatMessage m
//            WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id)
//               OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id)
//            ORDER BY m.sentAt ASC
//            """)
//    List<ChatMessage> findChatMessagesBetweenUsers(
//            @Param("user1Id") Long user1Id,
//            @Param("user2Id") Long user2Id
//    );
//
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class ChatResponse {
//    private Long id;
//    private Long senderId;
//    private Long receiverId;
//    private String message;
//}
//
//@Controller
//@NoArgsConstructor
//@AllArgsConstructor
//
//@RequestMapping("api/v1")
//class ChatController {
//    private ChatService chatService;
//
//    @GetMapping("/conversations")
//    public ResponseEntity<?> fetchChat(@RequestParam Long senderId, @RequestParam Long receiverId){
//        return ResponseEntity.ok(
//                chatService.fetchChat(senderId, receiverId));
//    }
//}
//
//@RequestMapping("/api/v1/")
//@RestController
//class SearchController {
//
//    private SearchService searchService;
//
//    @GetMapping("search-user")
//    public ResponseEntity<?> searchForUser(@RequestParam String name){
//        return ResponseEntity.ok(searchService.searchUser(name));
//    }
//
//}
//
//@Controller
//@RequestMapping("/api/v1/")
//@AllArgsConstructor
//@NoArgsConstructor
//class WebSocketController {
//
//}
//
//@Service
//class ChatService {
//    private ChatRepository chatRepository;
//    private ChatMapper chatMapper;
//
//    public List<ChatResponse> fetchChat(Long senderId, Long receiverId) {
//        chatMapper.toResponse(chatRepository.findChatMessagesBetweenUsers(senderId, receiverId));
//    }
//}
//
//@Service
//class SearchService {
//
//    private UserRepository userRepository;
//    private UserMapper userMapper;
//
//    public List<UserResponse> searchUser(String keyword) {
//        List<User> userList = userRepository.findByFirstnameContainingIgnoreCaseOrLastnameIgnoreCaseOrUsernameIgnoreCase(keyword);
//        return userMapper.toUserResponse(userList);
//    }
//}
//
//@Service
//class WebSocketService {
//}
//
//@Configuration
//@RequiredArgsConstructor
//class ApplicationConfig {
//
//    private final UserRepository repository;
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> repository.findUsersByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(
//                        "User not found"
//                ));
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService());
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//}
//
//@Component
//@RequiredArgsConstructor
//class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
//        final String authHeader = request.getHeader("Authorization");
//        final String jwtToken;
//        final String username;
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        //bearer : len is 7
//        jwtToken = authHeader.substring(7);
//        username = jwtService.extractUsername(jwtToken);
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//            if (jwtService.isTokenValid(jwtToken, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                        );
//                authToken.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(
//                                request
//                        )
//                );
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}
//
//@Service
//class JwtService {
//
//    @Value("${application.security.jwt.secret-key}")
//    private String secretKey;
//
//    @Value("${application.security.jwt.expiration}")
//    private long jwtExpiration;
//
//    public String extractUsername(String jwtToken) {
//        return extractClaims(jwtToken, Claims::getSubject);
//    }
//
//    public String generateToken(Map<String, Object> extractClaims,
//                                UserDetails userDetails) {
//        return Jwts.builder()
//                .setClaims(extractClaims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String generateToken(UserDetails userDetails) {
//        return generateToken(new HashMap<>(), userDetails);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder().
//                setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) &&
//                !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaims(token, Claims::getExpiration);
//    }
//
//    private Key getSignInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//
//    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//}
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//class SecurityConfiguration {
//    private final JwtAuthenticationFilter jwtAuthFilter;
//
//    private final AuthenticationProvider authenticationProvider;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        return http
//                .csrf(csrf -> csrf.disable())
//
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/auth/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/ads/**").permitAll()
//                        .requestMatchers("/api/v1/favorites/**").authenticated()
//                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/swagger-ui/**").permitAll()
//                        .requestMatchers("/v3/api-docs/**").permitAll()
//                        .requestMatchers("/swagger-ui.html").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/search").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
//
//}
//
//@RestController
//@RequestMapping("/api/v1/demo-controller")
//class DemoController {
//    @GetMapping
//    public ResponseEntity<String> sayHello() {
//        return ResponseEntity.ok("Hello from secured end point");
//    }
//
//}
//
//class AdNotFavoriteException extends BaseException {
//    public AdNotFavoriteException() {
//        super("Ad not favorite", ErrorCode.AD_NOT_FAVORITE);
//    }
//}
//
//class AdNotFoundException extends BaseException {
//    public AdNotFoundException() {
//        super("Ad is not found!", ErrorCode.AD_NOT_FOUND);
//    }
//}
//
//class AdNotRemovableException extends BaseException {
//    public AdNotRemovableException() {
//        super("Ad not removable", ErrorCode.AD_NOT_REMOVABLE);
//    }
//}
//
//class AdViewNotAllowedException extends BaseException {
//    public AdViewNotAllowedException() {
//        super("Ad view isn't allowed", ErrorCode.AD_VIEW_NOT_ALLOWED);
//
//    }
//}
//
//class AlreadyBlockedException extends BaseException {
//
//    public AlreadyBlockedException() {
//        super("User is already blocked!", ErrorCode.USER_ALREADY_BLOCKED);
//    }
//}
//
//class AlreadyFavoriteAdException extends BaseException {
//    public AlreadyFavoriteAdException() {
//        super("Ad is already favorite", ErrorCode.AD_ALREADY_FAVORITE);
//    }
//}
//
//abstract class BaseException extends RuntimeException {
//    private final ErrorCode errorCode;
//
//    public BaseException(String message, ErrorCode errorCode) {
//        super(message);
//        this.errorCode = errorCode;
//    }
//
//    public ErrorCode getErrorCode() {
//        return errorCode;
//    }
//}
//
//class DuplicateEmailException extends BaseException {
//    public DuplicateEmailException() {
//        super("Email already exists", ErrorCode.DUPLICATE_EMAIL);
//    }
//}
//
//class DuplicatePhoneNumberException extends BaseException {
//    public DuplicatePhoneNumberException() {
//        super("Phone number already exists.", ErrorCode.DUPLICATE_PHONE_NUMBER);
//    }
//}
//
//class DuplicateUsernameException extends BaseException {
//    public DuplicateUsernameException() {
//        super("Username already exists.", ErrorCode.DUPLICATE_USERNAME);
//    }
//}
//
//enum ErrorCode {
//    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND),
//    INVALID_PASSWORD_OR_USERNAME("INVALID_PASSWORD_OR_USERNAME", HttpStatus.UNAUTHORIZED),
//    DUPLICATE_EMAIL("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT),
//    DUPLICATE_PHONE_NUMBER("PHONE_NUMBER_ALREADY_EXISTS", HttpStatus.CONFLICT),
//    DUPLICATE_USERNAME("USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT),
//
//    USER_ALREADY_BLOCKED("USER_ALREADY_BLOCKED", HttpStatus.CONFLICT),
//    USER_ALREADY_ENABLED("USER_ALREADY_ENABLED", HttpStatus.CONFLICT),
//    AD_NOT_FOUND("AD_NOT_FOUND", HttpStatus.NOT_FOUND),
//    AD_ALREADY_FAVORITE("AD_ALREADY_FAVORITE", HttpStatus.CONFLICT),
//    AD_NOT_FAVORITE("AD_NOT_FAVORITE", HttpStatus.BAD_REQUEST),
//    AD_NOT_REMOVABLE("AD_NOT_REMOVABLE", HttpStatus.CONFLICT),
//
//    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", HttpStatus.FORBIDDEN),
//    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
//    AD_VIEW_NOT_ALLOWED("NOT_ALLOWED_AD_VIEW", HttpStatus.FORBIDDEN);
//
//    private final String label;
//    private final HttpStatus status;
//
//    ErrorCode(String label, HttpStatus status) {
//        this.label = label;
//        this.status = status;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//
//    public HttpStatus getStatus() {
//        return status;
//    }
//}
//
//record ErrorResponse(
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
//        LocalDateTime timestamp,
//        int status,
//        String message,
//        String error) {
//}
//
//@RestControllerAdvice
//class GlobalExceptionHandler {
//
//    @ExceptionHandler(BaseException.class)
//    public ResponseEntity<ErrorResponse> handleError(BaseException ex) {
//        ErrorCode errorCode = ex.getErrorCode();
//
//        ErrorResponse errorResponse = new ErrorResponse(
//                LocalDateTime.now(),
//                errorCode.getStatus().value(),
//                ex.getMessage(),
//                errorCode.getLabel()
//        );
//
//        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
//    }
//}
//
//class InvalidUsernameOrPassword extends BaseException {
//
//    public InvalidUsernameOrPassword() {
//        super("Password or username is invalid", ErrorCode.INVALID_PASSWORD_OR_USERNAME);
//    }
//}
//
//class OperationNotAllowedException extends BaseException {
//    public OperationNotAllowedException() {
//        super("Operation not allowed", ErrorCode.OPERATION_NOT_ALLOWED);
//    }
//}
//
//class UserAlreadyEnabled extends BaseException {
//    public UserAlreadyEnabled() {
//        super("User is already enabled!", ErrorCode.USER_ALREADY_ENABLED);
//    }
//}
//
//class UserNotFoundException extends BaseException {
//    public UserNotFoundException() {
//        super("User is not found!", ErrorCode.USER_NOT_FOUND);
//    }
//}
//
//interface UserRepository extends JpaRepository<User, Long> {
//
//    Optional<User> findUsersByUsername(String username);
//
//    boolean existsByUsername(String username);
//
//    boolean existsByPhoneNumber(String phoneNumber);
//
//    boolean existsByEmail(String email);
//
//    List<User> findByFirstnameContainingIgnoreCaseOrLastnameIgnoreCaseOrUsernameIgnoreCase(String keyword);
//
//}
//
//enum Role {
//    USER,
//    ADMIN
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "users", uniqueConstraints = {
//        @UniqueConstraint(columnNames = "username"),
//        @UniqueConstraint(columnNames = "phone_number"),
//        @UniqueConstraint(columnNames = "email")
//})
//class User implements UserDetails {
//    @Id
//    @GeneratedValue
//    private Long id;
//    private String username;
//    private String firstname;
//    private String lastname;
//    private String email;
//    @Column(name = "phone_number")
//    private String phoneNumber;
//    private String password;
//    @Enumerated(EnumType.STRING)
//    private Role role;
//    private boolean enable;
//
//    /*
//      Spring:
//      Role: USER
//      Authority: ROLE_USER
//      then for checking, .hasRole("ADMIN")
//      or hasAuthority("ROLE_ADMIN")
//     */
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
//    }
//
//    @Override
//    public String getUsername() {
//        return this.username;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return this.enable;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//}
//
//@Mapper(componentModel = "spring")
//interface UserMapper {
//    List<UserResponse> toUserResponse(List<User> users);
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class UserResponse {
//    private Long id;
//    private String username;
//    private String firstname;
//    private String lastname;
//    private String email;
//    private String phoneNumber;
//    private boolean enable;
//}