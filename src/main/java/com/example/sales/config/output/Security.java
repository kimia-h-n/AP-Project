//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//
//@Configuration
//@RequiredArgsConstructor
//class ApplicationConfig {
//
//    private final UserRepository repository;
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> repository.findByUsername(username)
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
//@Slf4j
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
//
//        log.info("JWT FILTER -> {} {}", request.getMethod(), request.getRequestURI());
//        log.info("Origin: {}", request.getHeader("Origin"));
//        log.info("Authorization: {}", request.getHeader("Authorization"));
//
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
//    public boolean validateJwtToken(String token) {
//        try {
//            extractAllClaims(token);
//            return true;
//        } catch (IllegalArgumentException ex) {
//            return false;
//        }
//    }
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
//
//    private final JwtAuthenticationFilter jwtAuthFilter;
//    private final AuthenticationProvider authenticationProvider;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        return http
//                .cors(Customizer.withDefaults())
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // ───────────────────── Public Endpoints ─────────────────────
//                        .requestMatchers("/api/v1/auth/**").permitAll()
//                        .requestMatchers("/api/v1/province").permitAll()
//                        .requestMatchers("/chat/**").permitAll()
//
//                        // ───────────────────── Public GET Endpoints ─────────────────
//                        .requestMatchers(HttpMethod.GET, "/api/v1/ads/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/filter").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/search").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/rating/avg/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
//
//                        // ───────────────────── Swagger / Docs ───────────────────────
//                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
//                        .requestMatchers("/v3/api-docs/**").permitAll()
//
//                        // ───────────────────── Authenticated Endpoints ──────────────
//                        .requestMatchers("/api/v1/favorites/**").authenticated()
//
//                        // ───────────────────── Image Mutations (need token) ─────────
//                        .requestMatchers(HttpMethod.POST, "/api/v1/ads/**").authenticated()
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/images/**").authenticated()
//                        .requestMatchers(HttpMethod.DELETE, "/api/v1/images/**").authenticated()
//
//                        // ───────────────────── Admin ───────────────────────────────
//                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/v1/admin/dashboard/stats").hasRole("ADMIN")
//
//                        // ───────────────────── Everything else ─────────────────────
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
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOriginPatterns(List.of("*"));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setExposedHeaders(List.of("Authorization"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//}