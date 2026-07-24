package com.example.sales.province;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

/**
 * Initializes city data from a JSON file on application startup.
 * <p>
 * If the city table already contains data, initialization is skipped.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class CityDataInitializer {

    private final ProvinceRepository cityRepository;
    private final ObjectMapper objectMapper;

    /**
     * Loads city seed data from {@code data/cities.json} into the database.
     *
     * @return command line runner that performs the initialization
     */
    @Bean
    public CommandLineRunner loadCities() {
        return args -> {
            if (cityRepository.count() > 0) {
                return;
            }

            InputStream inputStream = new ClassPathResource("data/cities.json").getInputStream();

            List<CitySeedDto> cityDtos = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<CitySeedDto>>() {}
            );

            List<City> cities = cityDtos.stream()
                    .map(dto -> City.builder()
                            .label(dto.getLabel())
                            .name(dto.getName())
                            .build())
                    .toList();

            cityRepository.saveAll(cities);
        };
    }
}
