package Blog.filtrer;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static Blog.loginapplication.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final SecureRandom secureRandom = new SecureRandom();


    private final AuthenticationManager authenticationManager;


    static final String FILTER_REQUEST_MATCHER = "/api/login";

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        super(FILTER_REQUEST_MATCHER, authenticationManager);
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        UserLogin authenticationRequest =
                new ObjectMapper().readValue(request.getInputStream(), UserLogin.class);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword()
        );

        Authentication authenticate = authenticationManager.authenticate(authentication);
        return authenticate;
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        byte[] randomFgp = new byte[50];
        secureRandom.nextBytes(randomFgp);
        String userFingerprint = DatatypeConverter.printHexBinary(randomFgp);
        String fingerprintCookie = userFingerprint;
        Cookie cookie = new Cookie("__Secure-Fgp", fingerprintCookie);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }



        byte[] userFingerprintDigest = digest.digest(userFingerprint.getBytes(StandardCharsets.UTF_8));
        String userFingerprintHash = DatatypeConverter.printHexBinary(userFingerprintDigest);

        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        c.add(Calendar.MINUTE, 10);
        Date expirationDate = c.getTime();
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("typ", "JWT");

        User user = (User) authentication.getPrincipal();
        String UrlTop = request.getRequestURL().toString();

        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expirationDate)
                .withIssuer(UrlTop)
                .withIssuedAt(now)
                .withNotBefore(now)
                .withClaim("userFingerprint", userFingerprintHash)
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withHeader(headerClaims)
                .sign(Algorithm.HMAC256(keyHMAC));


        c.add(Calendar.MINUTE, 30);
        expirationDate = c.getTime();

        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expirationDate)
                .withClaim("userFingerprint", userFingerprintHash)
                .withIssuer(request.getRequestURL().toString())
                .sign(Algorithm.HMAC256(keyHMAC));


        log.info("login token non cifrato {}",access_token);
        log.info("refresh token non cifrato {}",refresh_token);
        try {
            access_token = tokenCipher.cipherToken(access_token,keyCiphering);
            refresh_token=  tokenCipher.cipherToken(refresh_token,keyCiphering);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        List<String> ruoli=user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        tokens.put("roles",ruoli.toString());
        response.setContentType(APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }



}