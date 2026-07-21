//import jakarta.persistence.*;
//import java.security.Principal;
//import java.time.Instant;
//import java.util.Collections;
//import java.util.List;
//
//@Component
//@AllArgsConstructor
//class AuthChannelInterceptor implements ChannelInterceptor {
//    private JwtService jwtService;
//    private static final Logger logger = LoggerFactory.getLogger(AuthChannelInterceptor.class);
//
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (accessor == null) {
//            return message;
//        }
//
//        StompCommand command = accessor.getCommand();
//        logger.info("Processing STOMP {} for session {}", command, accessor.getSessionId());
//
//        if (command == null || StompCommand.CONNECTED.equals(command)) {
//            return message;
//        }
//
//        try {
//            String token = accessor.getFirstNativeHeader("Authorization");
//            logger.info("Authorization header present: {}", token != null);
//
//            if (StompCommand.CONNECT.equals(command)) {
//                if (token == null || !token.startsWith("Bearer ")) {
//                    throw new AuthenticationCredentialsNotFoundException("Authorization header missing");
//                }
//
//                token = token.substring(7);
//                if (!jwtService.validateJwtToken(token)) {
//                    throw new BadCredentialsException("Invalid JWT token");
//                }
//
//                String username = jwtService.extractUsername(token);
//
//                Authentication auth = new UsernamePasswordAuthenticationToken(
//                        username, null, Collections.emptyList());
//                accessor.setUser(auth);
//
//                logger.info("Authenticated user: {}", username);
//                return message;
//            }
//
//            if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
//                Principal user = accessor.getUser();
//                if (user == null) {
//                    throw new AuthenticationCredentialsNotFoundException("User not authenticated");
//                }
//                return message;
//            }
//
//            return message;
//        } catch (Exception e) {
//            logger.error("STOMP {} command failed: {}", command, e.getMessage());
//            throw new MessageDeliveryException("Authentication failed: " + e.getMessage());
//        }
//    }
//}
//
//@Mapper(componentModel = "spring")
//interface ChatMapper {
//    @Mapping(target = "senderId", source = "sender.id")
//    @Mapping(target = "receiverId", source = "receiver.id")
//    @Mapping(target = "message", source = "message")
//    MessageResponse toResponse(ChatMessage chatMessage);
//
//    @Mapping(target = "senderId", source = "sender.id")
//    @Mapping(target = "receiverId", source = "receiver.id")
//    @Mapping(target = "message", source = "message")
//    List<MessageResponse> toResponseList(List<ChatMessage> messageList);
//}
//
//@Data
//@Entity
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//class ChatMessage {
//    @Id
//    @GeneratedValue
//    private Long id;
//    private String message;
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
//@Configuration
//@AllArgsConstructor
//@EnableWebSocketMessageBroker
//class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    private final AuthChannelInterceptor authChannelInterceptor;
//
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/queue", "/topic");
//        registry.setApplicationDestinationPrefixes("/app");
//        registry.setUserDestinationPrefix("/user");
//    }
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/chat") //any request from front end, must start here
//                .setAllowedOriginPatterns("*")
//                .withSockJS(); //in case of web socket not working, try SockJS
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(authChannelInterceptor);
//        //any request from client, first must go through authChannelInterceptor
//    }
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
//class WebSocketController {
//
//    private final WebSocketService webSocketService;
//
//
//    @MessageMapping("/sendMessage")
//    public void sendMessage(@Payload MessageRequest message, Principal principal) {
//        webSocketService.sendMessage(message, principal);
//    }
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class MessageRequest {
//    private Long receiverId;
//    private String message;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class MessageResponse {
//    private Long id;
//    private Long senderId;
//    private Long receiverId;
//    private String message;
//}
//
//@Service
//@AllArgsConstructor
//class ChatService {
//    private final ChatRepository chatRepository;
//    private final ChatMapper chatMapper;
//
//    public List<MessageResponse> fetchChat(Long senderId, Long receiverId) {
//        return chatMapper.toResponseList(chatRepository.findChatMessagesBetweenUsers(senderId, receiverId));
//    }
//
//
//    public void saveChat(ChatMessage chatMessage) {
//        chatRepository.save(chatMessage);
//    }
//}
//
//@Service
//class SearchService {
//
//    private UserRepository userRepository;
//    private UserMapper userMapper;
//
//    public List<UserInfoResponse> searchUser(String keyword) {
//        List<User> userList = userRepository.searchUsers(keyword);
//        return userMapper.toUserResponse(userList);
//    }
//}
//
//@Service
//@AllArgsConstructor
//class WebSocketService {
//
//    private final UserRepository userRepository;
//    private final ChatService chatService;
//    private final SimpMessagingTemplate simpMessagingTemplate;
//    private final ChatMapper chatMapper;
//
//    public void sendMessage(MessageRequest message, Principal principal) {
//        User sender = userRepository.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
//        User receiver = userRepository.findById(message.getReceiverId()).orElseThrow(UserNotFoundException::new);
//
//        ChatMessage chatMessage = ChatMessage.builder()
//                .sentAt(Instant.now())
//                .message(message.getMessage())
//                .sender(sender)
//                .receiver(receiver)
//                .build();
//
//        chatService.saveChat(chatMessage);
//        String receiverDestination = "/queue/messages-" + chatMessage.getReceiver().getId();
//        simpMessagingTemplate.convertAndSend(receiverDestination, chatMapper.toResponse(chatMessage));
//    }
//}