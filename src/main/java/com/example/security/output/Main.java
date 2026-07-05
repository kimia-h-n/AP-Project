//import jakarta.persistence.*;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.security.Key;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.function.Function;
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
//}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//class AuthenticationRequest {
//    String username;
//    String password;
//
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
//        authManager.authenticate(new UsernamePasswordAuthenticationToken(
//                request.getUsername(),
//                request.getPassword()
//        ));
//        //if we get to this line, it means the password and username are authenticated and correct.
//        var user = repository.findUsersByUsername(request.getUsername()).orElseThrow(); //todo: in advance levels, you may want to pass the costume expcetiona nd handle it!
//        var jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
//    }
//
//    public AuthenticationResponse register(RegisterRequest request) {
//        var user = User.builder()
//                .username(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))//save encoded password
//                .firstname(request.getFirstname())
//                .lastname(request.getLastname())
//                .email(request.getEmail())
//                .phoneNumber(request.getPhoneNumber())
//                .role(Role.USER)
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
//    //data access object to fetch user deatils and password and ...
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
//@RequiredArgsConstructor //using any final field we declare here
//class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
//        //first check if token exsits, pass the token within the heather
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
//        //the second part of if, is to show if the user has already been authenticated, it should be moved to service right awya
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//            //validate if the token is still valid
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
//            filterChain.doFilter(request, response);
//
//        }
//    }
//}
//
//@Service //managed bean
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
//    //apply filter
//    //white list means a list where they don't need any authorization or token
//   //For example when we want to sign in or sign up
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        return http
//                // csrf off (برای API)
//                .csrf(csrf -> csrf.disable())
//
//                // قوانین دسترسی
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/auth/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//
//                // stateless بودن
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//
//                // provider سفارشی
//                .authenticationProvider(authenticationProvider)
//
//                // JWT filter قبل از UsernamePasswordAuthenticationFilter
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//
//                // پایان DSL
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
////we defined id as interger, that's the paramter for this repository
//interface UserRepository extends JpaRepository<User, Integer> {
//
//    //username is unique so we want to check with that
//    Optional<User> findUsersByUsername(String username);
//
//}
//
//@SpringBootApplication
//class SecurityApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(SecurityApplication.class, args);
//	}
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
////@Entity
//@Table(name = "users", uniqueConstraints = {
//        @UniqueConstraint(columnNames = "username"),
//        @UniqueConstraint(columnNames = "phone_number")
//})
//class User implements UserDetails {
//    @Id
//    @GeneratedValue
//    private Integer id;
//    @Column(unique = true)
//    private String username;
//    private String firstname;
//    private String lastname;
//    private String email;
//    @Column(name = "phone_number", unique = true)
//    private String phoneNumber;
//    private String password;
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
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
//        return email;
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
//        return true;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//}