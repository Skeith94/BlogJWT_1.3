package Blog.security;




import Blog.filtrer.CustomAuthorizationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import static Blog.security.MyCustomAutMenager.myCustomAuthenticationManager;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;





@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/login").allowedMethods("POST").allowedOrigins("http://localhost:4200").allowCredentials(true);
                registry.addMapping("/blog/home").allowedMethods("GET").allowedOrigins("http://localhost:4200");
            }
        };
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy((STATELESS));
        http.authorizeRequests().antMatchers("/api/login/**","/api/token/refresh","/api/user/registration/**","/blog/home","/blog/topic").permitAll();
        http.authorizeRequests().antMatchers( "/api/user/blog/post/**").hasAnyRole("SUPER_ADMIN","ADMIN","SCRITTORE");
        http.authorizeRequests().antMatchers( "/blog/topic/comment/**").hasAnyRole("SUPER_ADMIN","ADMIN","SCRITTORE","USER");
        http.authorizeRequests().antMatchers( "/api/user/guest/sendagain").hasRole("GUEST");
        http.authorizeRequests().antMatchers( "/api/role/**").hasRole("SUPER_ADMIN");
        http.authorizeRequests().antMatchers( "/api/user","/blog/topic/delete/**").hasAnyRole("SUPER_ADMIN","ADMIN");
        http.authorizeRequests().anyRequest().authenticated();
        http.apply(myCustomAuthenticationManager());
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }




}






