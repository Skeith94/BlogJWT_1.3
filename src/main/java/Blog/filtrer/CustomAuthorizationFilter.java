package Blog.filtrer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static Blog.loginapplication.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/login")|| request.getServletPath().equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
                try {
                    String userFingerprint = null;
                    if (request.getCookies() != null && request.getCookies().length > 0) {
                        List<Cookie> cookies = Arrays.stream(request.getCookies()).collect(Collectors.toList());
                        Optional<Cookie> cookie = cookies.stream().filter(c -> "__Secure-Fgp"
                                .equals(c.getName())).findFirst();
                        if (cookie.isPresent()) {
                            userFingerprint = cookie.get().getValue();
                        }
                    }

                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] userFingerprintDigest = digest.digest(userFingerprint.getBytes("utf-8"));
                    String userFingerprintHash = DatatypeConverter.printHexBinary(userFingerprintDigest);
                    String token = authorizationHeader.substring("Bearer".length());
                    token = tokenCipher.decipherToken( token,keyCiphering);
                    JWTVerifier verifier = JWT.require(Algorithm.HMAC256(keyHMAC))
                            .withClaim("userFingerprint", userFingerprintHash)
                            .build();
                    DecodedJWT decodeJWT = verifier.verify(token);
                    String username=decodeJWT.getSubject();
                    String[] roles=decodeJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
                    stream(roles).forEach(role-> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(username,null,authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request,response);
                } catch (Exception exception) {
                    log.error("errore log in:{}",exception.getMessage());
                    response.setHeader("error",exception.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    // response.sendError(FORBIDDEN.value());
                    Map<String,String> error=new HashMap<>();
                    error.put("messaggio_errore",exception.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),error);
                }
            }else{
                filterChain.doFilter(request,response);
            }
        }
    }
}

