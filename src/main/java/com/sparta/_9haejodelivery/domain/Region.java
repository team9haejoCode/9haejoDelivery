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
    @Column(name = "region_id", nullable = false)
    private UUID regionId;

    @Column(name = "zonecode", length = 10, nullable = false)
    private String zonecode;

    @Column(name = "sigungu", length = 20, nullable = false)
    private String sigungu;

    @Column(name = "bcode", length = 10, nullable = false)
    private String bcode;

    @Builder
    public Region(UUID regionId, String zonecode, String sigungu, String bcode) {
        this.regionId = regionId;
        this.zonecode = zonecode;
        this.sigungu = sigungu;
        this.bcode = bcode;
    }

    public void updateRegion(String zonecode, String sigungu, String bcode) {
        if (zonecode != null) this.zonecode = zonecode;
        if (sigungu != null) this.sigungu = sigungu;
        if (bcode != null) this.bcode = bcode;
    }
}