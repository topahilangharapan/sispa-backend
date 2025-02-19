package radiant.sispa.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import radiant.sispa.backend.security.jwt.JwtTokenFilter;

import java.io.IOException;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/user/add").hasAuthority("admin".toUpperCase())
                        .requestMatchers("/api/role/add").hasAuthority("admin".toUpperCase())
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                        .accessDeniedHandler(
                                new AccessDeniedHandler() {
                                    @Override
                                    public void handle(HttpServletRequest request, HttpServletResponse response,
                                                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                        response.getWriter().write("Anda Tidak Memiliki Akses ke Endpoint Ini!");
                                    }
                                }
                        )
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception{
        http.csrf(Customizer.withDefaults())
                .authorizeHttpRequests (requests -> requests
                        .requestMatchers (new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers (new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/pekerja/**")).hasAuthority("Human Resource")
                        .requestMatchers(new AntPathRequestMatcher("/developer/**")).hasAuthority("Human Resource")
                        .requestMatchers(new AntPathRequestMatcher("/proyek/**")).hasAuthority("Project Manager")
                        .requestMatchers(new AntPathRequestMatcher("/roman/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/")
                )
                .logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher( "/logout"))
                        .logoutSuccessUrl("/login"))
                .exceptionHandling(e -> e
                        .accessDeniedHandler(
                                new AccessDeniedHandler() {
                                    @Override
                                    public void handle(HttpServletRequest request, HttpServletResponse response,
                                                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
                                        response.sendRedirect("/role-denied");
                                    }
                                }
                        )
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(encoder());

        return authenticationManagerBuilder.build();
    }
}
