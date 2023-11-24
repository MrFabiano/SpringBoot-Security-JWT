//package com.microservices.apis.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Bean
//    public CorsFilter corsFilter(){
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
//        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin","Access-Control-Allow-Origin","Content-Type",
//                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
//                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
//        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept","Authorization",
//                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
//        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST","PUT","DELETE","OPTIONS"));
//        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsFilter(urlBasedCorsConfigurationSource);
//    }
//}
//


