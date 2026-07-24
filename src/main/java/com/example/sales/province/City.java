package com.example.sales.province;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a city/province entry used by the application.
 * <p>
 * The entity stores both the English label and the Persian display name.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class City {

    /**
     * Unique identifier of the city record.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Unique English label for the city.
     */
    @Column(nullable = false, unique = true)
    private String label;

    /**
     * Persian name of the city.
     */
    private String name; // persion
    /**
     * Example:
     * <pre>
     * id = 1
     * label = Tehran
     * name = تهران
     * </pre>
     */
}

