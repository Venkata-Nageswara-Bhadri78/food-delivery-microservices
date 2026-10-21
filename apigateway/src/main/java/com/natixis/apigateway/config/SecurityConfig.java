package com.natixis.apigateway.config;

import com.natixis.apigateway.enums.UserType;
import com.natixis.apigateway.security.JwtAuthenticationWebFilter;
import com.natixis.apigateway.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtService jwtService) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .cors(Customizer.withDefaults())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/api/auth", "/api/auth/**").permitAll()
                        
                        .pathMatchers(HttpMethod.POST, "/api/restaurant", "/api/restaurant/**").hasAuthority("ROLE_" + UserType.ADMIN.name())
                        .pathMatchers(HttpMethod.DELETE, "/api/restaurant", "/api/restaurant/**").hasAuthority("ROLE_" + UserType.ADMIN.name())
                        .pathMatchers("/api/restaurant", "/api/restaurant/**").permitAll()
                        
                        .pathMatchers(HttpMethod.POST, "/api/customer/customer").permitAll()
                        .pathMatchers("/api/customer", "/api/customer/**")
                        .hasAnyAuthority("ROLE_" + UserType.CUSTOMER.name(), "ROLE_" + UserType.ADMIN.name(), "ROLE_" + UserType.AGENT.name())
                        
                        .pathMatchers(HttpMethod.POST, "/api/agent/agent").permitAll()
                        .pathMatchers("/api/agent", "/api/agent/**")
                        .hasAnyAuthority("ROLE_" + UserType.AGENT.name(), "ROLE_" + UserType.ADMIN.name())

                        .pathMatchers(HttpMethod.POST, "/api/admin/admin").permitAll()
                        .pathMatchers("/api/admin", "/api/admin/**")
                        .hasAuthority("ROLE_" + UserType.ADMIN.name())
                                            
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> writeJson(
                                exchange.getResponse(),
                                HttpStatus.UNAUTHORIZED,
                                "Unauthorized access - No token provided or invalid token"
                        ))
                        .accessDeniedHandler((exchange, denied) -> writeJson(
                                exchange.getResponse(),
                                HttpStatus.FORBIDDEN,
                                "Access Denied - You do not have permission to access this resource"
                        ))
                )
                .addFilterAt(new JwtAuthenticationWebFilter(jwtService), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private Mono<Void> writeJson(org.springframework.http.server.reactive.ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"success\":false,\"message\":\"" + message + "\",\"data\":null}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
