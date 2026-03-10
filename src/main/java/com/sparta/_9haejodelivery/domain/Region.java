package com.sparta._9haejodelivery.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "P_REGION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region extends BaseEntity {

    @Id
    @Column(name = "bcode_id", length = 11, nullable = false, updatable = false)
    private String bcodeId;

    @Column(name = "sigungu", length = 20, nullable = false)
    private String sigungu;

    @Column(name = "bcode", length = 10, nullable = false)
    private String bcode;

    @Builder
    public Region(String bcodeId, String sigungu, String bcode) {
        this.bcodeId = bcodeId;
        this.sigungu = sigungu;
        this.bcode = bcode;
    }

    public void updateRegion(String sigungu, String bcode) {
        if (sigungu != null) this.sigungu = sigungu;
        if (bcode != null) this.bcode = bcode;
    }
}