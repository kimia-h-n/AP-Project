package com.example.sales.picture;

import com.example.sales.ad.model.Ad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Data
@Entity
@Table(name = "image_data")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String type;
    private Integer sortOrder = 0;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "image", columnDefinition = "bytea", nullable = false)
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryImage = false;
}
