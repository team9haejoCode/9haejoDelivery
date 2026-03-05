package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "P_REGION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region extends BaseEntity {

    @Id
    @Column(name = "region_id", columnDefinition = "uuid", nullable = false)
    private UUID regionId;

    @Column(name = "city", length = 20, nullable = false)
    private String city;

    @Column(name = "district", length = 20, nullable = false)
    private String district;

    @Column(name = "neighborhood", length = 20, nullable = false)
    private String neighborhood;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Builder
    public Region(UUID regionId, String city, String district, String neighborhood, String postalCode) {
        this.regionId = regionId;
        this.city = city;
        this.district = district;
        this.neighborhood = neighborhood;
        this.postalCode = postalCode;
    }

    public void updateRegion(String city, String district, String neighborhood, String postalCode) {
        if (city != null) this.city = city;
        if (district != null) this.district = district;
        if (neighborhood != null) this.neighborhood = neighborhood;
        if (postalCode != null) this.postalCode = postalCode;
    }
}